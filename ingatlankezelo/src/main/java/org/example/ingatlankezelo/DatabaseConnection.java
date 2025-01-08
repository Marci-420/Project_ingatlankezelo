package org.example.ingatlankezelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {

    // Path to the MySQL database
    private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/ingatlanproject";
    private static final String username = "asd";
    private static final String password = "asd";

    // Create a database connection
    private Connection createDatabaseConnection() {
        try {
            Connection conn = DriverManager.getConnection(DATABASE_URL, username, password);
            if (conn != null) {
                System.out.println("Database connection established.");
            } else {
                System.err.println("Failed to establish database connection.");
            }
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Failed to establish database connection: " + e.getMessage());
            return null;
        }
    }

    // Close database connection and statement
    private void closeDatabaseConnection(Connection c, Statement s) throws SQLException {
        if (s != null) s.close();
        if (c != null) c.close();
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT * FROM properties";

        try (Connection connection = DriverManager.getConnection(DATABASE_URL, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String address = resultSet.getString("address");
                String type = resultSet.getString("type");
                double rentPrice = resultSet.getDouble("rent_price");
                String status = resultSet.getString("status");

                Property property = new Property(address, type, rentPrice, status);
                properties.add(property);
            }
        } catch (SQLException e) {
            System.err.println("Hiba történt az ingatlanok lekérdezésekor: " + e.getMessage());
        }

        return properties;
    }


    // Save property to the database
    public boolean saveToDatabase(Property p) {
        String query = "INSERT INTO properties (address, type, rent_price, status) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null; // PreparedStatement used

        try {
            conn = createDatabaseConnection(); // Get the connection
            if (conn == null) {
                System.err.println("No connection available.");
                return false;
            }

            // Check if property object is not null and contains valid data
            System.out.println("Address: " + p.getAddress());
            System.out.println("Type: " + p.getType());
            System.out.println("Rent price: " + p.getRentPrice());
            System.out.println("Status: " + p.getStatus());

            // Prepare statement
            stmt = conn.prepareStatement(query);

            // Set the parameters for the query
            stmt.setString(1, p.getAddress());
            stmt.setString(2, p.getType());
            stmt.setDouble(3, p.getRentPrice());
            stmt.setString(4, p.getStatus());

            // Enable transaction handling
            conn.setAutoCommit(false);  // Disable auto-commit
            int rowsInserted = stmt.executeUpdate(); // Execute the query
            if (rowsInserted > 0) {
                System.out.println("Successfully inserted the record.");
                conn.commit();  // Commit the transaction
                return true;
            } else {
                System.err.println("No rows inserted.");
                conn.rollback();  // Rollback if no rows were inserted
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error occurred while saving the property: " + e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();  // Rollback the transaction in case of an error
                }
            } catch (SQLException rollbackException) {
                System.err.println("Error occurred while rolling back the transaction: " + rollbackException.getMessage());
            }
            return false;
        } finally {
            try {
                closeDatabaseConnection(conn, stmt); // Always close resources
            } catch (SQLException e) {
                System.err.println("Error occurred while closing the connection/statement: " + e.getMessage());
            }
        }
    }
}
