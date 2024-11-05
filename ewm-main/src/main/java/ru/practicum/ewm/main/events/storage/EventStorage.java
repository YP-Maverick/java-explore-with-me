package ru.practicum.ewm.main.events.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventStorage extends JpaRepository<ru.practicum.ewm.main.events.model.Event, Long>, CustomizedEventStorage {
    List<ru.practicum.ewm.main.events.model.Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    Optional<ru.practicum.ewm.main.events.model.Event> findByIdAndInitiatorId(Long id, Long initiatorId);

    @Query("select e from Event e " +
            "where e.id = :id " +
            "and e.state = 'PUBLISHED'")
    Optional<ru.practicum.ewm.main.events.model.Event> findPublicEventById(@Param("id")Long id);

    Set<ru.practicum.ewm.main.events.model.Event> findByIdIn(Collection<Long> eventsId);
}