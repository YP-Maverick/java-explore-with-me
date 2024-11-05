package ru.practicum.ewm.main.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.main.compilation.dto.CompilationDto;
import ru.practicum.ewm.main.compilation.dto.CreateCompilationDto;
import ru.practicum.ewm.main.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.main.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.main.compilation.model.Compilation;
import ru.practicum.ewm.main.compilation.storage.CompilationStorage;
import ru.practicum.ewm.main.events.model.Event;
import ru.practicum.ewm.main.events.storage.EventStorage;
import ru.practicum.ewm.main.exception.ConflictException;
import ru.practicum.ewm.main.exception.NotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationStorage compilationStorage;
    private final CompilationMapper compilationMapper;
    private final EventStorage eventStorage;

    @Override
    public CompilationDto createCompilation(CreateCompilationDto createDto) {
        log.info("Request to create event compilation with title {}.", createDto.getTitle());

        Set<Event> events = (createDto.getEvents() != null && !createDto.getEvents().isEmpty())
                ? eventStorage.findByIdIn(createDto.getEvents())
                : new HashSet<>();

        if (createDto.getEvents() != null && events.size() != createDto.getEvents().size()) {
            Set<Long> receivedEventsId = events.stream().map(Event::getId).collect(Collectors.toSet());
            createDto.getEvents().removeAll(receivedEventsId);
            Long nonExist = createDto.getEvents().stream().findFirst().get();

            log.error("Conflict. Event with id = {} in the created compilation does not exist.", nonExist);
            throw new ConflictException(
                    String.format("Cannot create compilation with non-existent event with id = %d", nonExist)
            );
        }
        Compilation compilation = compilationStorage.save(compilationMapper.toModel(createDto, events));
        return compilationMapper.toDto(compilation);
    }

    private Compilation getById(Long id) {
        return compilationStorage.findById(id).orElseThrow(() -> {
            log.error("NotFound. Compilation with id {} does not exist.", id);
            return new NotFoundException(String.format("Compilation with id %d does not exist.", id));
        });
    }

    @Override
    public CompilationDto updateCompilation(Long id, UpdateCompilationDto dto) {
        log.info("Request to update event compilation with id {}.", id);

        Compilation toUpdate = getById(id);

        if (dto.getTitle() != null) toUpdate.setTitle(dto.getTitle());
        if (dto.getPinned() != null) toUpdate.setPinned(dto.getPinned());

        Set<Long> pastEvents = toUpdate.getEvents().stream().map(Event::getId).collect(Collectors.toSet());
        if (dto.getEvents() != null && !dto.getEvents().equals(pastEvents)) {
            Set<Event> events = !dto.getEvents().isEmpty() ? eventStorage.findByIdIn(dto.getEvents())
                    : new HashSet<>();
            toUpdate.setEvents(events);
        }

        Compilation compilation = compilationStorage.save(toUpdate);
        return compilationMapper.toDto(compilation);
    }

    @Override
    public Long deleteCompilation(Long id) {
        log.info("Request to delete event compilation with id {}.", id);

        // Check for existence of the compilation
        getByIdCompilation(id);

        compilationStorage.deleteById(id);
        return id;
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getByIdCompilation(Long id) {
        log.info("Request to find event compilation with id {}.", id);

        Compilation compilation = getById(id);
        return compilationMapper.toDto(compilation);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Pageable pageable) {
        log.info("Request to find event compilations.");

        List<Compilation> result = (pinned != null) ? compilationStorage.findByPinned(pinned, pageable)
                : compilationStorage.findAll(pageable).toList();
        return result.stream()
                .map(compilationMapper::toDto)
                .collect(Collectors.toList());
    }
}
