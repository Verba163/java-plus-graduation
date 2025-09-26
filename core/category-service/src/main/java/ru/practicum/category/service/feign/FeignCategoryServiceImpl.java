package ru.practicum.category.service.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.interaction.category.dto.CategoryDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeignCategoryServiceImpl implements FeignCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getCategoryByIds(List<Long> categoryIds) {
        log.info("Fetching category by ids: ");
        if (categoryIds == null || categoryIds.isEmpty()) {
            return List.of();
        }
        List<Category> category = categoryRepository.findAllById(categoryIds);

        return category.stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }
}
