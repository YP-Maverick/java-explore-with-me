package ru.practicum.ewm.main.request.dto;

import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.request.model.RequestStatus;

import java.time.LocalDateTime;

@Value
@Builder
public class RequestDto {
    Long id;
    Long requester;
    Long event;
    RequestStatus status;
    LocalDateTime created;
}

