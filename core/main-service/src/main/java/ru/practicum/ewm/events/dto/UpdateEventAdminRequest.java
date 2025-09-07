package ru.practicum.ewm.events.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.enums.AdminEventAction;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventAdminRequest {
    String title;
    String description;
    String annotation;
    Long category;
    LocationDto location;
    Boolean requestModeration;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime eventDate;
    AdminEventAction stateAction;
}