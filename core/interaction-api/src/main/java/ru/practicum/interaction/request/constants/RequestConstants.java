package ru.practicum.interaction.request.constants;

public interface RequestConstants {
    String USERS = "/users";
    String REQUEST_BASE_PATH = "/{user-id}/requests";
    String USER_ID = "user-id";
    String REQUEST_BASE_PATCH_PATH = "/{user-id}/requests/{request-id}/cancel";
    String REQUEST_ID = "request-id";
}
