package ru.practicum.category.service.validation;

import ru.practicum.category.model.Category;

public interface CategoryValidationChecker {

    Category checkAndGetCategory(Long catId);
}
