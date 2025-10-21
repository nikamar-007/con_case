package com.example.conferenceapp.dao;

import com.example.conferenceapp.model.Activity;
import com.example.conferenceapp.util.DBUtil;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ActivityDao {

    private static final String BASE_SELECT = """
            SELECT a.id,
                   a.title,
                   a.day_num,
                   a.start_time,
                   a.end_time,
                   e.title            AS event_title,
                   e.start_datetime   AS event_start,
                   d.name             AS direction_name,
                   m.full_name        AS moderator_name
            FROM activity a
            JOIN event e      ON a.event_id = e.id
            JOIN direction d  ON e.direction_id = d.id
            LEFT JOIN user m  ON a.moderator_id = m.id
        """;

    public List<Activity> findForParticipant(int participantId, Integer directionId) {
        if (participantId <= 0) {
            return List.of();
        }
        String sql = BASE_SELECT + "\n            JOIN activity_participant ap ON ap.activity_id = a.id\n            WHERE ap.participant_id = ?\n              AND (? IS NULL OR e.direction_id = ?)\n            ORDER BY DATE_ADD(DATE(e.start_datetime), INTERVAL COALESCE(a.day_num,1)-1 DAY), a.start_time";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, participantId);
            if (directionId != null) {
                ps.setInt(2, directionId);
                ps.setInt(3, directionId);
            } else {
                ps.setNull(2, Types.INTEGER);
                ps.setNull(3, Types.INTEGER);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return collect(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return List.of();
    }

    public List<Activity> findByModerator(int moderatorId) {
        String sql = BASE_SELECT + "\n            WHERE a.moderator_id = ?\n            ORDER BY DATE_ADD(DATE(e.start_datetime), INTERVAL COALESCE(a.day_num,1)-1 DAY), a.start_time";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, moderatorId);
            try (ResultSet rs = ps.executeQuery()) {
                return collect(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return List.of();
    }

    public List<Activity> findByJury(int juryId) {
        String sql = BASE_SELECT + "\n            JOIN activity_jury aj ON aj.activity_id = a.id\n            WHERE aj.jury_id = ?\n            ORDER BY DATE_ADD(DATE(e.start_datetime), INTERVAL COALESCE(a.day_num,1)-1 DAY), a.start_time";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, juryId);
            try (ResultSet rs = ps.executeQuery()) {
                return collect(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return List.of();
    }

    private List<Activity> collect(ResultSet rs) throws SQLException {
        List<Activity> list = new ArrayList<>();
        while (rs.next()) {
            Integer dayNum = (Integer) rs.getObject("day_num");
            LocalDate eventStartDate = rs.getTimestamp("event_start") != null
                    ? rs.getTimestamp("event_start").toLocalDateTime().toLocalDate()
                    : null;

            LocalDate activityDate = null;
            if (eventStartDate != null) {
                activityDate = dayNum != null && dayNum > 1
                        ? eventStartDate.plusDays(dayNum - 1)
                        : eventStartDate;
            }

            LocalTime startTime = toLocalTime(rs.getTime("start_time"));
            LocalTime endTime   = toLocalTime(rs.getTime("end_time"));
            LocalDateTime startDateTime = activityDate != null && startTime != null
                    ? LocalDateTime.of(activityDate, startTime)
                    : null;
            LocalDateTime endDateTime = activityDate != null && endTime != null
                    ? LocalDateTime.of(activityDate, endTime)
                    : null;

            list.add(new Activity(
                    rs.getInt("id"),
                    rs.getString("event_title"),
                    rs.getString("title"),
                    dayNum,
                    startDateTime,
                    endDateTime,
                    rs.getString("direction_name"),
                    rs.getString("moderator_name")
            ));
        }
        return list;
    }

    private LocalTime toLocalTime(Time time) {
        return time != null ? time.toLocalTime() : null;
    }
}
