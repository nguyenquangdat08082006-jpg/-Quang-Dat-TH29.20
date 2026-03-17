package da;

import java.sql.*;

public class MessageService {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=trungkien;encrypt=true;trustServerCertificate=true;";
    private static final String USER = "sa";
    private static final String PASS = "29082006";

    public static Connection getConnection() throws Exception {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public static void saveMsg(String sender, String content) {
        String sql = "INSERT INTO Messages (Sender, Content) VALUES (?,?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, sender);
            ps.setString(2, content);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}