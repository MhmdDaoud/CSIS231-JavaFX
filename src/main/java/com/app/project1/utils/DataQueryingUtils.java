package com.app.project1.utils;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility class for querying and processing data.
 */
public class DataQueryingUtils {

    /**
     * Retrieves expenses per category for a specific user and account within the last month.
     *
     * @param userID    The ID of the user.
     * @param accountID The ID of the account.
     * @return An ObservableList of PieChart.Data objects representing expenses per category,
     * or null if an error occurs or no data is found.
     */
    public static ObservableList<PieChart.Data> expensesPerCat(int userID, int accountID) {
        DBHandler dbHandler = new DBHandler();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT categories.category_name, SUM(ABS(transactions.transaction_amount)) AS total_expenses " +
                            "FROM transactions JOIN categories ON transactions.transaction_category = categories.category_id " +
                            "WHERE transactions.user_id = ? AND transactions.account_id = ? AND transactions.transaction_amount < 0 " +
                            "AND transactions.transaction_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH) " +
                            "GROUP BY categories.category_name;"
            );
            statement.setInt(1, userID);
            statement.setInt(2, accountID);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String categoryName = resultSet.getString("category_name");
                double totalExpenses = resultSet.getDouble("total_expenses");
                pieChartData.add(new PieChart.Data(categoryName, totalExpenses));
            }

            return pieChartData;
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return null;
    }

    /**
     * Retrieves the total monthly expenses for a specific account.
     *
     * @param account_id The ID of the account for which expenses are calculated.
     * @return A string representation of the total monthly expenses for the account.
     * Returns "0.0" if no expenses are found or an error occurs during the database operation.
     */
    public static String getMonthlyAccountExpenses(int account_id) {
        DBHandler dbHandler = new DBHandler();
        double totalExpenses = 0.0;
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(ABS(transaction_amount)) AS total_expenses " +
                            "FROM transactions " +
                            "WHERE account_id = ? " +
                            "AND transaction_amount < 0 " +
                            "AND transaction_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH);"
            );
            statement.setInt(1, account_id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalExpenses = resultSet.getDouble("total_expenses");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return Double.toString(totalExpenses);
    }

    /**
     * Retrieves the total expenses for a specific account and year from the transactions table.
     *
     * @param account_id The ID of the account for which expenses are to be retrieved.
     * @param year       The year for which expenses are to be calculated.
     * @return The total expenses for the specified account and year.
     */
    public static double getTotalAccountExpenses(int account_id, int year) {
        DBHandler dbHandler = new DBHandler();
        double totalExpenses = 0.0;
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(transaction_amount) AS total_expenses " +
                            "FROM transactions " +
                            "WHERE account_id = ? " +
                            "AND YEAR(transaction_date) = ? " +
                            "AND transaction_amount < 0;"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, year);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalExpenses = resultSet.getDouble("total_expenses");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return totalExpenses;
    }

    /**
     * Retrieves the total income for a specific account and year from the transactions table.
     *
     * @param account_id The ID of the account for which income is to be retrieved.
     * @param year       The year for which income is to be calculated.
     * @return The total income for the specified account and year.
     */
    public static double getTotalAccountIncome(int account_id, int year) {
        DBHandler dbHandler = new DBHandler();
        double totalIncome = 0.0;
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(transaction_amount) AS total_income " +
                            "FROM transactions " +
                            "WHERE account_id = ? AND YEAR(transaction_date) = ? AND transaction_amount > 0;"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, year);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                totalIncome = resultSet.getDouble("total_income");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return totalIncome;
    }

    /**
     * Calculates the net balance (income minus expenses) for a specific account and year.
     *
     * @param account_id The ID of the account for which the net balance is to be calculated.
     * @param year       The year for which the net balance is to be calculated.
     * @return           The net balance for the specified account and year.
     */
    public static double getNetBalance(int account_id, int year) {
        DBHandler dbHandler = new DBHandler();
        double totalIncome = 0.0;
        double totalExpenses = 0.0;
        try {
            PreparedStatement incomeStatement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(transaction_amount) AS total_income " +
                            "FROM transactions " +
                            "WHERE account_id = ? AND transaction_amount > 0 AND YEAR(transaction_date) = ?;"
            );
            incomeStatement.setInt(1, account_id);
            incomeStatement.setInt(2, year);
            ResultSet incomeResultSet = incomeStatement.executeQuery();
            if (incomeResultSet.next()) {
                totalIncome = incomeResultSet.getDouble("total_income");
            }

            PreparedStatement expensesStatement = dbHandler.getConnection().prepareStatement(
                    "SELECT SUM(transaction_amount) AS total_expenses " +
                            "FROM transactions " +
                            "WHERE account_id = ? AND transaction_amount < 0 AND YEAR(transaction_date) = ?;"
            );
            expensesStatement.setInt(1, account_id);
            expensesStatement.setInt(2, year);
            ResultSet expensesResultSet = expensesStatement.executeQuery();
            if (expensesResultSet.next()) {
                totalExpenses = expensesResultSet.getDouble("total_expenses");
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return totalIncome - Math.abs(totalExpenses);
    }

    /**
     * Retrieves the monthly expenses distribution for a specified account.
     *
     * @param account_id The ID of the account for which expenses distribution is to be retrieved.
     * @return An XYChart.Series object representing the monthly expenses distribution.
     */
    public static XYChart.Series<String, Number> expensesDistribution(int account_id) {
        DBHandler dbHandler = new DBHandler();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT YEAR(transaction_date) AS transaction_year, " +
                            "MONTHNAME(transaction_date) AS transaction_month, " +
                            "SUM(CASE WHEN transaction_amount < 0 THEN ABS(transaction_amount) ELSE 0 END) AS total_expenses " +
                            "FROM transactions WHERE account_id = ? " +
                            "AND YEAR(transaction_date) = YEAR(CURDATE()) " +
                            "GROUP BY YEAR(transaction_date), MONTHNAME(transaction_date), MONTH(transaction_date) " +
                            "ORDER BY MONTH(transaction_date);"
            );
            statement.setInt(1, account_id);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String month = resultSet.getString("transaction_month");
                double expenses = resultSet.getDouble("total_expenses");
                series.getData().add(new XYChart.Data<>(month, expenses));
            }
            resultSet.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return series;
    }


    /**
     * Retrieves the monthly income distribution for a specified account.
     *
     * @param account_id The ID of the account for which income distribution is to be retrieved.
     * @return An XYChart.Series object representing the monthly income distribution.
     */
    public static XYChart.Series<String, Number> incomeDistribution(int account_id) {
        DBHandler dbHandler = new DBHandler();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT YEAR(transaction_date) AS transaction_year, " +
                            "MONTHNAME(transaction_date) AS transaction_month, " +
                            "SUM(CASE WHEN transaction_amount > 0 THEN transaction_amount ELSE 0 END) AS total_income " +
                            "FROM transactions WHERE account_id = ? " +
                            "AND YEAR(transaction_date) = YEAR(CURDATE()) " +
                            "GROUP BY YEAR(transaction_date), MONTHNAME(transaction_date), MONTH(transaction_date) " +
                            "ORDER BY MONTH(transaction_date);"
            );
            statement.setInt(1, account_id);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String month = resultSet.getString("transaction_month");
                double income = resultSet.getDouble("total_income");
                series.getData().add(new XYChart.Data<>(month, income));
            }
            resultSet.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return series;
    }


    /**
     * Retrieves transaction data for a specified account to populate a TableView.
     *
     * @param account_id The ID of the account for which transaction data is to be retrieved.
     * @return An ObservableList<Transaction> containing the transaction data.
     */
    public static ObservableList<Transaction> getTableViewData(int account_id) {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                            "c.category_name AS transaction_category, t.transaction_amount" +
                            " FROM transactions t " +
                            "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                            "WHERE t.account_id = ?"
            );
            statement.setInt(1, account_id);

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                String description = resultSet.getString("transaction_description");
                Date date = resultSet.getDate("transaction_date");
                String category = resultSet.getString("transaction_category");
                double amount = resultSet.getDouble("transaction_amount");

                transactionList.add(new Transaction(transactionId, description, date, category, amount));
            }
            resultSet.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return transactionList;
    }

    /**
     * Searches for transactions based on a keyword in the transaction description or category name.
     *
     * @param account_id The ID of the account for which transactions are to be searched.
     * @param keyword    The keyword to search for in transaction descriptions or category names.
     * @return An ObservableList<Transaction> containing the transactions matching the keyword.
     */
    public static ObservableList<Transaction> searchForKeyword(int account_id, String keyword) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                            "c.category_name AS transaction_category, t.transaction_amount" +
                            " FROM transactions t " +
                            "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                            "WHERE t.account_id = ? AND (t.transaction_description LIKE ? OR c.category_name LIKE ?)"
            );
            statement.setInt(1, account_id);
            statement.setString(2, "%" + keyword + "%");
            statement.setString(3, "%" + keyword + "%");

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                String description = resultSet.getString("transaction_description");
                Date date = resultSet.getDate("transaction_date");
                String category = resultSet.getString("transaction_category");
                double amount = resultSet.getDouble("transaction_amount");

                transactions.add(new Transaction(transactionId, description, date, category, amount));
            }
            resultSet.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return transactions;
    }

    /**
     * Filters transactions based on a date range and account ID.
     *
     * @param startDate  The start date of the date range.
     * @param endDate    The end date of the date range.
     * @param account_id The ID of the account for which transactions are to be filtered.
     * @return An ObservableList<Transaction> containing the transactions within the specified date range.
     */
    public static ObservableList<Transaction> filterByDate(LocalDate startDate, LocalDate endDate, int account_id) {
        DBHandler dbHandler = new DBHandler();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                            "c.category_name AS transaction_category, t.transaction_amount" +
                            " FROM transactions t " +
                            "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                            "WHERE t.account_id = ? AND t.transaction_date BETWEEN ? AND ?"
            );
            statement.setInt(1, account_id);
            statement.setDate(2, java.sql.Date.valueOf(startDate));
            statement.setDate(3, java.sql.Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                String description = resultSet.getString("transaction_description");
                Date date = resultSet.getDate("transaction_date");
                String category = resultSet.getString("transaction_category");
                double amount = resultSet.getDouble("transaction_amount");

                transactions.add(new Transaction(transactionId, description, date, category, amount));
            }
            resultSet.close();
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return transactions;
    }

    /**
     * Retrieves a list of transactions for a specific account and current year.
     * Transactions are fetched from the database and organized into Transaction objects.
     *
     * @param account_id The ID of the account for which transactions are retrieved.
     * @return An ObservableList containing Transaction objects representing the transactions for the specified account in the current year.
     */
    public static ObservableList<Transaction> getTransactionsForTable(int account_id) {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
        DBHandler dbHandler = new DBHandler();

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                            "c.category_name, t.transaction_amount " +
                            "FROM transactions t " +
                            "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                            "WHERE t.account_id = ? AND YEAR(t.transaction_date) = ?;"
            );
            statement.setInt(1, account_id);
            statement.setInt(2, currentYear);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int transactionId = resultSet.getInt("transaction_id");
                String description = resultSet.getString("transaction_description");
                Date date = resultSet.getDate("transaction_date");
                String categoryName = resultSet.getString("category_name");
                double amount = resultSet.getDouble("transaction_amount");

                Transaction transaction = new Transaction(transactionId, description, date, categoryName, amount);
                transactionList.add(transaction);
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return transactionList;
    }

    /**
     * Converts a month name to its corresponding numerical representation.
     *
     * @param monthName The name of the month to convert.
     * @return The numerical representation of the month (1 for January, 2 for February, ..., 12 for December), or -1 if the month name is invalid.
     */
    public static int monthNameToNumber(String monthName) {
        String[] monthNames = {
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].equalsIgnoreCase(monthName)) {
                return i + 1;
            }
        }
        return -1;
    }
}
