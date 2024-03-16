package com.app.project1.database;

import java.sql.*;

public class DBHandler {

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_PATH = "jdbc:mysql://localhost:3306/financedb";
    private static final String DB_USER = "root";
    private static final String DB_PW = "root";

    private Connection connection;
    private Statement statement;

    public DBHandler() {
        connect();
    }

    private void connect() {
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_PATH, DB_USER, DB_PW);
            statement = connection.createStatement();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public ResultSet executeQuery(String sql) {
        try {
            ResultSet resultSet = statement.executeQuery(sql);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Statement getStatement() {
        return statement;
    }

    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException exe) {
            System.out.println("Error closing database connection: " + exe.getMessage());
        }
    }
}
