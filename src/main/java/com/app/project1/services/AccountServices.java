package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Account;
import com.app.project1.session.SessionManager;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides services related to account management, including adding new accounts, checking account existence,
 * and retrieving accounts by ID or name.
 */
public class AccountServices {

    /**
     * Attempts to add a new account with the specified name and balance for the current user.
     *
     * @param name    The name of the account to add.
     * @param balance The initial balance of the account.
     * @return true if the account was successfully added, false otherwise.
     */
    public static boolean processAddAccount(String name, double balance) {
        DBHandler dbHandler = new DBHandler();
        try {
            if (checkAccountExistence(name)) {
                return false;
            }
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "INSERT INTO accounts (user_id, account_name, account_balance) VALUES (?, ?, ?);"
            );
            statement.setInt(1, SessionManager.getCurrentUser().getId());
            statement.setString(2, name);
            statement.setDouble(3, balance);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
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
     * Checks if an account with the specified name already exists for the current user.
     *
     * @param name The name of the account to check.
     * @return true if an account with the name already exists, false otherwise.
     */
    private static boolean checkAccountExistence(String name) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM accounts WHERE LOWER(account_name) = LOWER(?) AND user_id = ?;"
            );
            statement.setString(1, name.toLowerCase());
            statement.setInt(2, SessionManager.getCurrentUser().getId());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
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
     * Retrieves an account with the specified ID.
     *
     * @param account_id The ID of the account to retrieve.
     * @return The account with the specified ID, or null if not found.
     */
    public static Account getAccountByID(int account_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM accounts WHERE account_id = ?;"
            );
            statement.setInt(1, account_id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Account(resultSet.getInt("account_id"),
                        resultSet.getString("account_name"),
                        resultSet.getDouble("account_balance"));
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    /**
     * Retrieves an account with the specified name for the given user ID.
     *
     * @param user_id The ID of the user owning the account.
     * @param name    The name of the account to retrieve.
     * @return The account with the specified name and user ID, or null if not found.
     */
    public static Account getAccountByName(int user_id, String name) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM accounts WHERE user_id = ? AND account_name = ?;"
            );
            statement.setInt(1, user_id);
            statement.setString(2, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Account(resultSet.getInt("account_id"),
                        resultSet.getString("account_name"),
                        resultSet.getDouble("account_balance"));
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }
}
