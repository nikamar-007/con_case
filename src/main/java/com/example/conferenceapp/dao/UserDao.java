package com.example.conferenceapp.dao;

import com.example.conferenceapp.model.User;
import com.example.conferenceapp.util.DBUtil;

import java.sql.*;

/** Аутентификация по idNumber + passwordHash (SHA-256 Base64). */
public class UserDao {

    private static final String BASE_SELECT = """
            SELECT u.id,
                   u.id_number,
                   u.full_name,
                   COALESCE(u.full_name, u.id_number) AS display_name,
                   u.photo,
                   u.email,
                   u.phone,
                   u.birth_date,
                   u.gender,
                   r.code,
                   u.direction_id,
                   d.name AS direction_name,
                   c.name AS country_name
            FROM user u
            JOIN role r      ON u.role_id = r.id
            LEFT JOIN direction d ON u.direction_id = d.id
            LEFT JOIN country c   ON u.country_id = c.id
        """;

    public User authenticate(String idNumber, String passwordHash) {

        String sql = BASE_SELECT + "\n            WHERE u.id_number = ?\n              AND u.password_hash = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, idNumber);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();           // TODO: заменить на логгер
        }
        return null;                        // учётные данные не подошли
    }

    public User findRemembered() {
        String sql = BASE_SELECT + "\n            WHERE u.remembered = 1\n            LIMIT 1";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public void updateRemembered(int userId, boolean remember) {
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement clear = c.prepareStatement("UPDATE user SET remembered = 0");
                 PreparedStatement mark  = remember
                        ? c.prepareStatement("UPDATE user SET remembered = 1 WHERE id = ?")
                        : null) {

                clear.executeUpdate();
                if (remember && mark != null) {
                    mark.setInt(1, userId);
                    mark.executeUpdate();
                }
                c.commit();
            } catch (SQLException ex) {
                c.rollback();
                throw ex;
            } finally {
                c.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private User mapUser(ResultSet rs) throws SQLException {
        String roleCode = rs.getString("code");
        User.Role role = switch (roleCode) {
            case "organizer"  -> User.Role.ORGANIZER;
            case "moderator"  -> User.Role.MODERATOR;
            case "jury"       -> User.Role.JURY;
            default           -> User.Role.PARTICIPANT;
        };

        Integer directionId = rs.getObject("direction_id") != null
                ? rs.getInt("direction_id")
                : null;

        return new User(
                rs.getInt("id"),
                rs.getString("id_number"),
                rs.getString("full_name"),
                rs.getString("display_name"),
                role,
                rs.getString("photo"),
                directionId,
                rs.getString("direction_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null,
                rs.getString("gender"),
                rs.getString("country_name")
        );
    }
}
