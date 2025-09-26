package ru.practicum.category.controller.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.admin.AdminCategoryService;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.category.dto.CategoryParams;
import ru.practicum.interaction.category.dto.NewCategoryDto;

import static ru.practicum.interaction.category.constants.CategoryApiPath.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping(ADMIN_API_PREFIX)
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Received POST request to create category: {}", newCategoryDto);
        return adminCategoryService.createCategory(newCategoryDto);
    }

    @DeleteMapping(ADMIN_API_PREFIX + CAT_ID_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable(CAT_ID) Long catId) {
        log.info("Received DELETE request to delete category with id: {}", catId);
        adminCategoryService.deleteCategory(catId);
    }

    @PatchMapping(ADMIN_API_PREFIX + CAT_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(@PathVariable(CAT_ID) Long catId,
                                      @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Received PATCH request to update category with id : {}, update : {}", catId, newCategoryDto);

        CategoryParams categoryParams = CategoryParams.builder()
                .newCategoryDto(newCategoryDto)
                .catId(catId)
                .build();

        return adminCategoryService.updateCategory(categoryParams);
    }
}
