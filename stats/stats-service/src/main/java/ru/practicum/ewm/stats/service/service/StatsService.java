package ru.practicum.ewm.stats.service.service;


import ru.practicum.ewm.stats.dto.dto.CreateStatsDto;
import ru.practicum.ewm.stats.dto.dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    CreateStatsDto save(CreateStatsDto creationDto);

    List<StatsDto> getStats(LocalDateTime start,
                            LocalDateTime end,
                            List<String> uris,
                            boolean unique);
}
