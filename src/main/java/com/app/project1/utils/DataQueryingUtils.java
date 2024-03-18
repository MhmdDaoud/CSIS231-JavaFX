package com.app.project1.utils;

import com.app.project1.database.DBHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    public static String getTotalAccountExpenses(int account_id) {
        DBHandler dbHandler = new DBHandler();
        double totalExpenses = 0.0;
        try {
            Statement statement = dbHandler.getConnection().createStatement();
            String sql = "SELECT SUM(transaction_amount) AS total_expenses " +
                    "FROM transactions " +
                    "WHERE account_id = " + account_id +
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
}
