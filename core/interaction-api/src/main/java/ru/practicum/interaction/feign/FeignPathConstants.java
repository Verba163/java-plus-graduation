package ru.practicum.interaction.feign;

public interface FeignPathConstants {
    String CAT_GET_BY_ID = "/api/categories/by-ids";

    String FIRST_COMMENTS_SEARCH = "/api/comments/find-first-comments/{event-id}";
    String COMMENTS_FOR_EVENT = "/api/comments/get-comments-for-event";
    String GET_COMMENTS_NUMBER = "/api/comments/get-comments-number";

    String EVENT_COUNT_BY_CATEGORY = "/api/events/count-by-category/{cat-id}";
    String GET_EVENT_BY_ID = "/api/events/get-event-by-id/{event-id}";
    String GET_EVENT_SHORT_DTO = "/api/events/get-events-short-dto/short";
    String CHECK_EVENT_EXISTING = "/api/events/check-event-existing";

    String GET_USER_REQUESTS = "/api/requests/get-user-request/user/{user-id}/event/{event-id}";
    String CONFIRMED_COUNT = "/api/requests/confirmed-count";
    String GET_REQUEST_BY_IDS = "/api/requests/get-requests-by-ids/confirmed-count";
    String UPDATE_REQUESTS = "/api/requests/update-requests/confirmed-count";
    String GET_EVENT_REQUESTS = "/api/requests/get-event-requests/event/{event-id}";

    String GET_USER_SHORT_DTO = "/api/users/get-user-short-dto/{user-id}";
}
