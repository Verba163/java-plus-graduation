package ru.practicum.category.service.integrity;

public interface CategoryIntegrityChecker {

    boolean canDeleteCategory(Long categoryId);
}
