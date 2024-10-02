package ru.practicum.ewm.main.categories.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.main.categories.model.Category;

public interface CategoryStorage extends JpaRepository<Category, Long> {
}
