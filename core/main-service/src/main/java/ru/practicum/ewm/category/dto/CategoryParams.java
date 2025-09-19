package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CategoryParams {

    NewCategoryDto newCategoryDto;

    Long catId;

    @Min(value = 0, message = "'from' parameter must be >= 0")
    Long from;

    @Positive(message = "'size' parameter must be > 0")
    Long size;
}
