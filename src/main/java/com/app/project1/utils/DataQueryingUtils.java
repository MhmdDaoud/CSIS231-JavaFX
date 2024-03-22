package com.app.project1.utils;

import com.app.project1.database.DBHandler;
import com.app.project1.session.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Date;

public class DataQueryingUtils {

    public static ObservableList<PieChart.Data> expensesPerCat(int userID, int accountID) {
        DBHandler dbHandler = new DBHandler();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        try {
            Statement statement = dbHandler.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "SELECT categories.category_name, SUM(transactions.transaction_amount) AS total_expenses " +
                    "FROM transactions " +
                    "JOIN categories ON transactions.transaction_category = categories.category_id " +
                    "WHERE transactions.user_id = " + userID +
                    " AND transactions.account_id = " + accountID +
                    " AND transactions.transaction_amount > 0 " +
                    " AND transactions.transaction_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH) " +
                    "GROUP BY categories.category_name;";
            ResultSet resultSet = statement.executeQuery(sql);

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

    public static String getMonthlyAccountExpenses(int account_id) {
        DBHandler dbHandler = new DBHandler();
        double totalExpenses = 0.0;
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT SUM(transaction_amount) AS total_expenses " +
                    "FROM transactions " +
                    "WHERE account_id = " + account_id +
                    " AND transaction_amount > 0" +
                    " AND transaction_date >= DATE_SUB(CURRENT_DATE(), INTERVAL 1 MONTH);";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public static double getTotalAccountExpenses(int account_id, int year) {
        DBHandler dbHandler = new DBHandler();
        double totalExpenses = 0.0;
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT SUM(transaction_amount) AS total_expenses " +
                    "FROM transactions " +
                    "WHERE account_id = " + account_id +
                    " AND YEAR(transaction_date) = " + year +
                    " AND transaction_amount < 0;";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public static double getTotalAccountIncome(int account_id, int year) {
        DBHandler dbHandler = new DBHandler();
        double totalIncome = 0.0;
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT SUM(transaction_amount) AS total_income " +
                    "FROM transactions " +
                    "WHERE account_id = " + account_id +
                    " AND YEAR(transaction_date) = " + year +
                    " AND transaction_amount > 0;";
            ResultSet resultSet = statement.executeQuery(sql);
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

    public static double getNetBalance(int account_id, int year) {
        DBHandler dbHandler = new DBHandler();
        double totalIncome = 0.0;
        double totalExpenses = 0.0;
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String incomeQuery = "SELECT SUM(transaction_amount) AS total_income " +
                    "FROM transactions " +
                    "WHERE account_id = " + account_id +
                    " AND transaction_amount > 0" + // Filter positive transactions
                    " AND YEAR(transaction_date) = " + year + ";";
            ResultSet incomeResultSet = statement.executeQuery(incomeQuery);
            if (incomeResultSet.next()) {
                totalIncome = incomeResultSet.getDouble("total_income");
            }

            String expensesQuery = "SELECT SUM(transaction_amount) AS total_expenses " +
                    "FROM transactions " +
                    "WHERE account_id = " + account_id +
                    " AND transaction_amount < 0" + // Filter negative transactions
                    " AND YEAR(transaction_date) = " + year + ";";
            ResultSet expensesResultSet = statement.executeQuery(expensesQuery);
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

    public static XYChart.Series<String, Number> expensesDistribution(int account_id) {
        DBHandler dbHandler = new DBHandler();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT YEAR(transaction_date) AS transaction_year, " +
                    "MONTHNAME(transaction_date) AS transaction_month, " +
                    "SUM(CASE WHEN transaction_amount < 0 THEN ABS(transaction_amount) ELSE 0 END) AS total_expenses " +
                    "FROM transactions WHERE account_id = " + account_id + " " +
                    "AND YEAR(transaction_date) = YEAR(CURDATE()) " +
                    "GROUP BY YEAR(transaction_date), MONTHNAME(transaction_date), MONTH(transaction_date) " +
                    "ORDER BY MONTH(transaction_date);";

            ResultSet resultSet = statement.executeQuery(sql);
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

    public static XYChart.Series<String, Number> incomeDistribution(int account_id) {
        DBHandler dbHandler = new DBHandler();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT YEAR(transaction_date) AS transaction_year, " +
                    "MONTHNAME(transaction_date) AS transaction_month, " +
                    "SUM(CASE WHEN transaction_amount > 0 THEN transaction_amount ELSE 0 END) AS total_income " +
                    "FROM transactions WHERE account_id = " + account_id + " " +
                    "AND YEAR(transaction_date) = YEAR(CURDATE()) " +
                    "GROUP BY YEAR(transaction_date), MONTHNAME(transaction_date), MONTH(transaction_date) " +
                    "ORDER BY MONTH(transaction_date);";

            ResultSet resultSet = statement.executeQuery(sql);
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

    public static ObservableList<Transaction> getTableViewData(int account_id) {
        ObservableList<Transaction> transactionList = FXCollections.observableArrayList();
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                    "c.category_name AS transaction_category, t.transaction_amount" +
                    " FROM transactions t " +
                    "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                    "WHERE t.account_id = " + account_id;

            ResultSet resultSet = statement.executeQuery(sql);
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

    public static ObservableList<Transaction> searchForKeyword(int account_id, String keyword) {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        DBHandler dbHandler = new DBHandler();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                    "c.category_name AS transaction_category, t.transaction_amount" +
                    " FROM transactions t " +
                    "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                    "WHERE t.account_id = " + account_id +
                    " AND (t.transaction_description LIKE '%" + keyword + "%'" +
                    " OR c.category_name LIKE '%" + keyword + "%');";

            ResultSet resultSet = statement.executeQuery(sql);
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

    public static ObservableList<Transaction> filterByDate(LocalDate startDate, LocalDate endDate, int account_id) {
        DBHandler dbHandler = new DBHandler();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT t.transaction_id, t.transaction_description, t.transaction_date, " +
                    "c.category_name AS transaction_category, t.transaction_amount" +
                    " FROM transactions t " +
                    "INNER JOIN categories c ON t.transaction_category = c.category_id " +
                    "WHERE t.account_id = " + account_id +
                    " AND t.transaction_date " +
                    "BETWEEN '" + java.sql.Date.valueOf(startDate) + "' AND '" + java.sql.Date.valueOf(endDate) + "';";
            ResultSet resultSet = statement.executeQuery(sql);

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
}
