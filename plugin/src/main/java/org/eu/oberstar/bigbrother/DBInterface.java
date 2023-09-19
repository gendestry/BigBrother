package org.eu.oberstar.bigbrother;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

class User {
    int id;
    String username, name, surname, email;
    boolean confirmed;

    @Override
    public String toString() {
        return username + "[" + id + ", " + (confirmed ? 't' : 'f') + "]: " + name + " " + surname + ", " + email;
    }
}

public class DBInterface {
    private static final String url = "jdbc:postgresql://localhost:5432/minecraft";
    private static final String user = "postgres";
    private static final String password = "example";

    private static Connection conn = null;

    public static Connection get() {
        if(conn == null) {
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(url, user, password);
                System.out.println("Connected to the PostgreSQL server successfully.");
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found: " + e.getMessage());
            }
        }

        return conn;
    }

    public static void close() {
        if(conn == null)
            return;
        
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Helper functions
    public static int getUserId(String username) {
        int userId = -1;

        try{
            PreparedStatement ps = get().prepareStatement("SELECT id FROM users WHERE username = ?");
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                userId = rs.getInt("id");
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e ){
            System.out.println(e.getMessage());
        }

        return userId;
    }

    public static User getUser(String username) {
        int id = getUserId(username);
        User user = null;

        // cant find the user
        if(id == -1)
            return user;

        try {
            PreparedStatement ps = get().prepareStatement("SELECT * FROM users WHERE id = ?");
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                user = new User();
                user.id = id;
                user.username = rs.getString("username");
                user.name = rs.getString("name");
                user.surname = rs.getString("surname");
                user.email = rs.getString("email");
                user.confirmed = rs.getBoolean("confirmed");
            }

            rs.close();
            ps.close();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return user;
    }

    public static void createSession(int userId) {
        try {
            String closeSQL = "UPDATE log_session SET leave_time = NOW() WHERE user_id = ? AND leave_time IS NULL";
            PreparedStatement closePs = get().prepareStatement(closeSQL);
            closePs.setInt(1, userId);
            closePs.executeUpdate();

            String insertSQL = "INSERT INTO log_session (user_id) VALUES (?)";
            PreparedStatement insertPs = get().prepareStatement(insertSQL);
            insertPs.setInt(1, userId);
            insertPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void concludeSession(int userId, String reason) {
        try {
            String closeSQL = "UPDATE log_session SET leave_time = NOW(), leave_reason = ? WHERE user_id = ? AND leave_time IS NULL";
            PreparedStatement closePs = get().prepareStatement(closeSQL);
            closePs.setString(1, reason);
            closePs.setInt(2, userId);
            closePs.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}
