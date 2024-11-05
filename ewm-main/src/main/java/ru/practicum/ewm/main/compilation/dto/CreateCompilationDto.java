package ru.practicum.ewm.main.compilation.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;

import java.util.Set;

@Value
@Builder
public class CreateCompilationDto {

    @NotBlank(message = "Title of compilation shouldn't be blank")
    @Size(max = 50, message = "Title of compilation shouldn't be more than 50 characters")
    String title;

    @JsonSetter(nulls = Nulls.SKIP)
    Boolean pinned = false;

    Set<Long> events;
}
