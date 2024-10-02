package ru.practicum.ewm.main.categories.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@With
@Value
@Builder
public class CategoryDto {

    Long id;
    String name;
}
