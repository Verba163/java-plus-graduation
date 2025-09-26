package ru.practicum.events.service.admin;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.events.mapper.EventMapper;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.QEvent;
import ru.practicum.events.service.assemblers.EventDtoAssemblers;
import ru.practicum.events.service.util.ConditionProperties;
import ru.practicum.events.service.util.PaginationHelper;
import ru.practicum.events.service.util.ProcessAction;
import ru.practicum.events.service.util.UpdateProperties;
import ru.practicum.events.service.validation.EventValidator;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.parameters.SearchEventsParameters;
import ru.practicum.interaction.events.dto.requests.UpdateEventAdminRequest;
import ru.practicum.interaction.events.dto.requests.UpdateEventCommonRequest;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional
public class AdminEventsServiceImpl implements AdminEventsService {

    private final EventsRepository eventsRepository;
    private final EventMapper eventMapper;
    private final PaginationHelper paginationHelper;
    private final EventValidator eventValidator;
    private final EventDtoAssemblers eventDtoAssemblers;
    private final ConditionProperties conditionProperties;
    private final ProcessAction processAction;
    private final UpdateProperties updateProperties;


    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> searchEvents(SearchEventsParameters searchParams) {

        QEvent event = QEvent.event;
        BooleanExpression condition = conditionProperties.buildSearchConditions(searchParams, event);

        Pageable page = paginationHelper.createPageableObject(searchParams.getFrom(), searchParams.getSize());

        List<Event> events = eventsRepository.findAll(condition, page)
                .stream()
                .toList();

        return eventDtoAssemblers.createEventFullDtoList(events);
    }

    @Override
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest updateRequest) {

        Event event = eventValidator.getEventWithCheck(eventId);
        UpdateEventCommonRequest commonRequest = eventMapper.adminUpdateRequestToCommonRequest(updateRequest);
        updateProperties.updateEventProperties(event, commonRequest);

        if (updateRequest.getStateAction() != null) {
            processAction.processStateAction(event, updateRequest.getStateAction());
        }

        Event savedEvent = eventsRepository.save(event);
        return eventDtoAssemblers.createEventFullDto(savedEvent);
    }
}