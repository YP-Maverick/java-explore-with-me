package ru.practicum.ewm.main.categories.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.main.categories.dto.CategoryDto;
import ru.practicum.ewm.main.categories.dto.CreateCategoryDto;
import ru.practicum.ewm.main.categories.model.Category;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toModel(CreateCategoryDto createCategoryDto);

    Category toModel(CategoryDto categoryDto);
}
