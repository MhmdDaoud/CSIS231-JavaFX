package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.utils.TransactionData;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Provides services related to transaction management in the application.
 */
public class TransactionServices {

    /**
     * Inserts a new transaction into the database.
     *
     * @param user_id                The ID of the user associated with the transaction.
     * @param account_id             The ID of the account associated with the transaction.
     * @param transaction_description The description of the transaction.
     * @param category_id            The ID of the category associated with the transaction.
     * @param transaction_date       The date of the transaction.
     * @param transaction_amount     The amount of the transaction.
     * @return True if the transaction was successfully inserted, false otherwise.
     */
    public static boolean insertTransaction(int user_id, int account_id, String transaction_description, int category_id,
                                            Date transaction_date, double transaction_amount) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "INSERT INTO transactions (user_id, account_id, transaction_amount, " +
                            "transaction_category, transaction_description, transaction_date) " +
                            "VALUES (?, ?, ?, ?, ?, ?);"
            );
            statement.setInt(1, user_id);
            statement.setInt(2, account_id);
            statement.setDouble(3, transaction_amount);
            statement.setInt(4, category_id);
            statement.setString(5, transaction_description);
            statement.setDate(6, java.sql.Date.valueOf(String.valueOf(transaction_date)));
            int rowsChanged = statement.executeUpdate();
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

    /**
     * Deletes a transaction from the database.
     *
     * @param transaction_id The ID of the transaction to delete.
     * @return True if the transaction was successfully deleted, false otherwise.
     */
    public static boolean deleteTransaction(int transaction_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "DELETE FROM transactions WHERE transaction_id = ?;"
            );
            statement.setInt(1, transaction_id);
            int rowsChanged = statement.executeUpdate();
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

    /**
     * Retrieves transaction fields from the database.
     *
     * @param transaction_id The ID of the transaction to retrieve.
     * @param account_id     The ID of the account associated with the transaction.
     * @return A TransactionData object containing transaction information, or null if no transaction is found.
     */
    public static TransactionData getTransactionFields(int transaction_id, int account_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT t.transaction_description, t.transaction_date, " +
                            "c.category_name AS transaction_category, t.transaction_amount" +
                            " FROM transactions t " +
                            "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                            "WHERE t.account_id = ? AND t.transaction_id = ?;"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, transaction_id);

            ResultSet resultSet = statement.executeQuery();
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

    /**
     * Updates a transaction in the database.
     *
     * @param transactionId     The ID of the transaction to update.
     * @param transactionDesc   The new description of the transaction.
     * @param transactionDate   The new date of the transaction.
     * @param transactionAmount The new amount of the transaction.
     * @return True if the transaction was successfully updated, false otherwise.
     */
    public static boolean updateTransaction(int transactionId, String transactionDesc,
                                            java.sql.Date transactionDate, double transactionAmount) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement preparedStatement = dbHandler.getConnection().prepareStatement(
                    "UPDATE transactions SET transaction_description = ?, transaction_date = ?, " +
                            "transaction_amount = ? WHERE transaction_id = ?;"
            );
            preparedStatement.setString(1, transactionDesc);
            preparedStatement.setDate(2, transactionDate);
            preparedStatement.setDouble(3, transactionAmount);
            preparedStatement.setInt(4, transactionId);

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
