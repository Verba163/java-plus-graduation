package ru.practicum.category.controller.feign;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.category.service.feign.FeignCategoryService;
import ru.practicum.interaction.category.dto.CategoryDto;

import java.util.List;

import static ru.practicum.interaction.feign.FeignPathConstants.CAT_GET_BY_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FeignCategoryController {

    private final FeignCategoryService feignCategoryService;

    @GetMapping(CAT_GET_BY_ID)
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryDto> getCategoryByIds(@RequestParam(name = "categoryIds", required = false)
                                              List<Long> categoryIds) {
        return feignCategoryService.getCategoryByIds(categoryIds);
    }
}
