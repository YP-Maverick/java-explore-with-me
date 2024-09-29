package ru.practicum.ewm.stats.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.dto.CreateStatsDto;
import ru.practicum.ewm.stats.dto.dto.StatsDto;
import ru.practicum.ewm.stats.service.model.StatsMapper;
import ru.practicum.ewm.stats.service.model.StatsModel;
import ru.practicum.ewm.stats.service.storage.StatsStorage;
import ru.practicum.ewm.stats.service.view.StatsView;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsStorage statsStorage;
    private final StatsMapper statsMapper;

    @Override
    public CreateStatsDto save(CreateStatsDto creationDto) {
        log.info("Запрос на добавление статистики по эндпоинту {}", creationDto.getUri());

        StatsModel newStats = statsStorage.save(statsMapper.toModel(creationDto));
        return statsMapper.toCreationDto(newStats);
    }

    @Transactional(readOnly = true)
    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Запрос на получение статистики по эндпоинту/-ам {}", uris);

        List<StatsView> views;
        if (unique) {
            if (uris.isEmpty()) {
                views = statsStorage.getStatsWithoutUrisUnique(start, end);
            } else views = statsStorage.getStatsWithUnique(start, end, uris);
        } else {
            if (uris.isEmpty()) {
                views = statsStorage.getStatsWithoutUris(start, end);
            } else views = statsStorage.getStats(start, end, uris);
        }
        return views.stream()
                .map(statsMapper::toDto)
                .collect(Collectors.toList());
    }
}
