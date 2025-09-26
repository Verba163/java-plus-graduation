package ru.practicum.interaction.events.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.events.dto.requests.UpdateEventUserRequest;

@Builder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateEventParameters {
    Long userId;
    Long eventId;
    UpdateEventUserRequest updateEventUserRequest;
}
