package com.example.conferenceapp.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Activity {
    private final int id;
    private final String eventTitle;
    private final String title;
    private final Integer dayNum;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final String direction;
    private final String moderator;

    public Activity(int id,
                    String eventTitle,
                    String title,
                    Integer dayNum,
                    LocalDateTime startDateTime,
                    LocalDateTime endDateTime,
                    String direction,
                    String moderator) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.title = title;
        this.dayNum = dayNum;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.direction = direction;
        this.moderator = moderator;
    }

    public int getId() { return id; }
    public String getEventTitle() { return eventTitle; }
    public String getTitle() { return title; }
    public Integer getDayNum() { return dayNum; }
    public LocalDateTime getStartDateTime() { return startDateTime; }
    public LocalDateTime getEndDateTime() { return endDateTime; }
    public String getDirection() { return direction; }
    public String getModerator() { return moderator; }

    public LocalDate getEventDate() {
        return startDateTime != null ? startDateTime.toLocalDate() : null;
    }

    public LocalTime getStartTime() {
        return startDateTime != null ? startDateTime.toLocalTime() : null;
    }

    public LocalTime getEndTime() {
        return endDateTime != null ? endDateTime.toLocalTime() : null;
    }
}
