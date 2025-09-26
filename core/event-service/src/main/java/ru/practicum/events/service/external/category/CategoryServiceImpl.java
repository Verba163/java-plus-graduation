package ru.practicum.events.service.external.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.feign.clients.CategoryFeignClient;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryFeignClient categoryFeignClient;

    @Override
    public CategoryDto getCategoryWithCheck(long categoryId) {
        return categoryFeignClient.getCategoryById(categoryId);
    }

}
