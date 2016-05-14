package com.codingbuffalo.businesstime.util;

public class TimeFormatter {
    /* Slightly borrowed from Android's android.text.format.DateUtils.formatElapsedTime() */
    public static String formatElapsedTime(long elapsedSeconds) {
        // Break the elapsed seconds into hours, minutes, and seconds.
        long hours = 0;
        long minutes = 0;
        if (elapsedSeconds >= 3600) {
            hours = elapsedSeconds / 3600;
            elapsedSeconds -= hours * 3600;
        }
        if (elapsedSeconds >= 60) {
            minutes = elapsedSeconds / 60;
            elapsedSeconds -= minutes * 60;
        }

        return String.format("%d:%02d", hours, minutes);
    }
}
