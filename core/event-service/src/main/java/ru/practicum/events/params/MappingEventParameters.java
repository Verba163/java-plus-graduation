package ru.practicum.events.params;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.model.Event;
import ru.practicum.interaction.category.dto.CategoryDto;
import ru.practicum.interaction.comments.dto.CommentShortDto;
import ru.practicum.interaction.user.dto.UserShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MappingEventParameters {
    Event event;
    CategoryDto categoryDto;
    UserShortDto initiator;
    Long confirmedRequests;
    Double rating;
    List<CommentShortDto> comments;
}
