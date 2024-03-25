package com.app.project1.services;

import com.app.project1.database.DBHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides services related to category management in the application.
 */
public class CategoryServices {

    /**
     * Retrieves the ID of a category based on its name.
     *
     * @param category_name The name of the category.
     * @return The ID of the category, or -1 if the category is not found.
     */
    public static int getCategoryID(String category_name) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT category_id FROM categories WHERE category_name = ?;"
            );
            statement.setString(1, category_name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("category_id");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return -1;
    }

    /**
     * Retrieves the name of a category based on its ID.
     *
     * @param category_id The ID of the category.
     * @return The name of the category, or null if the category is not found.
     */
    public static String getCategoryName(int category_id) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT category_name FROM categories WHERE category_id = ?;"
            );
            statement.setInt(1, category_id);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.getString("category_name");
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    /**
     * Retrieves a list of all categories.
     *
     * @return An ObservableList containing all category names, or an empty list if no categories are found.
     */
    public static ObservableList<String> getAllCategories() {
        DBHandler dbHandler = new DBHandler();
        ObservableList<String> categories = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT category_name FROM categories;"
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                categories.add(resultSet.getString("category_name"));
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return categories;
    }

    /**
     * Retrieves a list of categories suitable for budget allocation (excluding "Income").
     *
     * @return An ObservableList containing budget categories, or an empty list if no categories are found.
     */
    public static ObservableList<String> getBudgetCategories() {
        DBHandler dbHandler = new DBHandler();
        ObservableList<String> categories = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT category_name FROM categories WHERE LOWER(category_name) != 'income';"
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
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
