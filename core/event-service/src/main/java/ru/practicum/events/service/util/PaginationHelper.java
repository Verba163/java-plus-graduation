package ru.practicum.events.service.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PaginationHelper {

    public Pageable createPageableObject(int from, int size) {
        if (from < 0 || size <= 0) {
            throw new IllegalArgumentException("'from' must be >= 0 and 'size' must be > 0.");
        }
        return PageRequest.of(from / size, size);
    }

}