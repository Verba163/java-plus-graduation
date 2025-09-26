package ru.practicum.comments.service.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.comments.mapper.CommentMapper;
import ru.practicum.comments.model.Comment;
import ru.practicum.comments.storage.CommentRepository;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeignCommentServiceImpl implements FeignCommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    public List<CommentShortDto> getCommentsForEvent(GetAllCommentsParameters parameters) {
        List<Comment> comments = commentRepository.findPageableCommentsForEvent(parameters.getEventId(),
                parameters.getFrom(), parameters.getSize());
        return comments.stream()
                .map(commentMapper::toCommentShortDto)
                .toList();
    }

    @Override
    public List<CommentShortDto> findFirstCommentsForEvent(Long eventId, Integer size) {
        return commentRepository.findFirstCommentsForEvent(eventId, size).stream()
                .map(commentMapper::toCommentShortDto)
                .toList();
    }

    @Override
    public List<List<Long>> getCommentsNumberForEvents(List<Long> eventIds) {
        return commentRepository.getCommentsNumberForEvents(eventIds);
    }

}
