package ru.practicum.ewm.main.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import ru.practicum.ewm.main.request.model.RequestStatus;

import java.util.List;

@Value
@Builder
public class UpdateStatusRequestDto {
    @NotNull(message = "List of request's id shouldn't be null")
    List<Long> requestIds;
    @NotNull(message = "Status shouldn't be null")
    RequestStatus status;
}

