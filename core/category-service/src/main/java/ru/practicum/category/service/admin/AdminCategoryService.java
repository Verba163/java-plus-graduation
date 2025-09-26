package ru.practicum.category.service.admin;

import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.category.dto.CategoryParams;
import ru.practicum.interaction.category.dto.NewCategoryDto;

public interface AdminCategoryService {

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(CategoryParams updateCategory);
}
