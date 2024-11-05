package ru.practicum.ewm.main.compilation.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.events.dto.ShortEventDto;

import java.util.Set;

@Value
@Builder
public class CompilationDto {

    Long id;
    String title;
    Boolean pinned;
    Set<ShortEventDto> events;
}
