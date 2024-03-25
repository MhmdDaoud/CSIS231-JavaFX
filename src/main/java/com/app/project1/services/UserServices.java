package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Account;
import com.app.project1.session.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Provides various services related to user operations in the application.
 */
public class UserServices {

    /**
     * Retrieves a user by their ID from the database.
     *
     * @param user_id The ID of the user to retrieve.
     * @return The User object if found, or null if not found or an error occurs.
     */
    public static User getUserById(int user_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE user_id = ?;"
            );
            statement.setInt(1, user_id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("user_password"),
                        resultSet.getString("user_email"),
                        resultSet.getDate("creation_date"),
                        resultSet.getString("encryption_key")
                );
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    /**
     * Retrieves a user by their email address from the database.
     *
     * @param email The email address of the user to retrieve.
     * @return The User object if found, or null if not found or an error occurs.
     */
    public static User getUserByEmail(String email) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE user_email = ?;"
            );
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int fetchedID = resultSet.getInt("user_id");
                String fetchedUsername = resultSet.getString("username");
                String fetchedPW = resultSet.getString("user_password");
                Date fetchedDate = resultSet.getDate("creation_date");
                String fetchedKey = resultSet.getString("encryption_key");

                resultSet.close();
                statement.close();

                return new User(fetchedID, fetchedUsername, email, fetchedPW, fetchedDate, fetchedKey);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }


    /**
     * Retrieves a user by their username from the database.
     *
     * @param username The username of the user to retrieve.
     * @return The User object if found, or null if not found or an error occurs.
     */
    public static User getUserByName(String username) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE LOWER(username) = LOWER(?);"
            );
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.first()) {
                int fetchedID = resultSet.getInt("user_id");
                String fetchedEmail = resultSet.getString("user_email");
                String fetchedPW = resultSet.getString("user_password");
                Date fetchedDate = resultSet.getDate("creation_date");
                String fetchedKey = resultSet.getString("encryption_key");

                resultSet.close();
                statement.close();

                return new User(fetchedID, username, fetchedEmail, fetchedPW, fetchedDate, fetchedKey);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    /**
     * Retrieves the accounts associated with a user from the database.
     *
     * @param user_id The ID of the user whose accounts to retrieve.
     * @return An ArrayList of Account objects belonging to the user, or null if an error occurs.
     */
    public static ArrayList<Account> getUserAccounts(int user_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM accounts WHERE user_id = ?;"
            );
            statement.setInt(1, user_id);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<Account> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(new Account(resultSet.getInt("account_id"),
                        resultSet.getString("account_name"),
                        resultSet.getDouble("account_balance")));
            }

            resultSet.close();
            statement.close();

            return result;
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    /**
     * Inserts a new user into the database.
     *
     * @param username  The username of the new user.
     * @param email     The email address of the new user.
     * @param password  The password of the new user.
     * @param secretKey The encryption key for the new user.
     * @return true if the user is successfully inserted, false otherwise.
     */
    public static boolean insertUser(String username, String email, String password, String secretKey) {
        DBHandler dbHandler = new DBHandler();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "INSERT INTO users (user_email, user_password, username, creation_date, encryption_key) " +
                            "VALUES (?, ?, ?, ?, ?);"
            );

            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, username);
            statement.setString(4, dateFormat.format(new Date()));
            statement.setString(5, secretKey);

            int rowsChanged = statement.executeUpdate();
            if (rowsChanged > 0) {
                return true;
            }
            statement.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }

    /**
     * Retrieves the total income of a user for a specific account from the database.
     *
     * @param user_id   The ID of the user.
     * @param account_id The ID of the account.
     * @return The total income of the user for the specified account, or 0 if an error occurs.
     */
    public static double getUserTotalIncome(int user_id, int account_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(transaction_amount) AS total_income FROM transactions " +
                            "WHERE user_id = ? AND account_id = ? AND transaction_category = 6;"
            );
            statement.setInt(1, user_id);
            statement.setInt(2, account_id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("total_income");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return 0;
    }
}
