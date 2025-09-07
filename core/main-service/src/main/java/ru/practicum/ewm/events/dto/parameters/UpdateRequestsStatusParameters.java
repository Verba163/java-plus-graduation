package ru.practicum.ewm.events.dto.parameters;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.dto.EventRequestStatusUpdateRequest;

@Builder(toBuilder = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateRequestsStatusParameters {
    Long userId;
    Long eventId;
    EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest;
}
