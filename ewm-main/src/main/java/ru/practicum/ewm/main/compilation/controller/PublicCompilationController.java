package ru.practicum.ewm.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.service.CompilationService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {

    private final CompilationService compilationService;

    @GetMapping({"/{compId}", "/{compId}/"})
    public CompilationDto getByIdCompilation(@PathVariable
                                             @Positive(message = "Compilation's id should be positive")
                                             Long compId) {
        return compilationService.getByIdCompilation(compId);
    }

    @GetMapping({"", "/"})
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0")
                                                @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
                                                int from,
                                                @RequestParam(defaultValue = "10")
                                                @Positive(message = "Parameter 'size' should be positive")
                                                int size) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return compilationService.getCompilations(pinned, PageRequest.of(page, size, sort));
    }
}