package ru.practicum.ewm.main.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(CreateCompilationDto createCompilationDto);

    CompilationDto updateCompilation(Long id, UpdateCompilationDto updateDto);

    Long deleteCompilation(Long id);

    CompilationDto getByIdCompilation(Long id);

    List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable);
}
