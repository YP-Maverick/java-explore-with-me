package ru.practicum.ewm.main.events.storage;


import ru.practicum.ewm.main.events.model.Event;
import ru.practicum.ewm.main.events.params.EventAdminSearchParam;
import ru.practicum.ewm.main.events.params.EventPublicSearchParam;

import java.util.List;

public interface CustomizedEventStorage {

    List<Event> searchEventsForAdmin(EventAdminSearchParam param);

    List<Event> searchPublicEvents(EventPublicSearchParam param);
}
