package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryParams;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoryRepository;
import ru.practicum.ewm.error.exception.ConflictException;
import ru.practicum.ewm.error.exception.NotFoundException;
import ru.practicum.ewm.events.storage.EventsRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventsRepository eventsRepository;
    private final CategoryMapper categoryMapper;


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
        Category category = checkAndGetCategory(catId);
        return categoryMapper.toCategoryDto(category);
    }

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
        checkAndGetCategory(catId);

        if (eventsRepository.countByCategoryId(catId) > 0) {
            throw new ConflictException(String.format("Category with id '%d' is not empty", catId));
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

        Category oldCategory = checkAndGetCategory(categoryId);

        if (categoryRepository.existsByNameAndIdNot(newName, categoryId)) {
            log.error("Conflict when trying update category id={} with new name: {}", categoryId, newName);
            throw new ConflictException(
                    String.format("Cannot update category id %d: name '%s' already exists", categoryId, newName));
        }

        oldCategory.setName(newName);
        categoryRepository.saveAndFlush(oldCategory);

        return categoryMapper.toCategoryDto(oldCategory);
    }

    private Category checkAndGetCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id '%d' not found", catId)
                ));
    }
}
