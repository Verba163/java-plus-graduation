package ru.practicum.error;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiError {
    private final HttpStatus status;
    private final String title;
    private final String message;
    private final String stackTrace;
}
