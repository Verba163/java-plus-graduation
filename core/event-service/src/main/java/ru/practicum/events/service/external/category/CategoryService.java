package ru.practicum.events.service.external.category;

import ru.practicum.interaction.category.dto.CategoryDto;

public interface CategoryService {

    CategoryDto getCategoryWithCheck(long categoryId);
}
