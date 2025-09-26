package ru.practicum.events.service.util;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import org.springframework.stereotype.Component;
import ru.practicum.events.model.QEvent;
import ru.practicum.interaction.events.dto.parameters.SearchEventsParameters;
import ru.practicum.interaction.events.enums.EventPublishState;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ConditionProperties {

    public BooleanExpression buildSearchConditions(SearchEventsParameters searchParams, QEvent event) {
        List<BooleanExpression> conditions = new ArrayList<>();

        Optional.ofNullable(searchParams.getUsers())
                .filter(list -> !list.isEmpty())
                .ifPresent(users -> conditions.add(event.initiatorId.in(users)));

        Optional.ofNullable(searchParams.getStates())
                .filter(statesList -> !statesList.isEmpty())
                .map(statesList -> statesList.stream()
                        .map(EventPublishState::valueOf)
                        .toList())
                .ifPresent(statesEnum -> conditions.add(event.eventPublishState.in(statesEnum)));

        Optional.ofNullable(searchParams.getCategories())
                .filter(list -> !list.isEmpty())
                .ifPresent(categories -> conditions.add(event.categoryId.in(categories)));

        Optional.ofNullable(searchParams.getRangeStart())
                .ifPresent(start -> conditions.add(event.eventDate.after(start)));

        Optional.ofNullable(searchParams.getRangeEnd())
                .ifPresent(end -> conditions.add(event.eventDate.before(end)));

        return conditions.stream()
                .reduce(Expressions.asBoolean(true).isTrue(), BooleanExpression::and);
    }
}
