package ru.practicum.ewm.main.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.categories.dto.CategoryDto;
import ru.practicum.ewm.main.user.dto.InitiatorDto;


import java.time.LocalDateTime;

@Value
@Builder
public class ShortEventDto {

    Long id;
    InitiatorDto initiator;
    String title;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    Boolean paid;
    LocalDateTime eventDate;
    Integer views;
}
