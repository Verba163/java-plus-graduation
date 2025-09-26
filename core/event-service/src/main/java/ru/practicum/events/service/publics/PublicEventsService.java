package ru.practicum.events.service.publics;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.EventFullDto;
import ru.practicum.interaction.events.dto.EventFullDtoWithComments;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;
import ru.practicum.interaction.events.dto.parameters.SearchPublicEventsParameters;

import java.util.List;

public interface PublicEventsService {

    List<EventFullDto> searchPublicEvents(SearchPublicEventsParameters searchParams, HttpServletRequest request);

    List<CommentShortDto> getAllEventComments(GetAllCommentsParameters parameters);

    EventFullDtoWithComments getPublicEventById(Long eventId, HttpServletRequest request);
}
