package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/employee_management?serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";

    // Private constructor: nobody should create objects of this class
    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        String url = getEnv("DB_URL", DEFAULT_URL);
        String user = getEnv("DB_USER", DEFAULT_USER);
        String password = System.getenv("DB_PASSWORD");

        if (password == null) {
            throw new SQLException(
                    "DB_PASSWORD environment variable is not set. "
                    + "Set it before running the application.");
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Reads an environment variable; uses a default if it is missing or empty
    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        return value;
    }
}
