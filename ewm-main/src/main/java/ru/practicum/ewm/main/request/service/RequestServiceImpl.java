package ru.practicum.ewm.main.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.ewm.main.events.model.Event;
import ru.practicum.ewm.main.events.model.EventStatus;
import ru.practicum.ewm.main.events.storage.EventStorage;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ForbiddenException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.request.dto.RequestDto;
import ru.practicum.ewm.main.request.dto.UpdateStatusRequestDto;
import ru.practicum.ewm.main.request.mapper.RequestMapper;
import ru.practicum.ewm.main.request.model.Request;
import ru.practicum.ewm.main.request.model.RequestStatus;
import ru.practicum.ewm.main.request.storage.RequestStorage;
import ru.practicum.ewm.main.user.model.User;
import ru.practicum.ewm.main.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestStorage requestStorage;
    private final RequestMapper requestMapper;
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    private void throwConflictLimit(Long eventId) {
        log.error("Conflict. Event with id {} has reached the participant limit.", eventId);
        throw new ConflictException("The participant limit has been reached");
    }

    private void throwNotFoundEvent(Long eventId, Long userId) {
        log.error("NotFound. Event with id {} for user with id {} does not exist.", eventId, userId);
        throw new NotFoundException(String.format("Event with id = %d was not found", eventId));
    }

    @Override
    public RequestDto createRequest(Long userId, Long eventId) {
        log.info("Request to create participation request for event with id {} from user with id {}.", eventId, userId);

        // Check for duplicate request
        Request existingRequest = requestStorage.findByRequesterIdAndEventId(userId, eventId);
        if (existingRequest != null) {
            log.error("Conflict. Duplicate request for participation from user with id {} for event with id {}.", userId, eventId);
            throw new ConflictException("You can't add a repeat request");
        }

        // Check if event exists
        Event event = eventStorage.findById(eventId).orElseThrow(() -> {
            log.error("NotFound. Interaction with a request for a non-existing event with id {}.", eventId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });

        // Check event status and limit
        if (!event.getState().equals(EventStatus.PUBLISHED)) {
            log.error("Conflict. User with id {} is attempting to request participation in an unpublished event with id {}.", userId, eventId);
            throw new ConflictException("You can't participate in an unpublished event");
        } else if (!event.getParticipantLimit().equals(0) && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throwConflictLimit(eventId);
        }

        // Check if user exists and is not the event initiator
        User requester = userStorage.findById(userId).orElseThrow(() -> {
            log.error("Forbidden. Non-existing user with id {} is attempting to interact with a request.", userId);
            return new ForbiddenException("You haven't access. Please, log in");
        });

        if (event.getInitiator().getId().equals(requester.getId())) {
            log.error("Conflict. User with id {} is attempting to request participation in their own event with id {}.", userId, eventId);
            throw new ConflictException("The event initiator cannot add a request to participate in their event");
        }

        RequestStatus status;
        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0)) {
            status = RequestStatus.CONFIRMED;
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventStorage.save(event);
        } else {
            status = RequestStatus.PENDING;
        }

        Request newRequest = requestStorage.save(new Request(null, requester, event, status, LocalDateTime.now()));
        return requestMapper.toDto(newRequest);
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        log.info("Request to cancel request with id {} from user with id {}.", requestId, userId);

        Request request = requestStorage.findById(requestId).orElseThrow(() -> {
            log.error("NotFound. Request with id {} does not exist.", requestId);
            return new NotFoundException(String.format("Request with id = %d was not found", requestId));
        });

        if (request.getRequester().getId().equals(userId)) {
            request.setStatus(RequestStatus.CANCELED);
            Request canceledRequest = requestStorage.save(request);
            return requestMapper.toDto(canceledRequest);
        } else {
            log.error("NotFound. Request with id {} does not exist for user with id {}.", requestId, userId);
            throw new NotFoundException(String.format("Request with id = %d was not found", requestId));
        }
    }

    @Override
    public Map<String, List<RequestDto>> changeStatusRequests(Long userId, Long eventId, UpdateStatusRequestDto updateDto) {
        log.info("Request to change status of participation requests for event with id {} from user with id {}.", eventId, userId);

        List<Request> requests = requestStorage.findByIdInAndEventId(updateDto.getRequestIds(), eventId);
        if (requests.size() != updateDto.getRequestIds().size()) {
            log.error("NotFound. Request(s) or event with id {} do not exist.", eventId);
            throw new NotFoundException(String.format("Request(s) or event with id = %d was not found", eventId));
        } else if (!requests.stream().allMatch(r -> r.getStatus().equals(RequestStatus.PENDING))) {
            log.error("Conflict. Attempting to change status for requests not in PENDING state.");
            throw new ConflictException("The status can only be changed for applications that are in a pending status");
        }

        Event event = requests.get(0).getEvent();
        if (!event.getInitiator().getId().equals(userId)) throwNotFoundEvent(eventId, userId);

        return switch (updateDto.getStatus()) {
            case CONFIRMED -> confirmRequests(requests, event);
            case REJECTED -> rejectRequests(requests);
            default -> new HashMap<>();
        };
    }

    private Map<String, List<RequestDto>> confirmRequests(List<Request> requests, Event event) {
        if (!event.getParticipantLimit().equals(0) && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throwConflictLimit(event.getId());
        }

        Map<String, List<RequestDto>> result = new HashMap<>();
        List<RequestDto> confirmedRequests;

        // Check if limit will be reached during request confirmation
        int difference = event.getParticipantLimit() - event.getConfirmedRequests();
        int countRequests = requests.size();
        if (difference < countRequests) {
            List<Request> confirmed = requests.subList(0, difference);
            confirmed.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
            confirmedRequests = confirmed.stream().map(requestMapper::toDto).collect(Collectors.toList());

            List<Request> rejected = new ArrayList<>(requests.subList(difference, countRequests));
            rejected.forEach(r -> r.setStatus(RequestStatus.REJECTED));
            List<RequestDto> rejectedRequests = rejected.stream().map(requestMapper::toDto).collect(Collectors.toList());
            result.put("rejectedRequests", rejectedRequests);

            requestStorage.saveAll(requests);
            event.setConfirmedRequests(event.getConfirmedRequests() + difference);
            eventStorage.save(event);
        } else {
            requests.forEach(r -> r.setStatus(RequestStatus.CONFIRMED));
            confirmedRequests = requestStorage.saveAll(requests).stream().map(requestMapper::toDto).collect(Collectors.toList());

            event.setConfirmedRequests(event.getConfirmedRequests() + countRequests);
            eventStorage.save(event);
        }
        result.put("confirmedRequests", confirmedRequests);
        return result;
    }

    private Map<String, List<RequestDto>> rejectRequests(List<Request> requests) {
        requests.forEach(r -> r.setStatus(RequestStatus.REJECTED));
        List<RequestDto> rejectedRequests = requestStorage.saveAll(requests).stream().map(requestMapper::toDto).collect(Collectors.toList());

        Map<String, List<RequestDto>> rejected = new HashMap<>();
        rejected.put("rejectedRequests", rejectedRequests);
        return rejected;
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getByUserRequests(Long userId) {
        log.info("Request to get participation requests for user with id {}.", userId);

        return requestStorage.findByRequesterId(userId).stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getByUserEventRequests(Long userId, Long eventId) {
        log.info("Request to get participation requests for event with id {} by user with id {}.", eventId, userId);

        List<Request> requests = requestStorage.findByEventId(eventId);
        if (requests.isEmpty()) return new ArrayList<>();
        if (requests.get(0).getRequester().getId().equals(userId)) throwNotFoundEvent(eventId, userId);

        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }
}
