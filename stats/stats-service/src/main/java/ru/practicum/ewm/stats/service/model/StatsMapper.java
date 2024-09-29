package ru.practicum.ewm.stats.service.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.stats.dto.dto.CreateStatsDto;
import ru.practicum.ewm.stats.dto.dto.StatsDto;
import ru.practicum.ewm.stats.service.view.StatsView;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatsMapper {
    StatsModel toModel(CreateStatsDto createStatsDto);

    StatsDto toDto(StatsView statsView);

    CreateStatsDto toCreationDto(StatsModel statsModel);
}
