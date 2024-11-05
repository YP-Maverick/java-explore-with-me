package ru.practicum.ewm.main.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class UpdateCompilationDto {

    @Size(max = 50, message = "Title of compilation shouldn't be more than 50 characters")
    String title;

    Boolean pinned;
    Set<Long> events;
}
