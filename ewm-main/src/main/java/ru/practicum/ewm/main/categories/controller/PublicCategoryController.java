package ru.practicum.ewm.main.categories.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.categories.dto.CategoryDto;
import ru.practicum.ewm.main.categories.service.CategoryService;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class PublicCategoryController {

    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryDto> getAllCategories(
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
            int from,
            @RequestParam(defaultValue = "10")
            @Positive(message = "Parameter 'size' should be positive")
            int size
    ) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return categoryService.getCategories(PageRequest.of(page, size, sort));
    }

    @GetMapping({"/{catId}", "/{catId}/"})
    public CategoryDto getCategoryById(
            @PathVariable
            @Positive(message = "Category's id should be positive")
            Long catId
    ) {
        return categoryService.getCategoryById(catId);
    }
}
