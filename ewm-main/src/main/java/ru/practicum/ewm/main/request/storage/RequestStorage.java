package ru.practicum.ewm.main.request.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.request.model.Request;

import java.util.List;

public interface RequestStorage extends JpaRepository<Request, Long> {
    Request findByRequesterIdAndEventId(Long requesterId, Long eventId);

    List<Request> findByIdInAndEventId(List<Long> requestIds, Long eventId);

    List<Request> findByRequesterId(Long requesterId);

    List<Request> findByEventId(Long eventId);
}

