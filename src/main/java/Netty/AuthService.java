package Netty;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuthService {

    private static Connection connection;
    private static Statement statement;

    static void connection() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:cloudDB.db");
            statement = connection.createStatement();
            createTable();
            createTableFiles();
            createTableSharingFiles();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void disconnect() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean setNewUser(String login, String password, String email) { //return true if new was set NU
        try {
            if (!isLoginBusy(login)) {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO users (login, password, email) VALUES (?, ?, ?)");
                ps.setString(1, login);
                ps.setString(2, password);
                ps.setString(3, email);
                ps.execute();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public static boolean setNewFile(String filename, int user_id,  String url) {
        try {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO files (filename, user_id, url) VALUES (?, ?, ?)");
            ps.setString(1, filename);
            ps.setInt(2, user_id);
            ps.setString(3, url);
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteFile(String url) {
        try {
            int fileId = AuthService.getFileId(url);
            PreparedStatement ps = connection.prepareStatement("DELETE FROM files WHERE id = ?");
            ps.setInt(1, fileId);
            ps.execute();

            PreparedStatement nps = connection.prepareStatement("DELETE FROM sharing_files WHERE file_id = ?");
            nps.setInt(1, fileId);
            nps.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean isLoginBusy(String login) {   //return true if login busy
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    static boolean getAccess(String login, String password) {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement("SELECT id FROM users WHERE login = ? AND password = ?");
            ps.setString(1, login);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public static int findUserId(String login){
        try {
            PreparedStatement ps = null;
            ps = connection.prepareStatement("SELECT id FROM users WHERE login = ?");
            ps.setString(1, login);
            ResultSet rs = ps.executeQuery();
            return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean shareFile(int user_id, String url) {
        try {
            int fileId = AuthService.getFileId(url);
            PreparedStatement ps = connection.prepareStatement("INSERT INTO sharing_files (user_id, file_id) VALUES (?, ?)");
            ps.setInt(1, user_id);
            ps.setInt(2, fileId);
            ps.execute();
            return true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }

    public static int getFileId(String url) {
        try {
            PreparedStatement ps = null;
            ps = connection.prepareStatement("SELECT id FROM files WHERE url = ?");
            ps.setString(1, url);
            ResultSet rs = ps.executeQuery();
            return rs.getInt("id");
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static List<String> getSharingFiles(int user_id) {
        List<String> urls = new ArrayList<String>();
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(
                    "SELECT url FROM files " +
                            "INNER JOIN sharing_files " +
                            "ON sharing_files.file_id = files.id " +
                            "WHERE sharing_files.user_id = ?"
            );
            ps.setInt(1, user_id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                urls.add(rs.getString(1));
            }
            rs.close();
            return urls;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return urls;
        }
    }

    private static void createTable() throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS users(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "login VARCHAR(50) UNIQUE NOT NULL," +
                        "password VARCHAR(50) NOT NULL," +
                        "email VARCHAR(100)" +
                        ")"
        );
    }

    private static void createTableFiles() throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS files(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "user_id INTEGER NOT NULL," +
                        "filename VARCHAR(100)," +
                        "url VARCHAR(250) NOT NULL," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)" +
                        ")"
        );
    }

    private static void createTableSharingFiles() throws SQLException {
        statement.executeUpdate(
                "CREATE TABLE IF NOT EXISTS sharing_files(" +
                        "user_id INTEGER NOT NULL," +
                        "file_id VARCHAR(100)," +
                        "FOREIGN KEY (user_id) REFERENCES users(id)," +
                        "FOREIGN KEY (file_id) REFERENCES files(id)" +
                        ")"
        );
    }

}
