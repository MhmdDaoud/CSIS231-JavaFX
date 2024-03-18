package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Account;
import com.app.project1.session.SessionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountServices {
    public static boolean processAddAccount(String name, double balance) {
        DBHandler dbHandler = new DBHandler();
        try {
            if (checkAccountExistance(name)) {
                return false;
            }
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "INSERT INTO accounts (user_id, account_name, account_balance) " +
                    "VALUES (" + SessionManager.getCurrentUser().getId() + ", '" + name + "', " + balance + ");";
            int rowsAffected = statement.executeUpdate(sql);
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

    private static boolean checkAccountExistance(String name) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT * FROM accounts WHERE account_name = '" + name
                    + "' AND user_id = " + SessionManager.getCurrentUser().getId() + ";";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public static Account getAccountByID(int account_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM accounts WHERE account_id = " + account_id + ";";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public static Account getAccountByName(String name) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT * FROM accounts WHERE account_name = '" + name + "';";
            ResultSet resultSet = statement.executeQuery(sql);
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
