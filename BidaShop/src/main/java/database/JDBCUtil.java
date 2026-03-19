package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCUtil {
    // For testing: allow setting mock connection
    private static Connection testConnection;
    
    public static void setTestConnection(Connection connection) {
        testConnection = connection;
    }
    
    public static Connection getConnection() {
        // If test connection is set, use it
        if (testConnection != null) {
            return testConnection;
        }
        
        // Otherwise, use real connection
        Connection c = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            String url = "jdbc:sqlserver://localhost:1433;databaseName=BidaShop;encrypt=true;trustServerCertificate=true";
            String user = "sa";
            String pass = "123456";
            c = DriverManager.getConnection(url, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }
    
    // Thực thi SELECT
    public static ResultSet executeQuery(String sql, Object... args) {
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            return stmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi truy vấn dữ liệu", e);
        }
    }

    // Thực thi INSERT, UPDATE, DELETE
    public static int executeUpdate(String sql, Object... args) {
        try (
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            for (int i = 0; i < args.length; i++) {
                stmt.setObject(i + 1, args[i]);
            }
            return stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi cập nhật dữ liệu", e);
        }
    }
    
    public static void closeConnection(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}