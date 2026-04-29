package evolution;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:evolution.db";

    public DatabaseManager() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create table if it doesn't exist
            String createTableSQL = "CREATE TABLE IF NOT EXISTS evolution_stats ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "generation INT NOT NULL,"
                    + "population INT NOT NULL,"
                    + "best_fitness INT NOT NULL,"
                    + "avg_fitness DOUBLE NOT NULL,"
                    + "total_loc INT NOT NULL"
                    + ");";
            
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public void insertGenerationStats(int generation, int population, int bestFitness, double avgFitness, int totalLoc) {
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
