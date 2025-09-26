package ru.practicum.ewm.client;

import feign.FeignException;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.StatHitDto;
import ru.practicum.dto.StatViewDto;

import java.util.List;

@FeignClient(name = "stats-server")
public interface StatFeignClient {

    @PostMapping(value = "/hit", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    StatHitDto hit(@Valid @RequestBody StatHitDto statDto) throws FeignException;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    List<StatViewDto> getStat(@RequestParam(value = "start", required = false) String start,
                              @RequestParam(value = "end", required = false) String end,
                              @RequestParam(value = "uris", required = false) List<String> uris,
                              @RequestParam(value = "unique", required = false) Boolean unique)
            throws FeignException;
}