package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.utils.TransactionData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class TransactionServices {

    public static boolean insertTransaction(String transaction_description, int category_id,
                                            Date transaction_date, double transaction_amount) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "INSERT INTO transactions (transaction_description, category_id, transaction_date, transaction_amount)" +
                    "VALUES (" + transaction_description + ", " + category_id + ", " + transaction_date + ", " + transaction_amount + ");";
            statement.executeUpdate(sql);
            statement.close();
            return true;
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }

    public static boolean deleteTransaction(int transaction_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "DELETE FROM transactions WHERE transaction_id = " + transaction_id + ";";
            statement.executeUpdate(sql);
            statement.close();
            return true;
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }

    public static TransactionData getTransactionFields(int transaction_id, int account_id) throws SQLException {
        DBHandler dbHandler = new DBHandler();
        try (Statement statement = dbHandler.getConnection().createStatement()) {
            String sql = "SELECT t.transaction_description, t.transaction_date, " +
                    "c.category_name AS transaction_category, t.transaction_amount" +
                    " FROM transactions t " +
                    "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                    "WHERE t.account_id = " + account_id +
                    " AND t.transaction_id = " + transaction_id;

            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                String transactionDesc = resultSet.getString("transaction_description");
                java.sql.Date transactionDate = resultSet.getDate("transaction_date");
                String transactionCat = resultSet.getString("transaction_category");
                double transactionAmount = resultSet.getDouble("transaction_amount");

                return new TransactionData(transactionDesc, transactionDate.toLocalDate(), transactionCat, transactionAmount);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    public static boolean updateTransaction(int transactionId, String transactionDesc, java.sql.Date transactionDate,
                                            int transactionCat, double transactionAmount) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement preparedStatement = dbHandler.getConnection().prepareStatement(
                    "UPDATE transactions SET transaction_description = ?, transaction_date = ?, " +
                            "transaction_category = ?, transaction_amount = ? WHERE transaction_id = ?;"
            );
            preparedStatement.setString(1, transactionDesc);
            preparedStatement.setDate(2, transactionDate);
            preparedStatement.setInt(3, transactionCat);
            preparedStatement.setDouble(4, transactionAmount);
            preparedStatement.setInt(5, transactionId);

            int rowsChanged = preparedStatement.executeUpdate();
            if (rowsChanged > 0) {
                return true;
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }
}