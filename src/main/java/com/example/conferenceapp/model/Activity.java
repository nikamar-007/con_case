package com.example.conferenceapp.model;

import java.time.LocalTime;

public class Activity {
    private final int id;
    private final String eventTitle;
    private final String title;
    private final Integer dayNum;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String direction;
    private final String moderator;

    public Activity(int id,
                    String eventTitle,
                    String title,
                    Integer dayNum,
                    LocalTime startTime,
                    LocalTime endTime,
                    String direction,
                    String moderator) {
        this.id = id;
        this.eventTitle = eventTitle;
        this.title = title;
        this.dayNum = dayNum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.direction = direction;
        this.moderator = moderator;
    }

    public int getId() { return id; }
    public String getEventTitle() { return eventTitle; }
    public String getTitle() { return title; }
    public Integer getDayNum() { return dayNum; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getDirection() { return direction; }
    public String getModerator() { return moderator; }
}
