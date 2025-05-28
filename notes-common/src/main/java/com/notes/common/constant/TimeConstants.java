package com.notes.common.constant;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class TimeConstants {
    public static final LocalDateTime DEFAULT_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);
}
