package ru.practicum.ewm.category.storage;

import ru.practicum.ewm.category.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
