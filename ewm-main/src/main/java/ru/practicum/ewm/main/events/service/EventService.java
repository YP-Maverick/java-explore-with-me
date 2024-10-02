package ru.practicum.ewm.main.events.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.events.dto.CreateEventDto;
import ru.practicum.ewm.main.events.dto.LongEventDto;
import ru.practicum.ewm.main.events.dto.ShortEventDto;
import ru.practicum.ewm.main.events.dto.UpdateEventDto;
import ru.practicum.ewm.main.events.params.EventAdminSearchParam;
import ru.practicum.ewm.main.events.params.EventPublicSearchParam;


import java.util.List;

public interface EventService {
    
    LongEventDto create(Long userId, CreateEventDto createEventDto);

    LongEventDto updateByUser(Long userId, Long eventId, UpdateEventDto updateDto);

    LongEventDto updateByAdmin(Long eventId, UpdateEventDto updateDto);

    LongEventDto getByUserId(Long userId, Long eventId);

    List<ShortEventDto> getAllByUser(Long userId, Pageable pageable);

    List<LongEventDto> getEventsForAdmin(EventAdminSearchParam param);

    List<ShortEventDto> getPublicEvents(EventPublicSearchParam param);

    LongEventDto getPublicById(Long id);
}
