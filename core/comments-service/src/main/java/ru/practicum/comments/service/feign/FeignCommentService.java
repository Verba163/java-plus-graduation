package ru.practicum.comments.service.feign;

import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;

import java.util.List;

public interface FeignCommentService {

    List<CommentShortDto> getCommentsForEvent(GetAllCommentsParameters parameters);

    List<CommentShortDto> findFirstCommentsForEvent(Long eventId, Long size);

    List<List<Long>> getCommentsNumberForEvents(List<Long> eventIds);
}
