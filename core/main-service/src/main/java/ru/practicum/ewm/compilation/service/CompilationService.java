package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationParams;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    List<CompilationDto> getCompilations(CompilationParams compilationParams);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createdCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(CompilationParams compilationParams);
}
