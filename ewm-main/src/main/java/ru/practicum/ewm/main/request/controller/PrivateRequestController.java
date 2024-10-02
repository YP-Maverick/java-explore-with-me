package ru.practicum.ewm.main.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import ru.practicum.ewm.main.request.dto.RequestDto;
import ru.practicum.ewm.main.request.dto.UpdateStatusRequestDto;
import ru.practicum.ewm.main.request.service.RequestService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}")
public class PrivateRequestController {

    private final RequestService requestService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping(value = {"/requests", "/requests/"})
    public RequestDto createRequestDto(@PathVariable
                                       @Positive(message = "User's id should be positive")
                                       Long userId,
                                       @RequestParam
                                       @Positive(message = "Event's id should be positive")
                                       Long eventId) {
        return requestService.createRequest(userId, eventId);
    }

    @PatchMapping(value = {"/requests/{requestId}/cancel", "/requests/{requestId}/cancel/"})
    public RequestDto cancelRequest(@PathVariable
                                    @Positive(message = "User's id should be positive")
                                    Long userId,
                                    @PathVariable
                                    @Positive(message = "Request's id should be positive")
                                    Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping(value = {"/events/{eventId}/requests", "/events/{eventId}/requests/"})
    public Map<String, List<RequestDto>> changeStateRequest(
            @PathVariable
            @Positive(message = "User's id should be positive")
            Long userId,
            @PathVariable
            @Positive(message = "Event's id should be positive")
            Long eventId,
            @Valid @RequestBody UpdateStatusRequestDto updateStatusRequestDto
            ) {
        if (updateStatusRequestDto.getRequestIds().isEmpty()) {
            return new HashMap<>();
        } else return requestService.changeStatusRequests(userId, eventId, updateStatusRequestDto);
    }

    @GetMapping({"/requests", "/requests/"})
    public List<RequestDto> getUserRequests(@PathVariable
                                            @Positive(message = "User's id should be positive")
                                            Long userId) {
        return requestService.getByUserRequests(userId);
    }

    @GetMapping({"/events/{eventId}/requests", "/events/{eventId}/requests/"})
    public List<RequestDto> getUserEventRequests(@PathVariable
                                                 @Positive(message = "User's id should be positive")
                                                 Long userId,
                                                 @PathVariable
                                                 @Positive(message = "Event's id should be positive")
                                                 Long eventId) {
        return requestService.getByUserEventRequests(userId, eventId);
    }
}