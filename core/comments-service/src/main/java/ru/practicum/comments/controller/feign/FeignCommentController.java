package ru.practicum.comments.controller.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comments.service.feign.FeignCommentService;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.events.dto.parameters.GetAllCommentsParameters;

import java.util.List;

import static ru.practicum.interaction.events.constants.EventsApiPath.EVENT_ID;
import static ru.practicum.interaction.feign.FeignPathConstants.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeignCommentController {

    private final FeignCommentService feignCommentService;

    @GetMapping(COMMENTS_FOR_EVENT)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> getCommentsForEvent(@RequestParam(EVENT_ID) Long eventId,
                                                     @RequestParam("from") Integer from,
                                                     @RequestParam("size") Integer size) {
        log.info("Request: get all comment for event id={}. From={} and size={}", eventId, from,
                size);
        GetAllCommentsParameters parameters = GetAllCommentsParameters.builder()
                .eventId(eventId)
                .from(from)
                .size(size)
                .build();
        return feignCommentService.getCommentsForEvent(parameters);
    }

    @GetMapping(GET_COMMENTS_NUMBER)
    @ResponseStatus(HttpStatus.OK)
    public List<List<Long>> getCommentsNumberForEvents(@RequestParam("eventIds") List<Long> eventIds) {
        log.info("Request: get count comment for events id={}.", eventIds);
        return feignCommentService.getCommentsNumberForEvents(eventIds);
    }

    @GetMapping(FIRST_COMMENTS_SEARCH)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentShortDto> findFirstCommentsForEvent(@PathVariable(EVENT_ID) Long eventId,
                                                           @RequestParam("size") Long size) {
        log.info("Request: get first five comment for events id={}.", eventId);
        return feignCommentService.findFirstCommentsForEvent(eventId, size);
    }
}