package ru.practicum.events.service.publics.params.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.events.model.Event;
import ru.practicum.events.model.QEvent;
import ru.practicum.events.storage.EventsRepository;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.error.exception.ValidationException;
import ru.practicum.interaction.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.interaction.events.enums.EventPublishState;
import ru.practicum.interaction.feign.clients.CategoryFeignClient;
import ru.practicum.interaction.feign.clients.RequestFeignClient;
import ru.practicum.interaction.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class DefaultEventFilter implements EventFilter {

    private final EventsRepository eventsRepository;
    private final CategoryFeignClient categoryFeignClient;
    private final RequestFeignClient requestFeignClient;

    @Override
    public BooleanExpression getCondition(QEvent event, SearchPublicEventsParameters params) {

        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.eventPublishState.eq(EventPublishState.PUBLISHED));

        if (params.getText() != null && !params.getText().isBlank()) {
            String text = params.getText();
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            List<CategoryDto> categories = categoryFeignClient.getCategoryByIds(params.getCategories());
            if (categories.isEmpty()) {
                throw new ValidationException("Categories from search query are not found.");
            }
            conditions.add(event.categoryId.in(params.getCategories()));
        }

        if (Boolean.TRUE.equals(params.getOnlyAvailable())) {
            List<Long> availableEventIds = getAvailableEventIdsByParticipantLimit();
            conditions.add(event.id.in(availableEventIds));
        }

        if (params.getPaid() != null) {
            conditions.add(event.paid.eq(params.getPaid()));
        }

        if (params.getRangeStart() != null || params.getRangeEnd() != null) {
            if (params.getRangeStart() != null) {
                conditions.add(event.eventDate.after(params.getRangeStart()));
            }
            if (params.getRangeEnd() != null) {
                conditions.add(event.eventDate.before(params.getRangeEnd()));
            }
        } else {
            LocalDateTime now = Util.getNowTruncatedToSeconds();
            conditions.add(event.eventDate.after(now));
        }

        return conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);
    }

    private List<Long> getAvailableEventIdsByParticipantLimit() {
        List<Event> events = eventsRepository.findAll();
        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();

        Map<Long, Long> confirmedRequestsMap = requestFeignClient.getConfirmedRequestsCount(eventIds);
        System.out.println(confirmedRequestsMap);

        return events.stream()
                .filter(event -> event.getParticipantLimit() == 0 ||
                        confirmedRequestsMap.getOrDefault(event.getId(), 0L) < event.getParticipantLimit())
                .map(Event::getId)
                .toList();
    }
}
