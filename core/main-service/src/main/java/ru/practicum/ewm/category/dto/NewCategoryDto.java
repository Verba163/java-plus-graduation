package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NewCategoryDto {

    @NotBlank(message = "Name can't be empty")
    @Size(max = 50, message = "Name can't be more than 50 symbols")
    String name;
}
