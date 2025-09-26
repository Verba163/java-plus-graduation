package ru.practicum.category.service.validation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.category.storage.CategoryRepository;
import ru.practicum.interaction.error.exception.NotFoundException;

@Service
@RequiredArgsConstructor
public class CategoryValidationCheckerImpl implements CategoryValidationChecker {

    private final CategoryRepository categoryRepository;

    @Override
    public Category checkAndGetCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Category with id '%d' not found", catId)
                ));
    }
}