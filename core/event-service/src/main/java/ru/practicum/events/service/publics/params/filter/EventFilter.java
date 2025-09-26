package ru.practicum.events.service.publics.params.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.events.model.QEvent;
import ru.practicum.interaction.events.dto.parameters.SearchPublicEventsParameters;


public interface EventFilter {

    BooleanExpression getCondition(QEvent event, SearchPublicEventsParameters params);
}