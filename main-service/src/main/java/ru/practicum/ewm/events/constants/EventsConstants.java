package ru.practicum.ewm.events.constants;

public class EventsConstants {
    public static final String DATA_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String PRIVATE_API_PREFIX = "/users";
    public static final String PRIVATE_API_PREFIX_USER_ID_EVENT_ID = "/{user-id}/events/{event-id}";
    public static final String PRIVATE_API_USER_EVENT_REQUESTS = "/{user-id}/events/{event-id}/requests";

    public static final String ADMIN_API_PREFIX = "/admin/events";

    public static final String PUBLIC_API_PREFIX = "/events";
    public static final String PUBLIC_API_PREFIX_USER_ID = "/{user-id}/events";
    public static final String PUBLIC_API_PREFIX_COMMENTS = "/events/{event-id}/comments";

    public static final String USER_ID = "user-id";
    public static final String EVENT_ID = "event-id";
    public static final String EVENT_ID_PATH = "/{event-id}";
}
