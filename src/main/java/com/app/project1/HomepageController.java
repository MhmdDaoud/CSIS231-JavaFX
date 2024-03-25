/**
 * Controller class for the homepage view of the finance manager application.
 */
package com.app.project1;

import com.app.project1.services.AccountServices;
import com.app.project1.services.UserServices;
import com.app.project1.session.Account;
import com.app.project1.session.SessionManager;
import com.app.project1.utils.DataQueryingUtils;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controller class for the user homepage view.
 */
public class HomepageController implements Initializable {

    @FXML
    Button transBtn, budgetsBtn, addAccountBtn, logoutBtn;
    @FXML
    Label welcomeLabel, totExpensesLabel, acctBalanceLabel;
    @FXML
    PieChart expensesPie;
    @FXML
    ComboBox<String> accountComboBox;
    private MainApplication mainApplication;

    /**
     * Sets the reference to the main application.
     *
     * @param mainApplication The main application instance.
     */
    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Sets the welcome label text to display the current user's username.
     */
    private void setWelcomeLabel() {
        try {
            welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getUsername() + "!");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Sets up the pie chart to display expenses per category for the current user and account.
     */
    private void setPieChart() {
        try {
            expensesPie.setTitle("Expenses per Category");
            expensesPie.setData(DataQueryingUtils.expensesPerCat(SessionManager.getCurrentUser().getId(),
                    SessionManager.getCurrentAccount().getAccountID()));
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Populates the accountComboBox with the names of user accounts.
     */
    private void setComboBox() {
        try {
            accountComboBox.getItems().clear();
            ArrayList<Account> accounts = UserServices.getUserAccounts(SessionManager.getCurrentUser().getId());
            for (Account account : accounts) {
                accountComboBox.getItems().add(account.getAccountName());
            }
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Sets the labels displaying information about total expenses and account balance for the current month.
     */
    private void setInfoLabels() {
        YearMonth currentYearMonth = YearMonth.now();
        Month currentMonth = currentYearMonth.getMonth();
        String formattedMonth = currentMonth.getDisplayName(TextStyle.FULL, Locale.getDefault());
        formattedMonth = formattedMonth.substring(0, 1).toUpperCase() + formattedMonth.substring(1).toLowerCase();

        totExpensesLabel.setText(formattedMonth + " Expenses:\n" +
                DataQueryingUtils.getMonthlyAccountExpenses(SessionManager.getCurrentAccount().getAccountID()) + "$");
        acctBalanceLabel.setText("Total Account Income:\n" +
                UserServices.getUserTotalIncome(SessionManager.getCurrentUser().getId(),
                        SessionManager.getCurrentAccount().getAccountID()) + "$");

    }

    /**
     * Handles the "ADD" button press event to create a new account.
     */
    public void addPressed() {
        Stage tempStage = new Stage();
        tempStage.setTitle("Account Creation");
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        HBox hbox = new HBox();

        TextField nameField = new TextField();
        nameField.setPromptText("Enter account name");
        TextField balanceField = new TextField();
        balanceField.setPromptText("Enter account balance");
        Button addBtn = new Button("ADD");
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill: red");

        vbox.getChildren().addAll(hbox, errorLabel);
        hbox.getChildren().addAll(nameField, balanceField, addBtn);
        hbox.setPadding(new Insets(5, 5, 5, 5));
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER);

        tempStage.setScene(new Scene(vbox, 500, 60));
        tempStage.show();

        addBtn.setOnAction(e -> {
            String acctName = nameField.getText();
            double acctBalance = Double.parseDouble(balanceField.getText());
            if (AccountServices.processAddAccount(acctName, acctBalance)) {
                tempStage.close();
                setComboBox();
            } else {
                errorLabel.setText("Account already exists.");
            }
        });
    }

    /**
     * Handles the "Transactions" button press event to navigate to the transactions view.
     */
    public void transBtn() {
        mainApplication.changeFXML("transactions.fxml");
    }

    /**
     * Handles the "Budgets" button press event to navigate to the budgets view.
     */
    public void budgetsBtn() {
        mainApplication.changeFXML("budgets.fxml");
    }

    /**
     * Handles the "Logout" button press event to log out the user and navigate to the login view.
     */
    public void processLogout() {
        SessionManager.logout();
        mainApplication.changeFXML("login.fxml");
    }

    /**
     * Initializes the controller after loading the FXML file.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources specific to this view.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWelcomeLabel();
        setComboBox();

        totExpensesLabel.setText("Total Expenses:");
        acctBalanceLabel.setText("Total Account Income:");

        accountComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SessionManager.setCurrentAccount(
                        AccountServices.getAccountByName(SessionManager.getCurrentUser().getId(), newValue)
                );
                setPieChart();
                setInfoLabels();
            } else {
                expensesPie.getData().clear();
            }
        });
    }
}
