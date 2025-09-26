package ru.practicum.category.service.user;

import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.category.dto.CategoryParams;

import java.util.List;

public interface UserCategoryService {

    List<CategoryDto> getCategory(CategoryParams categoryParams);

    CategoryDto getCategoryById(Long catId);
}
