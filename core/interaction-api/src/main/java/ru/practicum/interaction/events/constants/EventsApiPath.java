package ru.practicum.interaction.events.constants;

public interface EventsApiPath {
    String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    String PRIVATE_API_PREFIX = "/users";
    String PRIVATE_API_PREFIX_USER_ID_EVENT_ID = "/{user-id}/events/{event-id}";
    String PRIVATE_API_USER_EVENT_REQUESTS = "/{user-id}/events/{event-id}/requests";

    String ADMIN_API_PREFIX = "/admin/events";

    String PUBLIC_API_PREFIX = "/events";
    String PUBLIC_API_PREFIX_USER_ID = "/{user-id}/events";
    String PUBLIC_API_PREFIX_COMMENTS = "/events/{event-id}/comments";

    String USER_ID = "user-id";
    String EVENT_ID = "event-id";
    String EVENT_ID_PATH = "/{event-id}";
}
