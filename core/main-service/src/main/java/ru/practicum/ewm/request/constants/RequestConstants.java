package ru.practicum.ewm.request.constants;

public interface RequestConstants {
    public static final String USERS = "/users";
    public static final String REQUEST_BASE_PATH = "{user-id}/requests";
    public static final String USER_ID = "user-id";
    public static final String REQUEST_BASE_PATCH_PATH = "{user-id}/requests/{request-id}/cancel";
    public static final String REQUEST_ID = "request-id";
}
