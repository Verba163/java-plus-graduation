package ru.practicum.interaction.feign.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.feign.config.FeignConfig;

import java.util.List;

import static ru.practicum.interaction.category.constants.CategoryApiPath.*;
import static ru.practicum.interaction.feign.FeignPathConstants.CAT_GET_BY_ID;

@FeignClient(name = "category-service", configuration = {FeignConfig.class})
public interface CategoryFeignClient {

    @GetMapping(CAT_GET_BY_ID)
    List<CategoryDto> getCategoryByIds(@RequestParam(name = "categoryIds", required = false)
                                       List<Long> categoryIds);

    @GetMapping(PUBLIC_API_PREFIX + CAT_ID_PATH)
    CategoryDto getCategoryById(@PathVariable(CAT_ID) Long catId);
}

