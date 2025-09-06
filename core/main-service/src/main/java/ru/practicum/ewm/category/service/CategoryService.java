package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryParams;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getCategory(CategoryParams categoryParams);

    CategoryDto getCategoryById(Long catId);

    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(CategoryParams updateCategory);
}
