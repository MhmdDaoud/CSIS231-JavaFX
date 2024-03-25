package com.app.project1.database;

import java.sql.*;

/**
 * Handles database operations such as connecting to the database, executing queries, and closing connections.
 */
public class DBHandler {

    /**
     * Initializing environment variables
     */
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_PATH = "jdbc:mysql://localhost:3306/financedb";
    private static final String DB_USER = "root";
    private static final String DB_PW = "root";

    private Connection connection;
    private Statement statement;

    /**
     * Initializes a DBHandler object and establishes a connection to the database.
     */
    public DBHandler() {
        connect();
    }

    /**
     * Establishes a connection to the database.
     */
    private void connect() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_PATH, DB_USER, DB_PW);
            statement = connection.createStatement();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Executes a query on the database.
     *
     * @param sql The SQL query to be executed.
     * @return The result set of the query, or null if an error occurs.
     */
    public ResultSet executeQuery(String sql) {
        try {
            ResultSet resultSet = statement.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * Retrieves the connection to the database.
     *
     * @return The database connection.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Sets the connection to the database.
     *
     * @param connection The database connection to be set.
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Retrieves the statement used for database operations.
     *
     * @return The database statement.
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * Sets the statement used for database operations.
     *
     * @param statement The database statement to be set.
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException exe) {
            System.out.println("Error closing database connection: " + exe.getMessage());
        }
    }
}
