package ru.practicum.ewm.main.request.service;

import ru.practicum.ewm.main.request.dto.RequestDto;
import ru.practicum.ewm.main.request.dto.UpdateStatusRequestDto;

import java.util.List;
import java.util.Map;

public interface RequestService {

    RequestDto createRequest(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    Map<String, List<RequestDto>> changeStatusRequests(Long userId, Long eventId, UpdateStatusRequestDto updateDto);

    List<RequestDto> getByUserRequests(Long userId);

    List<RequestDto> getByUserEventRequests(Long userId, Long eventId);
}
