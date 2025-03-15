package com.mos.backend.common.utils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class TimeCalculator {
    public static Duration calculateDurationToMidNight() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.plusDays(1).with(LocalTime.MIDNIGHT);
        return Duration.between(now, midnight);
    }
}
