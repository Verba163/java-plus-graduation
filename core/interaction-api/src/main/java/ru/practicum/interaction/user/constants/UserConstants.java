package ru.practicum.interaction.user.constants;

public interface UserConstants {

    String ADMIN_USER = "/admin/users";
    String USER_ID_PATH = "/{user-id}";
    String USER_ID = "user-id";
    String NEW_EMAIL_REGEXP = "(?i)^[A-Za-z0-9._%+-]{1,63}@[A-Za-z0-9]([A-Za-z0-9-]{0,61}[A-Za-z0-9])?(\\.[A-Za-z0-9]([A-Za-z0-9-]{0,62}[A-Za-z0-9])?)*\\.[A-Za-z0-9]{2,63}$";
}
