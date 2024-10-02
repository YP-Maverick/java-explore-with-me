package ru.practicum.ewm.main.categories.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.categories.dto.CategoryDto;
import ru.practicum.ewm.main.categories.dto.CreateCategoryDto;
import ru.practicum.ewm.main.categories.mapper.CategoryMapper;
import ru.practicum.ewm.main.categories.model.Category;
import ru.practicum.ewm.main.categories.storage.CategoryStorage;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryStorage categoryStorage;
    private final CategoryMapper mapper;

    @Override
    public CategoryDto createCategory(CreateCategoryDto creationDto) {
        log.info("Request to create category.");

        try {
            Category savedCategory = categoryStorage.save(mapper.toModel(creationDto));
            return mapper.toDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Attempt to create category with existing name {}.", creationDto.getName());
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        log.info("Request to update category with id {}.", categoryDto.getId());

        try {
            Category savedCategory = categoryStorage.saveAndFlush(mapper.toModel(categoryDto));
            return mapper.toDto(savedCategory);
        } catch (DataIntegrityViolationException e) {
            log.error("Duplicate. Attempt to update category with existing name {}.", categoryDto.getName());
            throw new ConflictException(e.getMessage());
        }
    }

    @Override
    public Long deleteCategory(Long id) {
        log.info("Request to delete category with id {}.", id);

        getCategoryById(id);

        categoryStorage.deleteById(id);
        return id;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getCategories(Pageable pageable) {
        log.info("Request to get categories with pageable {}.", pageable);

        List<Category> categories = categoryStorage.findAll(pageable).getContent();
        return categories.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getCategoryById(Long id) {
        log.info("Request to find category with id {}.", id);

        Category category = categoryStorage.findById(id).orElseThrow(() -> {
            log.error("NotFound. Category with id {} does not exist.", id);
            return new NotFoundException(String.format("Category with id %d does not exist.", id));
        });
        return mapper.toDto(category);
    }
}
