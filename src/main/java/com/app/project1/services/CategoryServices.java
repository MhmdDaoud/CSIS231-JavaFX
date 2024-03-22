package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CategoryServices {
    public static int getCategoryID(String category_name) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT category_id WHERE category_name = '" + category_name + "';";
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.getInt("category_id");
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return -1;
    }

    public static String getCategoryName(int category_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT category_name FROM categories WHERE category_id = " + category_id;
            ResultSet resultSet = statement.executeQuery(sql);
            return resultSet.getString("category_name");
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }


    public static ObservableList<String> getAllCategories() {
        DBHandler dbHandler = new DBHandler();
        ObservableList<String> categories = FXCollections.observableArrayList();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT category_name FROM categories;";

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next() ) {
                categories.add(resultSet.getString("category_name"));
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return categories;
    }
}
