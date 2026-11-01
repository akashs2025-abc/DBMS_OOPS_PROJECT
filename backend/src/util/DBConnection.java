package util;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {

    private static String getRequiredEnvironment(String key) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Set the " + key + " environment variable before starting CareerTrack.");
        }
        return value.trim();
    }

    private static final String URL = getOptionalEnvironment(
            "DB_URL",
            "jdbc:mysql://localhost:3306/job_tracker?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
    );
    private static final String USER = getRequiredEnvironment("DB_USER");
    private static final String PASSWORD = getRequiredEnvironment("DB_PASSWORD");

    private static String getOptionalEnvironment(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.trim().isEmpty()) ? defaultValue : value.trim();
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "MySQL JDBC Driver is missing from the runtime classpath. Add mysql-connector-j to the classpath.");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    
    public static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
