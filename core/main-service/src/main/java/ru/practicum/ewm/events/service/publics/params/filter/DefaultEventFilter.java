package ru.practicum.ewm.events.service.publics.params.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.ValidationException;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.ewm.events.enums.EventPublishState;
import ru.practicum.ewm.events.model.QEvent;
import ru.practicum.ewm.events.storage.EventsRepository;
import ru.practicum.ewm.util.Util;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class DefaultEventFilter implements EventFilter {

    private final CategoryRepository categoryRepository;
    private final EventsRepository eventsRepository;

    @Override
    public BooleanExpression getCondition(QEvent event, SearchPublicEventsParameters params) {

        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.eventPublishState.eq(EventPublishState.PUBLISHED));

        if (params.getText() != null && !params.getText().isBlank()) {
            String text = params.getText();
            conditions.add(event.annotation.containsIgnoreCase(text).or(event.description.containsIgnoreCase(text)));
        }

        if (params.getCategories() != null && !params.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(params.getCategories());
            if (categories.isEmpty()) {
                throw new ValidationException("Categories from search query are not found.");
            }
            conditions.add(event.category.id.in(params.getCategories()));
        }

        if (Boolean.TRUE.equals(params.getOnlyAvailable())) {
            List<Long> availableEventIds = eventsRepository.getAvailableEventIdsByParticipantLimit();
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
}
