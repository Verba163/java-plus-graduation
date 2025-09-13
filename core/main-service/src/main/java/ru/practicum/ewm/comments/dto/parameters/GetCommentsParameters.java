package ru.practicum.ewm.comments.dto.parameters;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.comments.model.CommentStatus;

import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetCommentsParameters {

    Long userId;
    List<Long> eventIds;
    CommentStatus status;

    @Min(value = 0, message = "Parameters 'from' can not be less then zero")
    Integer from;

    @Min(value = 1, message = "Parameters 'size' can not be less then one")
    Integer size;

    public GetCommentsParameters() {
        this.size = 10;
        this.from = 0;
    }
}