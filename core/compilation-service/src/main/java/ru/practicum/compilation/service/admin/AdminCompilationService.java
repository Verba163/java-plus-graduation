package ru.practicum.compilation.service.admin;


import ru.practicum.interaction.compilation.dto.CompilationDto;
import ru.practicum.interaction.compilation.dto.CompilationParams;
import ru.practicum.interaction.compilation.dto.NewCompilationDto;

public interface AdminCompilationService {

    CompilationDto createdCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(CompilationParams compilationParams);
}
