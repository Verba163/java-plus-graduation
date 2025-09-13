package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
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

import java.util.Collections;
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
    private final CategoryMapper categoryMapper;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(CompilationParams params) {
        Pageable pageable = calculatePageable(params.getFrom(), params.getSize().intValue());

        Page<Compilation> page = getPageByPinned(params.getPinned(), pageable);

        log.info("Fetched compilations: from={}, size={}, pinned={}",
                params.getFrom(), params.getSize(), params.getPinned());

        return page.stream()
                .map(this::convertToDtoWithEvents)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        Compilation compilation = findCompilationByIdOrThrow(compId);
        log.info("Get compilation with id={}", compId);
        return compilationMapper.toCompilationDto(compilation, createEventShortDtoList(compilation.getEvents()));
    }

    @Override
    @Transactional
    public CompilationDto createdCompilation(NewCompilationDto dto) {

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ValidationException("Title is necessary");
        }

        List<Event> events = findEventsByIdsOrThrow(dto.getEvents());

        Compilation compilation = compilationMapper.toCompilationEntity(dto, events);
        Compilation saved = compilationRepository.save(compilation);

        log.info("Created compilation id={} title='{}'", saved.getId(), saved.getTitle());
        return convertToDtoWithEvents(saved);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        findCompilationByIdOrThrow(compId);
        compilationRepository.deleteById(compId);
        log.info("Deleted compilation id={}", compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(CompilationParams params) {

        NewCompilationDto dto = params.getNewCompilationDto();
        Compilation compilation = findCompilationByIdOrThrow(params.getCompId());

        updateCompilationFields(compilation, dto);

        Compilation updated = compilationRepository.save(compilation);
        log.info("Updated compilation id={} title='{}'", updated.getId(), updated.getTitle());
        return convertToDtoWithEvents(updated);
    }

    private Pageable calculatePageable(Long from, Integer size) {
        int page = (int) (from / size);
        return PageRequest.of(page, size);
    }

    private Page<Compilation> getPageByPinned(Boolean pinned, Pageable pageable) {
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, pageable);
        } else {
            return compilationRepository.findAll(pageable);
        }
    }

    private Compilation findCompilationByIdOrThrow(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Compilation with id " + id + " not found"));
    }

    private Map<Long, Long> getConfirmedRequestsMap(List<Long> eventIds) {
        return eventsRepository.getConfirmedRequestsForEvents(eventIds).stream()
                .collect(Collectors.toMap(List::getFirst, List::getLast));
    }

    private List<Event> findEventsByIdsOrThrow(List<Long> eventIds) {
        if (eventIds == null) return Collections.emptyList();
        List<Event> events = eventsRepository.findAllById(eventIds);
        if (events.size() != eventIds.size()) {
            throw new NotFoundException("Not all received events were found");
        }
        return events;
    }

    private void updateCompilationFields(Compilation compilation, NewCompilationDto dto) {

        if (dto.getTitle() != null) {
            if (dto.getTitle().isBlank()) {
                throw new ValidationException("title не должно быть пустым");
            }
            compilation.setTitle(dto.getTitle());
        }

        compilation.setPinned(dto.isPinned());

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            List<Event> events = findEventsByIdsOrThrow(dto.getEvents());
            compilation.setEvents(events);
        }
    }

    private List<EventShortDto> createEventShortDtoList(List<Event> events) {
        if (CollectionUtils.isEmpty(events)) {
            return Collections.emptyList();
        }

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .toList();

        Map<Long, Long> viewsMap = eventsViewsGetter.getEventsViewsMap(eventIds);
        Map<Long, Long> confirmedRequestsMap = getConfirmedRequestsMap(eventIds);

        return events.stream()
                .map(event -> mapEventToShortDto(event, viewsMap, confirmedRequestsMap))
                .collect(Collectors.toList());
    }

    private EventShortDto mapEventToShortDto(Event event, Map<Long, Long> viewsMap, Map<Long, Long> confirmedRequestsMap) {
        MappingEventParameters params = MappingEventParameters.builder()
                .event(event)
                .categoryDto(categoryMapper.toCategoryDto(event.getCategory()))
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .confirmedRequests(confirmedRequestsMap.getOrDefault(event.getId(), 0L))
                .views(viewsMap.getOrDefault(event.getId(), 0L))
                .build();
        return eventMapper.toEventShortDto(params);
    }

    private CompilationDto convertToDtoWithEvents(Compilation compilation) {
        List<EventShortDto> eventShortDtos = createEventShortDtoList(compilation.getEvents());
        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }
}