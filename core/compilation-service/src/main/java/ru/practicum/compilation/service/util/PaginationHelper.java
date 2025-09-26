package ru.practicum.compilation.service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationHelper {
    public static Pageable calculatePageable(Long from, Integer size) {
        int page = (int) (from / size);
        return PageRequest.of(page, size);
    }
}