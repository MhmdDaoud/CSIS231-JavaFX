package com.app.project1;

import com.app.project1.services.AccountServices;
import com.app.project1.services.BudgetServices;
import com.app.project1.services.CategoryServices;
import com.app.project1.services.UserServices;
import com.app.project1.session.Account;
import com.app.project1.session.Budget;
import com.app.project1.session.SessionManager;
import com.app.project1.session.Transaction;
import com.app.project1.utils.ConfirmationBox;
import com.app.project1.utils.DataQueryingUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import static com.app.project1.services.BudgetServices.*;

/**
 * Controller class for the budgets view.
 */
public class BudgetsController implements Initializable {

    @FXML
    Label totBudgetAmountLabel, remainingBudgetAmountLabel, budgetCatsLabel;
    @FXML
    DatePicker startDateLabel, endDateLabel;
    @FXML
    ComboBox<String> accountSelector;
    @FXML
    Button backBtn, refreshBtn, addBdgt, deleteBdgt;
    @FXML
    TableView<Budget> budgetTable;
    @FXML
    private TableColumn<Budget, String> catNameCol;
    @FXML
    private TableColumn<Budget, Double> budgetedAmountCol;
    @FXML
    private TableColumn<Budget, Double> budgetExpensesCol;
    @FXML
    private TableColumn<Budget, Double> remainingBudgetCol;
    @FXML
    private TableColumn<Budget, String> monthCol;

    @FXML
    TableView<Transaction> budgetTransactionsTable;
    @FXML
    private TableColumn<Transaction, Integer> transactionIDCol;
    @FXML
    private TableColumn<Transaction, String> transactionDescCol;
    @FXML
    private TableColumn<Transaction, Date> transactionDateCol;
    @FXML
    private TableColumn<Transaction, String> transactionCatCol;
    @FXML
    private TableColumn<Transaction, Double> transactionAmountCol;
    private MainApplication mainApplication;

    /**
     * Sets the main application instance for this controller.
     *
     * @param mainApplication The MainApplication instance to set.
     */
    public void setApplication(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }


    /**
     * Initializes the columns of the budget table with PropertyValueFactory bindings and sets the table data.
     */
    private void initializeBudgetTableColumns() {
        catNameCol.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        budgetedAmountCol.setCellValueFactory(new PropertyValueFactory<>("budgetAmount"));
        budgetExpensesCol.setCellValueFactory(new PropertyValueFactory<>("actualExpenses"));
        remainingBudgetCol.setCellValueFactory(new PropertyValueFactory<>("remainingAmount"));
        monthCol.setCellValueFactory(new PropertyValueFactory<>("budgetMonth"));

        setBudgetTableData();
    }

    /**
     * Sets the data for the budget table by retrieving budget data for the current account and setting it to the table.
     */
    private void setBudgetTableData() {
        ObservableList<Budget> budgetList = getBudgetData(SessionManager.getCurrentAccount().getAccountID());

        budgetTable.setItems(budgetList);
    }

    /**
     * Initializes the columns of the transaction table with PropertyValueFactory bindings and sets the table data.
     */
    private void initializeTransactionTableColumns() {
        transactionIDCol.setCellValueFactory(new PropertyValueFactory<>("transaction_id"));
        transactionDescCol.setCellValueFactory(new PropertyValueFactory<>("transaction_description"));
        transactionDateCol.setCellValueFactory(new PropertyValueFactory<>("transaction_date"));
        transactionCatCol.setCellValueFactory(new PropertyValueFactory<>("transaction_category"));
        transactionAmountCol.setCellValueFactory(new PropertyValueFactory<>("transaction_amount"));

        setTransactionsTableData();
    }

    /**
     * Sets the data for the transaction table by retrieving transaction data for the current account and setting it to the table.
     */
    private void setTransactionsTableData() {
        ObservableList<Transaction> transactions = DataQueryingUtils.getTransactionsForTable(
                SessionManager.getCurrentAccount().getAccountID()
        );

        budgetTransactionsTable.setItems(transactions);
    }

    /**
     * Populates the account selector ComboBox with account names for the current user.
     */
    private void setAccountSelector() {
        try {
            ArrayList<Account> accounts = UserServices.getUserAccounts(SessionManager.getCurrentUser().getId());
            for (Account account : accounts) {
                accountSelector.getItems().add(account.getAccountName());
            }
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Sets the budget-related labels with total budget amount and remaining budget amount for the specified date range.
     */
    private void setBudgetLabels() {
        int accountId = SessionManager.getCurrentAccount().getAccountID();
        java.sql.Date sqlStartDate = java.sql.Date.valueOf(startDateLabel.getValue());
        java.sql.Date sqlEndDate = java.sql.Date.valueOf(endDateLabel.getValue());

        double totalAmount = BudgetServices.getTotalBudget(accountId, sqlStartDate, sqlEndDate);
        totBudgetAmountLabel.setText("Total Budget Amount: $" + totalAmount);

        double remainingAmount = getRemainingBudgetAmount(accountId, sqlStartDate, sqlEndDate);
        remainingBudgetAmountLabel.setText("Remaining Budget: $" + remainingAmount);
    }

    /**
     * Sets the start and end date labels for the budget period to the first and last day of the current month, respectively.
     */
    private void setBudgetPeriodLabels() {
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = currentDate.with(TemporalAdjusters.lastDayOfMonth());

        startDateLabel.setValue(startDate);
        endDateLabel.setValue(endDate);
    }

    /**
     * Handles the action event when the back button is pressed, navigating to the homepage.
     *
     * @param event The ActionEvent triggered by pressing the back button.
     */
    public void backPressed(ActionEvent event) {
        try {
            mainApplication.changeFXML("homepage.fxml");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles the action event when the refresh button is pressed, refreshing the budget and transaction tables if an account is selected.
     *
     * @param event The ActionEvent triggered by pressing the refresh button.
     */
    public void refreshPressed(ActionEvent event) {
        if (accountSelector.getValue() != null) {
            initializeBudgetTableColumns();
            initializeTransactionTableColumns();
            setBudgetLabels();
        }
    }

    /**
     * Handles the action event when the "Add Budget" button is pressed, allowing the user to add a new budget entry for the selected account, month, category, and amount.
     *
     * @param event The ActionEvent triggered by pressing the "Add Budget" button.
     */
    public void addBudgetPressed(ActionEvent event) {
        if (accountSelector.getValue() == null) {
            return;
        }

        Stage primaryStage = new Stage();
        primaryStage.setResizable(false);
        ComboBox<String> monthPicker = new ComboBox<>();
        monthPicker.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        ComboBox<String> categorySelector = new ComboBox<>();
        ObservableList<String> categories = CategoryServices.getBudgetCategories();
        categorySelector.setItems(categories);

        TextField amountField = new TextField();

        Label monthLabel = new Label("Month:");
        Label categoryLabel = new Label("Category:");
        Label amountLabel = new Label("Amount:");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button addBtn = new Button("ADD");
        addBtn.setOnAction(e -> {
            if (accountSelector.getValue().isEmpty()) {
                return;
            }
            double budgetAmount;

            int month = DataQueryingUtils.monthNameToNumber(monthPicker.getValue());
            if (month == -1) {
                errorLabel.setText("Please select a valid month.");
                return;
            }

            String categoryName = categorySelector.getValue();
            if (categoryName == null) {
                errorLabel.setText("Please select a category.");
                return;
            }

            try {
                budgetAmount = Double.parseDouble(amountField.getText());
                String category_name = categorySelector.getValue();
                int category_id = CategoryServices.getCategoryID(category_name);
                int monthNum = DataQueryingUtils.monthNameToNumber(monthPicker.getValue());

                int currentYear = LocalDate.now().getYear();

                LocalDate startDateLocal = LocalDate.of(currentYear, monthNum, 1);
                LocalDate endDateLocal = startDateLocal.plusMonths(1).minusDays(1);
                java.sql.Date startDate = java.sql.Date.valueOf(startDateLocal);
                java.sql.Date endDate = java.sql.Date.valueOf(endDateLocal);

                if (BudgetServices.processAddBudget(
                        SessionManager.getCurrentAccount().getAccountID(), category_id, budgetAmount, startDate, endDate
                )) {
                    primaryStage.close();
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid amount. Please enter a valid number.");
                return;
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(monthLabel, 0, 0);
        gridPane.add(monthPicker, 1, 0);
        gridPane.add(categoryLabel, 0, 1);
        gridPane.add(categorySelector, 1, 1);
        gridPane.add(amountLabel, 0, 2);
        gridPane.add(amountField, 1, 2);
        gridPane.add(errorLabel, 0, 3, 2, 1);
        gridPane.add(addBtn, 0, 4, 2, 1);

        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setTitle("Add Budget");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Handles the action event when the "Delete Budget" button is pressed, allowing the user to delete a budget entry for the selected account, month, and category.
     */
    public void deleteBudgetPressed() {
        if (accountSelector.getValue() == null) {
            return;
        }

        Stage primaryStage = new Stage();
        primaryStage.setResizable(false);
        ComboBox<String> monthPicker = new ComboBox<>();
        monthPicker.getItems().addAll(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        );

        ComboBox<String> categorySelector = new ComboBox<>();
        ObservableList<String> categories = CategoryServices.getBudgetCategories();
        categorySelector.setItems(categories);

        Label monthLabel = new Label("Month:");
        Label categoryLabel = new Label("Category:");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button deleteBtn = new Button("DELETE");
        deleteBtn.setOnAction(e -> {
            try {
                int month = DataQueryingUtils.monthNameToNumber(monthPicker.getValue());
                if (month == -1) {
                    errorLabel.setText("Please select a valid month.");
                    return;
                }

                String categoryName = categorySelector.getValue();
                if (categoryName == null) {
                    errorLabel.setText("Please select a category.");
                    return;
                }
                int category_id = CategoryServices.getCategoryID(categoryName);
                int currentYear = LocalDate.now().getYear();

                LocalDate startDateLocal = LocalDate.of(currentYear, month, 1);
                LocalDate endDateLocal = startDateLocal.plusMonths(1).minusDays(1);
                java.sql.Date startDate = java.sql.Date.valueOf(startDateLocal);
                java.sql.Date endDate = java.sql.Date.valueOf(endDateLocal);

                boolean confirmed = ConfirmationBox.show("Confirmation", "Are you sure you want to delete?");
                if (confirmed) {
                    try {
                        if (processDeleteBudget(
                                SessionManager.getCurrentAccount().getAccountID(), category_id, startDate, endDate)) {
                            primaryStage.close();
                        }
                    } catch (Exception ex) {
                        errorLabel.setText("Invlaid budget information.");
                    }
                }
            } catch (Exception exe) {
                System.out.println(exe.getMessage());
            }
        });

        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gridPane.add(monthLabel, 0, 0);
        gridPane.add(monthPicker, 1, 0);
        gridPane.add(categoryLabel, 0, 1);
        gridPane.add(categorySelector, 1, 1);
        gridPane.add(deleteBtn, 1, 2);

        Scene scene = new Scene(gridPane, 300, 200);
        primaryStage.setTitle("Delete Budget");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Initializes the controller with the account selector and sets listeners for account changes.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setAccountSelector();

        accountSelector.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SessionManager.setCurrentAccount(
                        AccountServices.getAccountByName(SessionManager.getCurrentUser().getId(), newValue)
                );
                setBudgetPeriodLabels();
                setBudgetLabels();
                initializeBudgetTableColumns();
                initializeTransactionTableColumns();
            }
        });
    }
}
