package ru.practicum.ewm.main.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.main.request.dto.RequestDto;
import ru.practicum.ewm.main.request.model.Request;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RequestMapper {
    @Mapping(source = "request.requester.id", target = "requester")
    @Mapping(source = "request.event.id", target = "event")
    RequestDto toDto(Request request);
}
