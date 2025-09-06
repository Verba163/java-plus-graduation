package ru.practicum.ewm.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewEventDto {
    @NotBlank
    @Size(min = 3, max = 120)
    String title;

    @NotBlank
    @Size(min = 20, max = 7000)
    String description;

    @NotBlank
    @Size(min = 20, max = 2000)
    String annotation;

    @NotNull
    Integer category;

    @NotNull
    LocationDto location;

    @Builder.Default
    Boolean requestModeration = true;

    @Builder.Default
    Boolean paid = false;

    @Builder.Default
    Integer participantLimit = 0;

    @NotNull
    @JsonFormat
    LocalDateTime eventDate;
}