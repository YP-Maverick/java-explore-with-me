package ru.practicum.ewm.main.categories.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.categories.dto.CategoryDto;
import ru.practicum.ewm.main.categories.dto.CreateCategoryDto;
import ru.practicum.ewm.main.categories.service.CategoryService;
import ru.practicum.ewm.main.exception.ConflictException;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping()
    public CategoryDto createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.createCategory(createCategoryDto);
    }

    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    @DeleteMapping({"/{catId}", "/{catId}/"})
    public Long deleteCategory(@PathVariable
                               @Positive(message = "Category's id should be positive")
                               Long catId) {
        try {
            return categoryService.deleteCategory(catId);
        } catch (DataIntegrityViolationException e) {
            log.error("Conflict. Удаление категории с id {} невозможно, т.к. она используется.", catId);
            throw new ConflictException(e.getMessage());
        }
    }

    @PatchMapping({"/{catId}", "/{catId}/"})
    public CategoryDto updateCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto,
                                      @PathVariable
                                      @Positive(message = "Category's id should be positive")
                                      Long catId) {
        CategoryDto updatedCategory = categoryService.getCategoryById(catId).withName(createCategoryDto.getName());
        return categoryService.updateCategory(updatedCategory);
    }
}
