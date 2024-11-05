package ru.practicum.ewm.main.events.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.categories.model.Category;
import ru.practicum.ewm.main.categories.storage.CategoryStorage;
import ru.practicum.ewm.main.events.dto.CreateEventDto;
import ru.practicum.ewm.main.events.dto.LongEventDto;
import ru.practicum.ewm.main.events.dto.ShortEventDto;
import ru.practicum.ewm.main.events.dto.UpdateEventDto;
import ru.practicum.ewm.main.events.mapper.EventMapper;
import ru.practicum.ewm.main.events.model.Event;
import ru.practicum.ewm.main.events.model.EventStatus;
import ru.practicum.ewm.main.events.params.EventAdminSearchParam;
import ru.practicum.ewm.main.events.params.EventPublicSearchParam;
import ru.practicum.ewm.main.events.storage.EventStorage;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.ForbiddenException;
import ru.practicum.ewm.main.exception.NotFoundException;
import ru.practicum.ewm.main.exception.NotValidException;
import ru.practicum.ewm.main.stat.service.StatsService;
import ru.practicum.ewm.main.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventStorage eventStorage;
    private final EventMapper eventMapper;
    private final UserStorage userStorage;
    private final CategoryStorage categoryStorage;
    private final StatsService statsService;

    private Category getCategory(Long categoryId) {
        return categoryStorage.findById(categoryId).orElseThrow(() -> {
            log.error("NotFound. Category with id {} does not exist.", categoryId);
            return new NotFoundException("Event's category doesn't exist.");
        });
    }

    private void throwNotValidTime(Long eventId) {
        log.error("NotValid. The date and time of the event with id {} from user is less than 2 hours from the current moment.", eventId);
        throw new NotValidException("The date and time of the event cannot be earlier than two hours from the current moment.");
    }

    @Override
    public LongEventDto create(Long userId, CreateEventDto createEventDto) {
        log.info("Request to create an event from user with id {}", userId);

        var initiator = userStorage.findById(userId).orElseThrow(() -> {
            log.error("Forbidden. Non-existent user with id {} is trying to interact with the event.", userId);
            return new ForbiddenException("You haven't access. Please, log in.");
        });
        var category = getCategory(createEventDto.getCategory());

        if (createEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throwNotValidTime(userId);
        }
        var event = eventStorage.save(eventMapper.toEvent(createEventDto, initiator, category, EventStatus.PENDING));
        return eventMapper.toLongDto(event, 0L);
    }

    private Event updateFields(Event event, UpdateEventDto updateDto, String initiator) {
        int hoursBuffer = "admin".equals(initiator) ? 0 : 2;

        Optional.ofNullable(updateDto.getTitle()).ifPresent(event::setTitle);
        Optional.ofNullable(updateDto.getAnnotation()).ifPresent(event::setAnnotation);
        Optional.ofNullable(updateDto.getDescription()).ifPresent(event::setDescription);
        Optional.ofNullable(updateDto.getCategory()).map(this::getCategory).ifPresent(event::setCategory);
        Optional.ofNullable(updateDto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        Optional.ofNullable(updateDto.getPaid()).ifPresent(event::setPaid);

        Optional.ofNullable(updateDto.getLocation().getLat()).ifPresent(event::setLat);
        Optional.ofNullable(updateDto.getLocation().getLon()).ifPresent(event::setLon);


        if (updateDto.getEventDate() != null) {
            var eventDate = updateDto.getEventDate();
            if (eventDate.isBefore(LocalDateTime.now().plusHours(hoursBuffer))) {
                throwNotValidTime(event.getId());
            } else {
                event.setEventDate(eventDate);
            }
        }

        Optional.ofNullable(updateDto.getRequestModeration()).ifPresent(event::setRequestModeration);

        return event;
    }

    @Override
    public LongEventDto updateByUser(Long userId, Long eventId, UpdateEventDto updateDto) {
        log.info("Request to update event with id {} from user with id {}", eventId, userId);

        var toUpdateEvent = eventStorage.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("NotFound. Updating by user. Event with id {} for user with id {} not found.", eventId, userId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });

        if (toUpdateEvent.getState().equals(EventStatus.PUBLISHED)) {
            log.error("Conflict. Attempting to change a published event with id {} from user with id {}", eventId, userId);
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        var updatedEvent = updateFields(toUpdateEvent, updateDto, "user");

        switch (Optional.ofNullable(updateDto.getStatusAction()).orElseGet(() -> {
            log.info("No status action provided for update by user");
            return null;
        })) {
            case SEND_TO_REVIEW -> updatedEvent.setState(EventStatus.PENDING);
            case CANCEL_REVIEW -> updatedEvent.setState(EventStatus.CANCELED);
            default -> log.info("No action taken for event id {}", updatedEvent.getId());
        }

        return eventMapper.toLongDto(eventStorage.save(updatedEvent), 0L);
    }

    @Override
    public LongEventDto updateByAdmin(Long eventId, UpdateEventDto updateDto) {
        log.info("Request to update event with id {} from admin.", eventId);

        var toUpdateEvent = eventStorage.findById(eventId).orElseThrow(() -> {
            log.error("NotFound. Updating by admin. Event with id {} not found.", eventId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });

        var updatedEvent = updateFields(toUpdateEvent, updateDto, "admin");

        switch (Optional.ofNullable(updateDto.getStatusAction()).orElseGet(() -> {
            log.info("No status action provided for update by admin");
            return null;
        })) {
            case PUBLISH_EVENT -> {
                if (updatedEvent.getState().equals(EventStatus.PENDING)) {
                    updatedEvent.setState(EventStatus.PUBLISHED);
                    updatedEvent.setPublishedOn(LocalDateTime.now());
                } else {
                    log.error("Cannot publish. Event is not in the pending state");
                    throw new ConflictException("Cannot publish because the event is not in the pending state");
                }
            }
            case REJECT_EVENT -> {
                if (!updatedEvent.getState().equals(EventStatus.PUBLISHED)) {
                    updatedEvent.setState(EventStatus.CANCELED);
                } else {
                    log.error("Cannot reject. Event is already published");
                    throw new ConflictException("Cannot reject because the event is already published");
                }
            }
            default -> log.info("No action taken for event id {}", updatedEvent.getId());
        }

        return eventMapper.toLongDto(eventStorage.save(updatedEvent), 0L);
    }

    @Transactional(readOnly = true)
    @Override
    public LongEventDto getByUserId(Long userId, Long eventId) {
        log.info("Request to get event with id {}", eventId);

        var event = eventStorage.findByIdAndInitiatorId(eventId, userId).orElseThrow(() -> {
            log.error("NotFound. Event with id {} for user with id {} not found.", eventId, userId);
            return new NotFoundException(String.format("Event with id = %d was not found", eventId));
        });

        var view = statsService.getView(Collections.singletonList(event.getId()), false);
        return eventMapper.toLongDto(event, view.getOrDefault(event.getId(), 0L));
    }

    private Map<Long, Long> getView(List<Event> events, boolean unique) {
        return events.isEmpty() ? new HashMap<>() :
                statsService.getView(events.stream()
                        .map(Event::getId)
                        .collect(Collectors.toList()), unique);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShortEventDto> getAllByUser(Long userId, Pageable pageable) {
        log.info("Request to get events from user with id {}", userId);

        var events = eventStorage.findByInitiatorId(userId, pageable);
        var view = getView(events, false);
        return events.stream()
                .map(e -> eventMapper.toShortDto(e, view.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<LongEventDto> getEventsForAdmin(EventAdminSearchParam param) {
        log.info("Request from admin to get events.");

        var events = eventStorage.searchEventsForAdmin(param);
        var view = getView(events, false);
        return events.stream()
                .map(e -> eventMapper.toLongDto(e, view.getOrDefault(e.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ShortEventDto> getPublicEvents(EventPublicSearchParam param) {
        log.info("Request to get published events.");

        if (param.getStart().isAfter(param.getEnd())) {
            log.error("NotValid. When searching for published events, rangeStart is after rangeEnd.");
            throw new NotValidException("The start of the range must be before the end of the range.");
        }

        var events = eventStorage.searchPublicEvents(param);

        var comparator = switch (param.getSort()) {
            case EVENT_DATE -> Comparator.comparing(ShortEventDto::getEventDate);
            case VIEWS -> Comparator.comparing(ShortEventDto::getViews, Comparator.reverseOrder());
            default -> Comparator.comparing(ShortEventDto::getId);
        };

        var view = getView(events, false);

        return events.stream()
                .map(e -> eventMapper.toShortDto(e, view.getOrDefault(e.getId(), 0L)))
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public LongEventDto getPublicById(Long id) {
        log.info("Request to get published event with id {}", id);

        var event = eventStorage.findPublicEventById(id).orElseThrow(() -> {
            log.error("NotFound. Event with id {} not found.", id);
            return new NotFoundException(String.format("Event with id = %d was not found", id));
        });

        var view = statsService.getView(Collections.singletonList(event.getId()), true);
        return eventMapper.toLongDto(event, view.getOrDefault(event.getId(), 0L));
    }
}
