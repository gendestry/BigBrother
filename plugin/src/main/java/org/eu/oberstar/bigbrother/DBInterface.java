package org.eu.oberstar.bigbrother;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

class User {
    int id;
    String username, name, surname, email;
    boolean confirmed;

    @Override
    public String toString() {
        return username + "[" + id + ", " + (confirmed ? 't' : 'f') + "]: " + name + " " + surname + ", " + email;
    }
}

class Pos {
    boolean valid = true;
    float x, y, z;
    String username;
}

class HomeLocations {
    LinkedList<Pos> homes = new LinkedList<>();
}

public class DBInterface {
    // private static final String url = "jdbc:postgresql://server:port/postgres";
    // private static final String user = "postgres.userhash";
    // private static final String password = "password";
    private static final String url = "";
    private static Connection conn = null;

    public static Connection get() {
        if(conn == null) {
            try {
                Class.forName("org.postgresql.Driver");
                conn = DriverManager.getConnection(url);
                // conn = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                System.out.println("get(): " + e.getMessage());
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
            String closeSQL = "UPDATE log_session SET leave_time = NOW(), leave_reason = ? WHERE leave_time IS NULL;";
            PreparedStatement closePs = get().prepareStatement(closeSQL);
            closePs.setString(1, "Server shutdown.");
            closePs.executeUpdate();
            closePs.close();
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

    public static void createSession(int userId, String ip) {
        try {
            String closeSQL = "UPDATE log_session SET leave_time = NOW() WHERE user_id = ? AND leave_time IS NULL";
            PreparedStatement closePs = get().prepareStatement(closeSQL);
            closePs.setInt(1, userId);
            closePs.executeUpdate();
            closePs.close();

            String insertSQL = "INSERT INTO log_session (user_id, ip) VALUES (?, ?)";
            PreparedStatement insertPs = get().prepareStatement(insertSQL);
            insertPs.setInt(1, userId);
            insertPs.setString(2, ip);
            insertPs.executeUpdate();
            insertPs.close();
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
            closePs.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void logCommand(int userId, String command) {
        try {
            String insertSQL = "INSERT INTO log_command (session_id, command) VALUES ((SELECT id FROM log_session WHERE user_id = ? AND leave_time IS NULL ORDER BY id DESC LIMIT 1), ?)";
            PreparedStatement insertPs = get().prepareStatement(insertSQL);
            insertPs.setInt(1, userId);
            insertPs.setString(2, command);
            insertPs.executeUpdate();
            insertPs.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void setHome(String username, float x, float y, float z) {
        int userId = getUserId(username);

        try {
            String insertSQL = "INSERT INTO home_locations (user_id, x, y, z) VALUES (?, ?, ?, ?) ON CONFLICT (user_id) DO UPDATE SET x = ?, y = ?, z = ?";
            PreparedStatement ps = get().prepareStatement(insertSQL);
            ps.setInt(1, userId);
            ps.setFloat(2, x);
            ps.setFloat(3, y);
            ps.setFloat(4, z);
            ps.setFloat(5, x);
            ps.setFloat(6, y);
            ps.setFloat(7, z);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Pos getHome(String username) {
        int userId = getUserId(username);

        try {
            String insertSQL = "SELECT x, y, z FROM home_locations WHERE user_id = ?";
            PreparedStatement ps = get().prepareStatement(insertSQL);
            ps.setInt(1, userId);
            
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Pos pos = new Pos();
                pos.x = rs.getFloat("x");
                pos.y = rs.getFloat("y");
                pos.z = rs.getFloat("z");

                ps.close();
                return pos;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static Pos listHome(String username) {
        int userId = getUserId(username);

        try {
            String homeSql = "SELECT x, y, z FROM home_locations WHERE user_id = ?";
            PreparedStatement ps = get().prepareStatement(homeSql);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                Pos pos = new Pos();
                pos.x = rs.getFloat("x");
                pos.y = rs.getFloat("y");
                pos.z = rs.getFloat("z");
                pos.username = username;

                ps.close();
                return pos;
            }
            else {
                Pos p = new Pos();
                p.valid = false;
                p.username = "User does not have a home set!";
                return p;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        Pos p = new Pos();
        p.valid = false;
        p.username = "User does not exist";
        return p;
    }

    public static HomeLocations listHomes() {
        String homeSql = "SELECT username, x, y, z FROM users LEFT JOIN home_locations ON users.id = home_locations.user_id";
        try {
            PreparedStatement ps = get().prepareStatement(homeSql);
            HomeLocations homes = new HomeLocations();
            ResultSet rs = ps.executeQuery();

            while(rs.next()) {
                Pos pos = new Pos();
                pos.x = rs.getFloat("x");
                pos.y = rs.getFloat("y");
                pos.z = rs.getFloat("z");
                pos.username = rs.getString("username");
                homes.homes.add(pos);
            }

            ps.close();
            return homes;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

}
