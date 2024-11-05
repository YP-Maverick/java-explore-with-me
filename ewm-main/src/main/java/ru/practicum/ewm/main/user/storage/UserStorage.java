package ru.practicum.ewm.main.user.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.user.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage extends JpaRepository<User, Long> {
    List<User> findByIdIn(Collection<Long> ids, Pageable pageable);
}