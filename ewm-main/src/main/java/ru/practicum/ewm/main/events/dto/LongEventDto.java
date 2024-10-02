package ru.practicum.ewm.main.events.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.categories.dto.CategoryDto;
import ru.practicum.ewm.main.events.model.EventStatus;
import ru.practicum.ewm.main.events.model.Location;
import ru.practicum.ewm.main.user.dto.InitiatorDto;

import java.time.LocalDateTime;

@Value
@Builder
public class LongEventDto {

    Long id;
    InitiatorDto initiator;
    String title;
    String annotation;
    String description;
    CategoryDto category;
    Integer participantLimit;
    Integer confirmedRequests;
    Boolean paid;
    Location location;

    LocalDateTime eventDate;
    LocalDateTime createdOn;
    LocalDateTime publishedOn;

    Boolean requestModeration;
    EventStatus state;
    Long views;
}
