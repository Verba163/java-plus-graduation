package ru.practicum.ewm.events.service.publics.params.filter;

import com.querydsl.core.types.dsl.BooleanExpression;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;
import ru.practicum.ewm.events.model.QEvent;

public interface EventFilter {

    BooleanExpression getCondition(QEvent event, SearchPublicEventsParameters params);
}