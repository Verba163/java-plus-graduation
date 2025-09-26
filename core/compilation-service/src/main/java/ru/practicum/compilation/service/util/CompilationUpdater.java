package ru.practicum.compilation.service.util;

import org.springframework.stereotype.Component;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.interaction.compilation.dto.NewCompilationDto;
import ru.practicum.interaction.error.exception.ValidationException;
import ru.practicum.interaction.feign.clients.EventFeignClient;

@Component
public class CompilationUpdater {

    private final EventFeignClient eventFeignClient;

    public CompilationUpdater(EventFeignClient eventFeignClient) {
        this.eventFeignClient = eventFeignClient;
    }

    public void updateCompilationFields(Compilation compilation, NewCompilationDto dto) {
        if (dto.getTitle() != null) {
            if (dto.getTitle().isBlank()) {
                throw new ValidationException("Title can not be empty");
            }
            compilation.setTitle(dto.getTitle());
        }
        compilation.setPinned(dto.isPinned());

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            eventFeignClient.checkEventExistingByIds(dto.getEvents());
            compilation.setEventsIds(dto.getEvents());
        }
    }
}