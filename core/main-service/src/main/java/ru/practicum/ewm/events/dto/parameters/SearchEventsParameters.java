package ru.practicum.ewm.events.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchEventsParameters {
    List<Long> users;
    List<String> states;
    List<Long> categories;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    Integer from;
    Integer size;
}
