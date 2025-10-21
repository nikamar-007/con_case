package com.example.conferenceapp.util;

import com.example.conferenceapp.model.Activity;

import java.time.format.DateTimeFormatter;

/**
 * Utility methods for presenting activity schedule data inside JavaFX tables.
 */
public final class ActivityFormatter {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private ActivityFormatter() {
    }

    /**
     * Formats the activity date together with the conference day number, or returns an em dash when
     * the source data is incomplete.
     */
    public static String formatDate(Activity activity) {
        if (activity == null || activity.getEventDate() == null) {
            return "—";
        }
        String date = activity.getEventDate().format(DATE_FMT);
        Integer dayNum = activity.getDayNum();
        if (dayNum != null) {
            return date + " (День " + dayNum + ")";
        }
        return date;
    }

    /**
     * Formats the start and end time range using HH:mm placeholders when values are missing.
     */
    public static String formatTime(Activity activity) {
        if (activity == null) {
            return "—";
        }
        if (activity.getStartTime() == null && activity.getEndTime() == null) {
            return "—";
        }
        String start = activity.getStartTime() != null
                ? activity.getStartTime().format(TIME_FMT)
                : "??";
        String end = activity.getEndTime() != null
                ? activity.getEndTime().format(TIME_FMT)
                : "??";
        return start + " – " + end;
    }
}
