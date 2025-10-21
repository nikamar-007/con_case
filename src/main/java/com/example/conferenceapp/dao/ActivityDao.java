package com.example.conferenceapp.dao;

import com.example.conferenceapp.model.Activity;
import com.example.conferenceapp.util.DBUtil;

import java.sql.*;
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
                   e.title  AS event_title,
                   d.name   AS direction_name,
                   m.full_name AS moderator_name
            FROM activity a
            JOIN event e      ON a.event_id = e.id
            JOIN direction d  ON e.direction_id = d.id
            LEFT JOIN user m  ON a.moderator_id = m.id
        """;

    public List<Activity> findForParticipant(Integer directionId) {
        String sql = BASE_SELECT + "\n            WHERE (? IS NULL OR e.direction_id = ?)\n            ORDER BY e.start_datetime, a.start_time";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (directionId != null) {
                ps.setInt(1, directionId);
                ps.setInt(2, directionId);
            } else {
                ps.setNull(1, Types.INTEGER);
                ps.setNull(2, Types.INTEGER);
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
        String sql = BASE_SELECT + "\n            WHERE a.moderator_id = ?\n            ORDER BY e.start_datetime, a.start_time";
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
        String sql = BASE_SELECT + "\n            JOIN activity_jury aj ON aj.activity_id = a.id\n            WHERE aj.jury_id = ?\n            ORDER BY e.start_datetime, a.start_time";
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
            list.add(new Activity(
                    rs.getInt("id"),
                    rs.getString("event_title"),
                    rs.getString("title"),
                    (Integer) rs.getObject("day_num"),
                    toLocalTime(rs.getTime("start_time")),
                    toLocalTime(rs.getTime("end_time")),
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
