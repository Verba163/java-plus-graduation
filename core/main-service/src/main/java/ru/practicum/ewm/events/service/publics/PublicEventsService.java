package ru.practicum.ewm.events.service.publics;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.comments.dto.CommentShortDto;
import ru.practicum.ewm.events.dto.EventFullDto;
import ru.practicum.ewm.events.dto.EventFullDtoWithComments;
import ru.practicum.ewm.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.ewm.events.dto.parameters.SearchPublicEventsParameters;

import java.util.List;

public interface PublicEventsService {

    List<EventFullDto> searchPublicEvents(SearchPublicEventsParameters searchParams, HttpServletRequest request);

    List<CommentShortDto> getAllEventComments(GetAllCommentsParameters parameters);

    EventFullDtoWithComments getPublicEventById(Long eventId, HttpServletRequest request);
}
