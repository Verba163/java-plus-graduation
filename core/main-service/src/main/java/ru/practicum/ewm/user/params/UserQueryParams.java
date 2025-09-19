package ru.practicum.ewm.user.params;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserQueryParams {
    List<Long> ids;

    @Min(value = 0, message = "Parameters 'from' can not be less then zero")
    Integer from;

    @Min(value = 1, message = "Parameters 'size' can not be less then zero")
    Integer size;

    public UserQueryParams() {
        this.from = 0;
        this.size = 10;
    }
}
