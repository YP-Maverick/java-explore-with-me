package ru.practicum.ewm.main.events.params;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.main.events.service.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventPublicSearchParam {
    private String text;
    private List<Long> categoriesId;
    private Boolean paid;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean onlyAvailable;
    private EventSort sort;
    private int from;
    private int size;
}
