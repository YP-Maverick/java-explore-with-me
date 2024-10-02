package ru.practicum.ewm.main.events.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.events.dto.StatusAction;
import ru.practicum.ewm.main.events.model.Location;

import java.time.LocalDateTime;

@Value
@Builder
public class UpdateEventDto {

    @Size(min = 3, max = 120, message = "Annotation of event shouldn't be less than 3 and more than 120 characters")
    String title;

    @Size(min = 20, max = 2000, message = "Annotation of event shouldn't be less than 20 and more than 2000 characters")
    String annotation;

    @Size(min = 20, max = 7000, message = "Description of event shouldn't be less than 20 and more than 7000 characters")
    String description;

    @PositiveOrZero(message = "Category of event shouldn't be negative")
    Long category;

    @PositiveOrZero(message = "Participant limit of event shouldn't be negative")
    Integer participantLimit;

    Boolean paid;

    Location location;

    @Future(message = "Event date should be in future")
    LocalDateTime eventDate;

    Boolean requestModeration;

    StatusAction statusAction;
}
