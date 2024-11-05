package ru.practicum.ewm.main.compilation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.events.mapper.EventMapper;
import ru.practicum.ewm.main.events.model.Event;

import java.util.Set;

@Mapper(uses = {EventMapper.class}, componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompilationMapper {

    @Mapping(target = "id", expression = "java(null)")
    @Mapping(source = "events", target = "events")
    Compilation toModel(CreateCompilationDto createCompilationDto, Set<Event> events);

    CompilationDto toDto(Compilation compilation);
}
