package ru.practicum.ewm.main.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.main.categories.mapper.CategoryMapper;
import ru.practicum.ewm.main.categories.model.Category;
import ru.practicum.ewm.main.events.dto.CreateEventDto;
import ru.practicum.ewm.main.events.dto.LongEventDto;
import ru.practicum.ewm.main.events.dto.ShortEventDto;
import ru.practicum.ewm.main.events.model.Event;
import ru.practicum.ewm.main.events.model.EventStatus;
import ru.practicum.ewm.main.user.mapper.UserMapper;
import ru.practicum.ewm.main.user.model.User;

import java.time.LocalDateTime;

@Mapper(uses = {UserMapper.class, CategoryMapper.class}, componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE, imports = {LocalDateTime.class})
public interface EventMapper {
    @Mapping(source = "views", target = "views")
    ShortEventDto toShortDto(Event event, Long views);

    @Mapping(source = "event.lat", target = "location.lat")
    @Mapping(source = "event.lon", target = "location.lon")
    @Mapping(source = "views", target = "views")
    LongEventDto toLongDto(Event event, Long views);

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(source = "createEventDto.location.lat", target = "lat")
    @Mapping(source = "createEventDto.location.lon", target = "lon")
    @Mapping(source = "initiator", target = "initiator")
    @Mapping(source = "category", target = "category")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now())")
    @Mapping(target = "confirmedRequests", constant = "0")
    Event toEvent(CreateEventDto createEventDto, User initiator, Category category, EventStatus state);
}
