package com.app.project1.services;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Budget;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Provides services related to budget management.
 */
public class BudgetServices {
    /**
     * Retrieves the total budget for the specified account between two dates.
     *
     * @param account_id The ID of the account.
     * @param start_date The start date of the calculation.
     * @param end_date   The end date of the calculation.
     * @return The total budget for the account, or null if an error occurs.
     */
    public static double getTotalBudget(int account_id, Date start_date, Date end_date) {
        DBHandler dbHandler = new DBHandler();
        double totalBudget = 0.0;
        try {
            PreparedStatement preparedStatement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(budget_amount) AS total_budget FROM budgets WHERE account_id = ? " +
                            "AND (start_date BETWEEN ? AND ?) AND (end_date BETWEEN ? AND ?);"
            );
            preparedStatement.setInt(1, account_id);
            preparedStatement.setDate(2, start_date);
            preparedStatement.setDate(3, end_date);
            preparedStatement.setDate(4, start_date);
            preparedStatement.setDate(5, end_date);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                totalBudget = resultSet.getDouble("total_budget");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return totalBudget;
    }

    /**
     * Retrieves a list of budgets for the specified account.
     *
     * @param account_id The ID of the account.
     * @return An ObservableList of Budget objects for the account, or an empty list if no budgets are found.
     */
    public static ObservableList<Budget> getBudgetData(int account_id) {
        DBHandler dbHandler = new DBHandler();
        ObservableList<Budget> budgetList = FXCollections.observableArrayList();

        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT b.budget_id, b.account_id, c.category_name, b.category_id, " +
                            "b.budget_amount, b.start_date, b.end_date " +
                            "FROM budgets b " +
                            "INNER JOIN categories c ON b.category_id = c.category_id " +
                            "WHERE b.account_id = ?;"
            );
            statement.setInt(1, account_id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int budgetId = resultSet.getInt("budget_id");
                int accountId = resultSet.getInt("account_id");
                String categoryName = resultSet.getString("category_name");
                int categoryId = resultSet.getInt("category_id");
                double budgetAmount = resultSet.getDouble("budget_amount");
                Date budgetStart = resultSet.getDate("start_date");
                Date budgetEnd = resultSet.getDate("end_date");

                Budget budget = new Budget(budgetId, accountId, categoryName, budgetAmount, budgetStart, budgetEnd);
                double actualExpenses = BudgetServices.getExpensesPerCategory(account_id, categoryId, budgetStart, budgetEnd);
                double remainingAmount = budgetAmount - Math.abs(actualExpenses);

                budget.setActualExpenses(actualExpenses);
                budget.setRemainingAmount(remainingAmount);
                budget.setBudgetMonth();

                budgetList.add(budget);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return budgetList;
    }

    /**
     * Retrieves the total expenses for a specific category within a budget period.
     *
     * @param account_id  The ID of the account.
     * @param category_id The ID of the category.
     * @param start_date  The start date of the budget period.
     * @param end_date    The end date of the budget period.
     * @return The total expenses for the category within the budget period, or 0.0 if an error occurs.
     */
    public static double getExpensesPerCategory(int account_id, int category_id, Date start_date, Date end_date) {
        DBHandler dbHandler = new DBHandler();
        double actualExpenses = 0.0;

        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(ABS(transaction_amount)) AS total_expenses " +
                            "FROM transactions WHERE account_id = ? AND transaction_amount < 0 AND " +
                            "transaction_category = ? AND " +
                            "transaction_date BETWEEN ? AND ?;"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, category_id);
            statement.setDate(3, start_date);
            statement.setDate(4, end_date);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                actualExpenses = resultSet.getDouble("total_expenses");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return actualExpenses;
    }

    /**
     * Calculates the remaining budget amount for the specified account within a budget period.
     *
     * @param account_id The ID of the account.
     * @param start_date The start date of the budget period.
     * @param end_date   The end date of the budget period.
     * @return The remaining budget amount for the account within the budget period, or 0.0 if an error occurs.
     */
    public static double getRemainingBudgetAmount(int account_id, Date start_date, Date end_date) {
        DBHandler dbHandler = new DBHandler();
        double totalBudget = getTotalBudget(account_id, start_date, end_date);
        double totalExpenses = 0.0;

        try {
            totalExpenses = getTotalExpenses(account_id, start_date, end_date);
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }

        double remainingBudget = totalBudget - totalExpenses;
        return remainingBudget;
    }

    /**
     * Calculates the total expenses for the specified account within a given period.
     *
     * @param account_id The ID of the account.
     * @param start_date The start date of the period.
     * @param end_date   The end date of the period.
     * @return The total expenses for the account within the specified period, or 0.0 if an error occurs.
     * @throws SQLException If an SQL exception occurs during database operations.
     */
    private static double getTotalExpenses(int account_id, Date start_date, Date end_date) throws SQLException {
        DBHandler dbHandler = new DBHandler();
        double totalExpenses = 0.0;

        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(ABS(transaction_amount)) AS total_expenses " +
                            "FROM transactions WHERE account_id = ? AND transaction_amount < 0 AND " +
                            "transaction_date BETWEEN ? AND ?;"
            );
            statement.setInt(1, account_id);
            statement.setDate(2, start_date);
            statement.setDate(3, end_date);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalExpenses = resultSet.getDouble("total_expenses");
            }
        } finally {
            dbHandler.closeConnection();
        }
        return totalExpenses;
    }

    /**
     * Processes the addition of a new budget entry.
     *
     * @param account_id    The ID of the account.
     * @param category_id   The ID of the category.
     * @param budget_amount The budgeted amount.
     * @param start_date    The start date of the budget period.
     * @param end_date      The end date of the budget period.
     * @return True if the budget addition is successful, false otherwise.
     */
    public static boolean processAddBudget(int account_id, int category_id, double budget_amount,
                                           Date start_date, Date end_date) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "INSERT INTO budgets (account_id, category_id, budget_amount, start_date, end_date) " +
                            "VALUES (?, ?, ?, ?, ?);"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, category_id);
            statement.setDouble(3, budget_amount);
            statement.setDate(4, start_date);
            statement.setDate(5, end_date);

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
     * Processes the deletion of an existing budget entry.
     *
     * @param account_id  The ID of the account.
     * @param category_id The ID of the category.
     * @param start_date  The start date of the budget period.
     * @param end_date    The end date of the budget period.
     * @return True if the budget deletion is successful, false otherwise.
     */
    public static boolean processDeleteBudget(int account_id, int category_id, Date start_date, Date end_date) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "DELETE FROM budgets WHERE account_id = ? AND category_id = ? " +
                            "AND (start_date BETWEEN ? AND ?) AND (end_date BETWEEN ? AND ?);"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, category_id);
            statement.setDate(3, start_date);
            statement.setDate(4, end_date);
            statement.setDate(5, start_date);
            statement.setDate(6, end_date);

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
}
