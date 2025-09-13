package ru.practicum.ewm.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Util {
    public static LocalDateTime getNowTruncatedToSeconds() {
        return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }
}
