package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationParams;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.storage.CompilationRepository;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.EventShortDto;
import ru.practicum.ewm.events.dto.parameters.MappingEventParameters;
import ru.practicum.ewm.events.mapper.EventMapper;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.events.views.EventsViewsGetter;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventsRepository eventsRepository;
    private final EventsViewsGetter eventsViewsGetter;

    @Override
    public List<CompilationDto> getCompilations(CompilationParams compilationParams) {
        if (compilationParams.getFrom() < 0 || compilationParams.getSize() <= 0) {
            throw new ValidationException("Invalid pagination parameters");
        }

        int pageNumber = (int) (compilationParams.getFrom() / compilationParams.getSize());
        int pageSize = compilationParams.getSize().intValue();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Compilation> compilationPage = compilationParams.getPinned() != null
                ? compilationRepository.findByPinned(compilationParams.getPinned(), pageable)
                : compilationRepository.findAll(pageable);

        log.info("Fetched compilations: from={}, size={}, pinned={}",
                compilationParams.getFrom(), compilationParams.getSize(), compilationParams.getPinned());

        return compilationPage.stream()
                .map(compilation -> CompilationMapper
                        .toCompilationDto(compilation, createEventShortDtoList(compilation.getEvents())))
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = checkAndGetCompilation(compId);
        log.info("Get compilations with id = {}", compId);
        return CompilationMapper.toCompilationDto(compilation, createEventShortDtoList(compilation.getEvents()));
    }

    @Override
    @Transactional
    public CompilationDto createdCompilation(NewCompilationDto newCompilationDto) {
        if (newCompilationDto.getTitle() == null || newCompilationDto.getTitle().isBlank()) {
            throw new ValidationException("Compilation title cannot be null or blank");
        }

        if (newCompilationDto.getEvents() == null) {
            newCompilationDto.setEvents(new ArrayList<>());
        }

        List<Event> events = eventsRepository.findAllById(newCompilationDto.getEvents());

        if (events.size() != newCompilationDto.getEvents().size()) {
            throw new NotFoundException("Not all received events were found");
        }

        Compilation compilation = CompilationMapper.toCompilationEntity(newCompilationDto, events);
        Compilation savedCompilation = compilationRepository.save(compilation);

        log.info("Created compilation id={} with title '{}'", savedCompilation.getId(), savedCompilation.getTitle());

        return CompilationMapper
                .toCompilationDto(savedCompilation, createEventShortDtoList(savedCompilation.getEvents()));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException(String.format("Compilation with id %d not found", compId));
        }

        compilationRepository.deleteById(compId);
        log.info("Deleted compilation id={}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(CompilationParams compilationParams) {
        NewCompilationDto newCompilationDto = compilationParams.getNewCompilationDto();

        if (newCompilationDto.getEvents() == null) {
            newCompilationDto.setEvents(new ArrayList<>());
        }

        Compilation compilation = checkAndGetCompilation(compilationParams.getCompId());

        if (newCompilationDto.getTitle() != null && !newCompilationDto.getTitle().isBlank()) {
            compilation.setTitle(newCompilationDto.getTitle());
        }

        compilation.setPinned(newCompilationDto.isPinned());

        if (!newCompilationDto.getEvents().isEmpty()) {
            List<Event> events = eventsRepository.findAllById(newCompilationDto.getEvents());
            if (events.size() != newCompilationDto.getEvents().size()) {
                throw new NotFoundException("Not all received events were found");
            }
            compilation.setEvents(events);
        }

        Compilation updatedCompilation = compilationRepository.save(compilation);
        log.info("Updated compilation id={} with title '{}'", updatedCompilation.getId(),
                updatedCompilation.getTitle());

        return CompilationMapper.toCompilationDto(updatedCompilation,
                createEventShortDtoList(updatedCompilation.getEvents()));
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));
    }

    private List<EventShortDto> createEventShortDtoList(List<Event> events) {
        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();
        Map<Long, Long> eventsViewsMap = eventsViewsGetter.getEventsViewsMap(eventIds);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        return events.stream()
                .map(event -> {
                    MappingEventParameters mappingEventParameters = MappingEventParameters.builder()
                            .event(event)
                            .categoryDto(CategoryMapper.toCategoryDto(event.getCategory()))
                            .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                            .confirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L))
                            .views(eventsViewsMap.getOrDefault(event.getId(), 0L))
                            .build();
                    return EventMapper.toEventShortDto(mappingEventParameters);
                })
                .collect(Collectors.toList());
    }

    private Compilation checkAndGetCompilation(Long compId) {
        return compilationRepository
                .findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id %d not found", compId)));
    }
}
