package ru.practicum.compilation.service.validation;

import org.springframework.stereotype.Component;
import ru.practicum.interaction.compilation.dto.NewCompilationDto;
import ru.practicum.interaction.error.exception.ValidationException;

@Component
public class CompilationValidator {

    public void validateNewCompilationDto(NewCompilationDto dto) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new ValidationException("Title is necessary");
        }
    }
}