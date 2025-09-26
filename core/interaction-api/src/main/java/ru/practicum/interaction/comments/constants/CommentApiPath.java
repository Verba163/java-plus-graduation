package ru.practicum.interaction.comments.constants;

public interface CommentApiPath {
    String PRIVATE_API_PREFIX = "/users/{user-id}/comments";
    String PRIVATE_API_PREFIX_COMMENT_ID = "/users/{user-id}/comments/{comment-id}";

    String ADMIN_API_PREFIX = "/admin/comments";
    String ADMIN_API_PREFIX_COMMENT_ID = "/admin/comments/{comment-id}";

    String USER_ID = "user-id";
    String COMMENT_ID = "comment-id";
}
