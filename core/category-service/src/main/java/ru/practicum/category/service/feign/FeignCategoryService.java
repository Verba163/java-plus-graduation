package ru.practicum.category.service.feign;

import ru.practicum.interaction.category.dto.CategoryDto;

import java.util.List;

public interface FeignCategoryService {

    List<CategoryDto> getCategoryByIds(List<Long> categoryIds);
}
