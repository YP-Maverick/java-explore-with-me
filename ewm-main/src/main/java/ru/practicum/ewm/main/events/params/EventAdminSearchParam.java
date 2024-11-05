package ru.practicum.ewm.main.events.params;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.main.events.model.EventStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventAdminSearchParam {
    private List<Long> usersId;
    private List<EventStatus> states;
    private List<Long> categoriesId;
    private LocalDateTime start;
    private LocalDateTime end;
    private Pageable pageable;
}
