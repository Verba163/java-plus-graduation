package ru.practicum.ewm.events.dto.requests;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.dto.LocationDto;

import java.time.LocalDateTime;

@Builder
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventCommonRequest {
    String title;
    String description;
    String annotation;
    Long category;
    LocationDto location;
    Boolean requestModeration;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime eventDate;
}
