package ru.practicum.ewm.comments.constants;

public class CommentConstants {
    public static final String PRIVATE_API_PREFIX = "/users/{user-id}/comments";
    public static final String PRIVATE_API_PREFIX_COMMENT_ID = "/users/{user-id}/comments/{comment-id}";

    public static final String ADMIN_API_PREFIX = "/admin/comments";
    public static final String ADMIN_API_PREFIX_COMMENT_ID = "/admin/comments/{comment-id}";

    public static final String USER_ID = "user-id";
    public static final String COMMENT_ID = "comment-id";
}
