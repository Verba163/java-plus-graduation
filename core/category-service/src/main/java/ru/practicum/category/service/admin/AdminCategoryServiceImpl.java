package ru.practicum.category.service.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.service.integrity.CategoryIntegrityChecker;
import ru.practicum.category.service.validation.CategoryValidationChecker;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.category.dto.CategoryParams;
import ru.practicum.interaction.category.dto.NewCategoryDto;
import ru.practicum.interaction.error.exception.ConflictException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CategoryIntegrityChecker integrityChecker;
    private final CategoryValidationChecker categoryValidationChecker;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {

        String name = newCategoryDto.getName();
        log.info("Creating category: {}", name);

        if (categoryRepository.existsByName(name)) {
            log.error("Conflict when create category with name: {}", name);
            throw new ConflictException(String.format(
                    "Category with name '%s' already exists", name
            ));
        }

        Category category = categoryMapper.toCategoryEntity(newCategoryDto);

        Category savedCategory = categoryRepository.save(category);

        return categoryMapper.toCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        categoryValidationChecker.checkAndGetCategory(catId);

        if (!integrityChecker.canDeleteCategory(catId)) {
            throw new ConflictException(
                    String.format("Category with id '%d' is not empty", catId));
        }

        log.info("Deleting category id: {}", catId);
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(CategoryParams updateCategory) {

        Long categoryId = updateCategory.getCatId();
        NewCategoryDto newCategoryDto = updateCategory.getNewCategoryDto();
        String newName = newCategoryDto.getName();

        log.info("Updating category id={} with new name: {}", categoryId, newName);

        Category oldCategory = categoryValidationChecker.checkAndGetCategory(categoryId);

        if (categoryRepository.existsByNameAndIdNot(newName, categoryId)) {
            log.error("Conflict when trying update category id={} with new name: {}", categoryId, newName);
            throw new ConflictException(
                    String.format("Cannot update category id %d: name '%s' already exists", categoryId, newName));
        }

        oldCategory.setName(newName);
        categoryRepository.saveAndFlush(oldCategory);

        return categoryMapper.toCategoryDto(oldCategory);
    }

}
