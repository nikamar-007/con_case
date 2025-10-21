package com.example.conferenceapp.dao;

import com.example.conferenceapp.model.User;
import com.example.conferenceapp.util.DBUtil;

import java.sql.*;

/** Аутентификация по idNumber + passwordHash (SHA-256 Base64). */
public class UserDao {

    public User authenticate(String idNumber, String passwordHash) {

        String sql = """
            SELECT u.id,
                   u.id_number,
                   u.full_name,
                   u.photo,          -- путь к аватару
                   r.code            -- participant / moderator / …
            FROM user u
            JOIN role r ON u.role_id = r.id
            WHERE u.id_number    = ?
              AND u.password_hash = SHA2(?,256)
        """;

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, idNumber);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    /* маппируем строковый code → enum Role */
                    String roleCode = rs.getString("code");
                    User.Role role = switch (roleCode) {
                        case "organizer"  -> User.Role.ORGANIZER;
                        case "moderator"  -> User.Role.MODERATOR;
                        case "jury"       -> User.Role.JURY;
                        default           -> User.Role.PARTICIPANT;
                    };

                    return new User(
                            rs.getInt   ("id"),
                            rs.getString("id_number"),
                            rs.getString("full_name"),
                            role,
                            rs.getString("photo")     // может быть null
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();           // TODO: заменить на логгер
        }
        return null;                        // учётные данные не подошли
    }
}
