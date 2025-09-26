package ru.practicum.category.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.validation.CategoryValidationChecker;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.category.dto.CategoryParams;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCategoryServiceImpl implements UserCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryValidationChecker categoryValidationChecker;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategory(CategoryParams categoryParams) {

        int pageNumber = (int) (categoryParams.getFrom() / categoryParams.getSize());
        int pageSize = categoryParams.getSize().intValue();

        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        Page<Category> categoryPage = categoryRepository.findAll(pageable);

        if (categoryParams.getFrom() > categoryPage.getTotalElements()) {
            return Collections.emptyList();
        }

        log.info("Fetching categories: from={}, size={}", categoryParams.getFrom(), pageSize);

        return categoryPage.stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long catId) {
        log.info("Fetching category by id: {}", catId);
        Category category = categoryValidationChecker.checkAndGetCategory(catId);
        return categoryMapper.toCategoryDto(category);
    }
}
