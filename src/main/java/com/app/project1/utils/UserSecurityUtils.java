package com.app.project1.utils;

import com.app.project1.database.DBHandler;
import com.app.project1.session.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;

public class UserSecurityUtils {

    public static boolean validate(String email, String password) {
        return isEmail(email) && isStrongPassword(password);
    }

    public static boolean isEmail(String email) {
        try {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static boolean isStrongPassword(String password) {
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }

        return password.length() >= 8 && hasLowerCase && hasUpperCase && hasNumber;
    }

    public static boolean usernameExists(String username) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM users WHERE LOWER(username) = LOWER('" + username + "');";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                resultSet.close();
                statement.close();
                return true;
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }

    public static boolean authenticate(String email, String password) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM users WHERE user_email = '" + email + "' AND user_password = '" + password + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.absolute(1)) {
                resultSet.close();
                statement.close();
                return true;
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
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

    public static User getUserByName(String username) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM users WHERE username = '" + username + "';";
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.first()) {
                String fetchedEmail = resultSet.getString("user_email");
                String fetchedPW = resultSet.getString("user_password");
                Date fetchedDate = resultSet.getDate("creation_date");

                resultSet.close();
                statement.close();

                return new User(username, fetchedEmail, fetchedPW, fetchedDate);
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
                String fetchedUsername = resultSet.getString("username");
                String fetchedPW = resultSet.getString("user_password");
                Date fetchedDate = resultSet.getDate("creation_date");

                resultSet.close();
                statement.close();

                return new User(fetchedUsername, email, fetchedPW, fetchedDate);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }
}
