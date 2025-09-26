package ru.practicum.category.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.service.user.UserCategoryService;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.category.dto.CategoryParams;

import java.util.List;

import static ru.practicum.interaction.category.constants.CategoryApiPath.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserCategoryController {

    private final UserCategoryService userCategoryService;

    @GetMapping(PUBLIC_API_PREFIX)
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategory(@RequestParam(defaultValue = "0") Long from,
                                         @RequestParam(defaultValue = "10") Long size) {
        log.info("Received GET request for all category with param: from: {}, size: {}", from, size);

        CategoryParams categoryParams = CategoryParams.builder()
                .from(from)
                .size(size)
                .build();

        return userCategoryService.getCategory(categoryParams);
    }

    @GetMapping(PUBLIC_API_PREFIX + CAT_ID_PATH)
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable(CAT_ID) Long catId) {
        log.info("Received GET request for category with id: {}", catId);
        return userCategoryService.getCategoryById(catId);
    }
}
