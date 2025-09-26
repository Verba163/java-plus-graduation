package ru.practicum.interaction.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.feign.config.FeignConfig;

import java.util.List;

import static ru.practicum.interaction.events.constants.EventsApiPath.EVENT_ID;
import static ru.practicum.interaction.feign.FeignPathConstants.*;

@FeignClient(name = "comments-service", configuration = {FeignConfig.class})
public interface CommentsFeignClient {

    @GetMapping(COMMENTS_FOR_EVENT)
    @ResponseStatus(HttpStatus.OK)
    List<CommentShortDto> getCommentsForEvent(@RequestParam(EVENT_ID) Long eventId,
                                              @RequestParam("from") Integer from,
                                              @RequestParam("size") Integer size);

    @GetMapping(GET_COMMENTS_NUMBER)
    List<List<Long>> getCommentsNumberForEvents(@RequestParam("eventIds") List<Long> eventIds);

    @GetMapping(FIRST_COMMENTS_SEARCH)
    List<CommentShortDto> findFirstCommentsForEvent(@PathVariable(EVENT_ID) Long eventId,
                                                    @RequestParam("size") Integer size);
}
