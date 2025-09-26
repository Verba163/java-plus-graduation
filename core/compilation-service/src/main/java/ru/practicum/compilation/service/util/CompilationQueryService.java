package ru.practicum.compilation.service.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.storage.CompilationRepository;
import ru.practicum.interaction.error.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CompilationQueryService {

    private final CompilationRepository compilationRepository;

    public Page<Compilation> getPageByPinned(Boolean pinned, Pageable pageable) {
        if (pinned != null) {
            return compilationRepository.findByPinned(pinned, pageable);
        }
        return compilationRepository.findAll(pageable);
    }

    public Compilation findCompilationByIdOrThrow(Long id) {
        return compilationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Compilation with id %d not found", id)));
    }
}