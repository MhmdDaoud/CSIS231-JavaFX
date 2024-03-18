package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Account;
import com.app.project1.session.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class UserServices {

    public static User getUserById(int user_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM users WHERE user_id = " + user_id + ";";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return new User(resultSet.getInt("user_id"),
                        resultSet.getString("username"),
                        resultSet.getString("user_password"),
                        resultSet.getString("user_email"),
                        resultSet.getDate("creation_date")
                );
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    public static User getUserByEmail(String email) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM users WHERE user_email = '" + email + "';";
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.first()) {
                int fetchedID = resultSet.getInt("user_id");
                String fetchedUsername = resultSet.getString("username");
                String fetchedPW = resultSet.getString("user_password");
                Date fetchedDate = resultSet.getDate("creation_date");

                resultSet.close();
                statement.close();

                return new User(fetchedID, fetchedUsername, email, fetchedPW, fetchedDate);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    public static User getUserByName(String username) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM users WHERE username = '" + username + "';";
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.first()) {
                int fetchedID = resultSet.getInt("user_id");
                String fetchedEmail = resultSet.getString("user_email");
                String fetchedPW = resultSet.getString("user_password");
                Date fetchedDate = resultSet.getDate("creation_date");

                resultSet.close();
                statement.close();

                return new User(fetchedID, username, fetchedEmail, fetchedPW, fetchedDate);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    public static ArrayList<Account> getUserAccounts(int user_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT * FROM accounts WHERE user_id = " + user_id + ";";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public static void insertUser(String username, String email, String password) {
        DBHandler dbHandler = new DBHandler();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "INSERT INTO users (user_email, user_password, username, creation_date) " +
                    "VALUES ('" + email + "', '" + password + "', '" + username + "', '" + dateFormat.format(new Date()) + "');";
            statement.executeUpdate(sql);
            statement.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
    }
}
