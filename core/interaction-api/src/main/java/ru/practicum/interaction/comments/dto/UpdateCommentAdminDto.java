package ru.practicum.interaction.comments.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.interaction.comments.enums.AdminAction;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class UpdateCommentAdminDto {

    AdminAction action;
}
