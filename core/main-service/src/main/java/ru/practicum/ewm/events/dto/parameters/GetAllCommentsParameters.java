package ru.practicum.ewm.events.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetAllCommentsParameters {
    Long eventId;
    Integer from;
    Integer size;
}
