package ru.practicum.ewm.main.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.main.events.dto.LongEventDto;
import ru.practicum.ewm.main.events.dto.UpdateEventDto;
import ru.practicum.ewm.main.events.model.EventStatus;
import ru.practicum.ewm.main.events.params.EventAdminSearchParam;
import ru.practicum.ewm.main.events.service.EventService;
import ru.practicum.ewm.stats.dto.Formatter;

import java.time.LocalDateTime;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @PatchMapping({"/{eventId}", "/{eventId}/"})
    public LongEventDto updateEventByAdmin(
            @PathVariable
            @Positive(message = "Event's id should be positive")
            Long eventId,
            @Valid @RequestBody UpdateEventDto updateDto
    ) {
        return eventService.updateByAdmin(eventId, updateDto);
    }

    @GetMapping()
    public List<LongEventDto> getAllByAdmin(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<EventStatus> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(defaultValue = "0")
            @PositiveOrZero(message = "Parameter 'from' shouldn't be negative")
            int from,
            @RequestParam(defaultValue = "10")
            @Positive(message = "Parameter 'size' should be positive")
            int size
    ) {
        int page = from / size;
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        LocalDateTime start = (rangeStart != null) ? LocalDateTime.parse(rangeStart, Formatter.getFormatter())
                : LocalDateTime.now();
        LocalDateTime end = (rangeEnd != null) ? LocalDateTime.parse(rangeEnd, Formatter.getFormatter())
                : LocalDateTime.now().plusYears(20);
        return eventService.getEventsForAdmin(EventAdminSearchParam.builder()
                .usersId(users)
                .states(states)
                .categoriesId(categories)
                .start(start)
                .end(end)
                .pageable(PageRequest.of(page, size, sort))
                .build());
    }
}
