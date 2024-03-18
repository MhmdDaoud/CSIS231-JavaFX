package com.app.project1;

import com.app.project1.database.DBHandler;
import com.app.project1.services.UserServices;
import com.app.project1.services.AccountServices;
import com.app.project1.session.Account;
import com.app.project1.session.SessionManager;
import com.app.project1.utils.DataQueryingUtils;
import com.mysql.cj.jdbc.result.ResultSetImpl;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class HomepageController implements Initializable {

    @FXML
    Button transBtn, expensesBtn, budgetsBtn, settingsBtn, addAccountBtn;

    @FXML
    Label welcomeLabel, totExpensesLabel;

    @FXML
    PieChart expensesPie;

    @FXML
    ComboBox<String> accountComboBox;
    private MainApplication mainApplication;

    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    private void setWelcomeLabel() {
        try {
            welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getUsername() + "!");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    private void setPieChart() {
        try {
            expensesPie.setTitle("Expenses per Category");
            expensesPie.setData(DataQueryingUtils.expensesPerCat(SessionManager.getCurrentUser().getId(),
                    SessionManager.getCurrentAccount().getAccountID()));
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    private void setComboBox() {
        try {
            ArrayList<Account> accounts = UserServices.getUserAccounts(SessionManager.getCurrentUser().getId());
            for (Account account : accounts) {
                accountComboBox.getItems().add(account.getAccountName());
            }
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    private void setInfoLabels() {
        YearMonth currentYearMonth = YearMonth.now();
        Month currentMonth = currentYearMonth.getMonth();
        totExpensesLabel.setText(currentMonth + " expenses: " +
                DataQueryingUtils.getTotalAccountExpenses(SessionManager.getCurrentAccount().getAccountID()));
    }

    public void addPressed() {
        Stage tempStage = new Stage();
        tempStage.setTitle("Account Creation");
        HBox hbox = new HBox();

        TextField nameField = new TextField();
        nameField.setPromptText("Enter account name");
        TextField balanceField = new TextField();
        balanceField.setPromptText("Enter account balance");
        Button addBtn = new Button("ADD");

        hbox.getChildren().addAll(nameField, balanceField, addBtn);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);

        tempStage.setScene(new Scene(hbox, 500, 60));
        tempStage.show();

        addBtn.setOnAction(e -> {
            String acctName = nameField.getText();
            double acctBalance = Double.parseDouble(balanceField.getText());
            if (AccountServices.processAddAccount(acctName, acctBalance)) {
                tempStage.close();
                setComboBox();
                setPieChart();
            }
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWelcomeLabel();
        setComboBox();

        accountComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SessionManager.setCurrentAccount(AccountServices.getAccountByName(newValue));
                setPieChart();;
                setInfoLabels();
            } else {
                expensesPie.getData().clear();
            }
        });
    }
}
