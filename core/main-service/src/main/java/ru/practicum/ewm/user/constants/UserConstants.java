package ru.practicum.ewm.user.constants;

public interface UserConstants {

    public static final String ADMIN_USER = "/admin/users";
    public static final String USER_ID_PATH = "/{user-id}";
    public static final String USER_ID = "user-id";
    public static final String NEW_EMAIL_REGEXP = "(?i)^[A-Za-z0-9._%+-]{1,63}@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,62}[A-Za-z0-9])?)*\\.[A-Za-z0-9]{2,63}$";
}
