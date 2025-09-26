package ru.practicum.category.service.integrity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.interaction.feign.clients.EventFeignClient;

@Service
@RequiredArgsConstructor
public class CategoryIntegrityCheckerImpl implements CategoryIntegrityChecker {

    private final EventFeignClient eventFeignClient;

    @Override
    public boolean canDeleteCategory(Long categoryId) {
        return eventFeignClient.countEventsByCategoryId(categoryId) == 0;
    }
}