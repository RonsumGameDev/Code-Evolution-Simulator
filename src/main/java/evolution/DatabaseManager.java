package evolution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/evolution_simulator?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "yourpassword"; // Updated per user request

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Failed to load MySQL JDBC driver");
        }
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {

            // Create table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS evolution_stats ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "generation INT NOT NULL,"
                    + "population INT NOT NULL,"
                    + "best_fitness INT NOT NULL,"
                    + "avg_fitness DOUBLE NOT NULL,"
                    + "total_loc INT NOT NULL DEFAULT 0"
                    + ");";

            stmt.execute(createTableSQL);

            // Attempt to add total_loc in case the table already existed from an older
            // version
            try {
                stmt.execute("ALTER TABLE evolution_stats ADD COLUMN total_loc INT NOT NULL DEFAULT 0");
            } catch (SQLException ignored) {
                // Ignore: column already exists
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    public void insertGenerationStats(int generation, int population, int bestFitness, double avgFitness,
            int totalLoc) {
        String insertSQL = "INSERT INTO evolution_stats (generation, population, best_fitness, avg_fitness, total_loc) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setInt(1, generation);
            pstmt.setInt(2, population);
            pstmt.setInt(3, bestFitness);
            pstmt.setDouble(4, avgFitness);
            pstmt.setInt(5, totalLoc);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error inserting stats into database: " + e.getMessage());
            javax.swing.JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "SQL Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void clearStats() {
        String deleteSQL = "DELETE FROM evolution_stats";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement()) {
            stmt.execute(deleteSQL);
        } catch (SQLException e) {
            System.err.println("Error clearing stats: " + e.getMessage());
        }
    }

    public int getSavedGenerationsCount() {
        String query = "SELECT COUNT(*) FROM evolution_stats";
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching count: " + e.getMessage());
        }
        return 0;
    }
}
