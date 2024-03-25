package com.app.project1;

import com.app.project1.services.AccountServices;
import com.app.project1.services.CategoryServices;
import com.app.project1.services.TransactionServices;
import com.app.project1.services.UserServices;
import com.app.project1.session.Account;
import com.app.project1.session.SessionManager;
import com.app.project1.session.Transaction;
import com.app.project1.utils.ConfirmationBox;
import com.app.project1.utils.DataQueryingUtils;
import com.app.project1.utils.TransactionData;
import javafx.animation.PauseTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import static com.app.project1.services.CategoryServices.getCategoryID;
import static com.app.project1.services.TransactionServices.*;

/**
 * Controller class for the user transactions view.
 */
public class TransactionsController implements Initializable {

    @FXML
    Button backBtn, refreshBtn, searchBtn, filterBtn, addTransBtn, deleteTransBtn, editTransBtn;
    @FXML
    ComboBox<String> selectAccountCombo;
    @FXML
    TextField searchBar;
    @FXML
    DatePicker startDateField, endDateField;
    @FXML
    TableView<Transaction> dataTable;
    @FXML
    private TableColumn<Transaction, Integer> transactionIdColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    private TableColumn<Transaction, Date> dateColumn;
    @FXML
    private TableColumn<Transaction, String> categoryColumn;
    @FXML
    private TableColumn<Transaction, Double> amountColumn;
    @FXML
    Label totIncomeField, totExpensesField, netBalanceField;
    @FXML
    BarChart<String, Number> barChart;
    private MainApplication mainApplication;

    /**
     * Sets the MainApplication instance for this controller.
     *
     * @param mainApplication The MainApplication instance to set.
     */
    public void setApplication(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }

    /**
     * Handles the back button press event by changing the FXML to "homepage.fxml".
     *
     * @param event The ActionEvent triggered by the back button press.
     */
    public void backPressed(ActionEvent event) {
        try {
            mainApplication.changeFXML("homepage.fxml");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Handles the refresh button press event by refreshing the data displayed in the TableView and BarChart.
     *
     * @param event The ActionEvent triggered by the refresh button press.
     */
    public void refreshPressed(ActionEvent event) {
        if (!selectAccountCombo.getSelectionModel().isEmpty()) {
            refreshBtn.setDisable(true);
            try {
                barChart.getData().clear();
                clearLabels();
                setLabels();
                initializeTableView();
                setExpenseGraph();


                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                delay.setOnFinished(e -> {
                    setIncomeGraph();
                    barChart.layout();
                    barChart.setLegendVisible(true);
                    refreshBtn.setDisable(false);
                });
                delay.play();

            } catch (Exception exe) {
                System.out.println(exe.getMessage());
            }
        }
    }

    /**
     * Handles the search button press event by searching for transactions based on the entered keyword.
     *
     * @param event The ActionEvent triggered by the search button press.
     */
    public void searchPressed(ActionEvent event) {
        if (!searchBar.getText().isEmpty()) {
            ObservableList<Transaction> transactions = DataQueryingUtils.searchForKeyword(
                    SessionManager.getCurrentAccount().getAccountID(), searchBar.getText().trim()
            );
            dataTable.setItems(transactions);
        }
    }

    /**
     * Handles the filter button press event by filtering transactions based on the selected date range.
     *
     * @param event The ActionEvent triggered by the filter button press.
     */
    public void filterPressed(ActionEvent event) {
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();

        if (startDate == null || endDate == null) {
            return;
        }

        ObservableList<Transaction> transactions = DataQueryingUtils.filterByDate(
                startDate, endDate, SessionManager.getCurrentAccount().getAccountID()
        );
        dataTable.getItems().clear();
        dataTable.setItems(transactions);
    }

    /**
     * Handles the add transaction button press event by displaying a stage with input fields for adding a new transaction.
     *
     * @param event The ActionEvent triggered by the add transaction button press.
     */
    public void addTransBtnPressed(ActionEvent event) {
        if (selectAccountCombo.getSelectionModel().isEmpty()) {
            return;
        }
        Stage tempStage = new Stage();
        tempStage.setResizable(false);
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label transactionDescLabel = new Label("Transaction Description:");
        TextField transactionDescField = new TextField();
        transactionDescField.setPromptText("Enter transaction description");

        Label transactionDateLabel = new Label("Transaction Date:");
        DatePicker transactionDateField = new DatePicker();
        transactionDateField.setPromptText("Select transaction date");

        Label categoryLabel = new Label("Category:");
        ComboBox<String> categorySelector = new ComboBox<>();
        ObservableList<String> categories = CategoryServices.getAllCategories();
        categorySelector.setItems(categories);
        categorySelector.setMinWidth(150);

        Label transactionAmountLabel = new Label("Transaction Amount:");
        TextField transactionAmountField = new TextField();
        transactionAmountField.setPromptText("Enter transaction amount");

        Button addBtn = new Button("ADD");

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        gridPane.add(transactionDescLabel, 0, 0);
        gridPane.add(transactionDescField, 1, 0);
        gridPane.add(transactionDateLabel, 0, 1);
        gridPane.add(transactionDateField, 1, 1);
        gridPane.add(categoryLabel, 0, 2);
        gridPane.add(categorySelector, 1, 2);
        gridPane.add(transactionAmountLabel, 0, 3);
        gridPane.add(transactionAmountField, 1, 3);
        gridPane.add(errorLabel, 0, 4, 2, 1);
        gridPane.add(addBtn, 0, 5, 2, 1);

        tempStage.setScene(new Scene(gridPane, 350, 250));
        tempStage.setResizable(false);
        tempStage.show();


        addBtn.setOnAction(e -> {
            String transactionDesc = transactionDescField.getText().trim();
            String amountText = transactionAmountField.getText().trim();
            LocalDate transactionDateValue = transactionDateField.getValue();
            String categoryValue = String.valueOf(categorySelector.getSelectionModel().getSelectedItem());
            if (transactionDesc.isEmpty() || amountText.isEmpty() || transactionDateValue == null || categoryValue.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
            }

            try {
                double transactionAmount = Double.parseDouble(amountText);
                java.sql.Date transactionDate = java.sql.Date.valueOf(transactionDateValue);
                int categoryId = getCategoryID(categoryValue);

                if (insertTransaction(SessionManager.getCurrentUser().getId(), SessionManager.getCurrentAccount().getAccountID(),
                        transactionDesc, categoryId, transactionDate, transactionAmount)
                ) {
                    tempStage.close();
                } else {
                    errorLabel.setText("Failed to insert transaction.");
                }
            } catch (NumberFormatException ex) {
                errorLabel.setText("Invalid amount format.");
            } catch (NullPointerException ex) {
                errorLabel.setText("Please fill in all the fields.");
            } catch (DateTimeParseException ex) {
                errorLabel.setText("Invalid date.");
            }
        });
    }

    /**
     * Handles the delete transaction button press event by displaying a stage for entering the transaction ID to delete.
     */
    public void deleteTransPressed() {
        if (selectAccountCombo.getSelectionModel().isEmpty()) {
            return;
        }

        Stage tempStage = new Stage();
        tempStage.setTitle("Transaction Deletion");
        tempStage.setResizable(false);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label instructLabel = new Label("Enter Transaction ID:");
        TextField idField = new TextField();
        idField.setPromptText("Enter transaction ID");

        Button deleteBtn = new Button("DELETE");

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        gridPane.add(instructLabel, 0, 0);
        gridPane.add(idField, 1, 0);
        gridPane.add(errorLabel, 0, 1, 2, 1);
        gridPane.add(deleteBtn, 0, 2, 2, 1);

        deleteBtn.setOnAction(e -> {
            if (idField.getText().isEmpty()) {
                return;
            }
            boolean confirmed = ConfirmationBox.show("Confirmation", "Are you sure you want to delete?");
            if (confirmed) {
                try {
                    int transactionID = Integer.parseInt(idField.getText());
                    if (deleteTransaction(transactionID)) {
                        tempStage.close();
                    } else {
                        errorLabel.setText("Invalid Transaction ID.");
                    }
                } catch (Exception exe) {
                    errorLabel.setText("An error has occurred, please try again later.");
                }
            }
        });

        tempStage.setScene(new Scene(gridPane, 300, 150));
        tempStage.show();
    }

    /**
     * Handles the edit transaction button press event by displaying a stage for editing an existing transaction.
     */
    public void editTransPressed() {
        if (selectAccountCombo.getSelectionModel().isEmpty()) {
            return;
        }

        Stage tempStage = new Stage();
        tempStage.setTitle("Edit Transaction");
        tempStage.setResizable(false);

        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        Label instructionLabel = new Label("Enter Transaction ID to edit:");
        TextField idField = new TextField();
        idField.setPromptText("Enter transaction ID");

        Button retrieveBtn = new Button("RETRIEVE");

        Label descLabel = new Label("Transaction Description:");
        TextField descField = new TextField();
        descField.setDisable(true);
        Label dateLabel = new Label("Transaction Date:");
        DatePicker dateField = new DatePicker();
        dateField.setDisable(true);
        Label amountLabel = new Label("Transaction Amount:");
        TextField amountField = new TextField();
        amountField.setDisable(true);
        Label catLabel = new Label("Transaction Category:");
        ComboBox<String> catSelector = new ComboBox<>();
        catSelector.setDisable(true);
        ObservableList<String> categories = CategoryServices.getAllCategories();
        catSelector.setItems(categories);

        Label errorLabel = new Label("");
        errorLabel.setTextFill(Color.RED);

        Button updateBtn = new Button("UPDATE");
        updateBtn.setDisable(true);

        gridPane.add(instructionLabel, 0, 0);
        gridPane.add(idField, 1, 0);
        gridPane.add(retrieveBtn, 2, 0);

        gridPane.add(descLabel, 0, 1);
        gridPane.add(descField, 1, 1);

        gridPane.add(dateLabel, 0, 2);
        gridPane.add(dateField, 1, 2);

        gridPane.add(amountLabel, 0, 3);
        gridPane.add(amountField, 1, 3);

        gridPane.add(catLabel, 0, 4);
        gridPane.add(catSelector, 1, 4);

        gridPane.add(errorLabel, 0, 5, 3, 1);
        gridPane.add(updateBtn, 0, 6, 3, 1);

        retrieveBtn.setOnAction(e -> {
            try {
                int transaction_id = Integer.parseInt(idField.getText());
                TransactionData transactionData = TransactionServices.getTransactionFields(
                        transaction_id, SessionManager.getCurrentAccount().getAccountID()
                );

                if (transactionData != null) {
                    descField.setDisable(false);
                    dateField.setDisable(false);
                    amountField.setDisable(false);
                    idField.setDisable(true);

                    descField.setText(transactionData.getTransactionDescription());
                    dateField.setValue(transactionData.getTransactionDate());
                    catSelector.setValue(transactionData.getTransactionCategory());
                    amountField.setText(transactionData.getTransactionAmount());

                    updateBtn.setDisable(false);

                    updateBtn.setOnAction(u -> {
                        String transaction_desc = descField.getText();
                        java.sql.Date transaction_date = java.sql.Date.valueOf(dateField.getValue());
                        int category_id = getCategoryID(String.valueOf(catSelector.getSelectionModel()));
                        double transaction_amount = Double.parseDouble(amountField.getText());

                        if (updateTransaction(transaction_id, transaction_desc, transaction_date, transaction_amount)) {
                            tempStage.close();
                        }
                    });
                } else {
                    errorLabel.setText("Transaction not found");
                }

            } catch (Exception exe) {
                errorLabel.setText("Error retrieving transaction");
            }
        });

        tempStage.setScene(new Scene(gridPane, 450, 250));
        tempStage.show();
    }

    /**
     * Sets the items in the selectAccountCombo ComboBox based on the user's accounts retrieved from the database.
     */
    private void setSelectAccountCombo() {
        try {
            ArrayList<Account> accounts = UserServices.getUserAccounts(SessionManager.getCurrentUser().getId());
            for (Account account : accounts) {
                selectAccountCombo.getItems().add(account.getAccountName());
            }
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Sets up the bar chart to display monthly expense distribution for the current account.
     */
    private void setExpenseGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        barChart.setTitle("Monthly Distribution");
        xAxis.setLabel("Month");

        xAxis.setTickLabelGap(25);
        XYChart.Series<String, Number> series = DataQueryingUtils.expensesDistribution(
                SessionManager.getCurrentAccount().getAccountID()
        );
        barChart.getData().add(series);
    }

    /**
     * Sets up the bar chart to display monthly income distribution for the current account.
     */
    private void setIncomeGraph() {
        CategoryAxis xAxis = new CategoryAxis();
        barChart.setTitle("Monthly Distribution");
        xAxis.setLabel("Month");

        xAxis.setTickLabelGap(25);
        XYChart.Series<String, Number> series = DataQueryingUtils.incomeDistribution(
                SessionManager.getCurrentAccount().getAccountID()
        );
        barChart.getData().add(series);
    }

    /**
     * Sets the labels for total income, total expenses, and net balance based on data retrieved from the database.
     */
    private void setLabels() {
        double totalAccountIncome = DataQueryingUtils.getTotalAccountIncome(
                SessionManager.getCurrentAccount().getAccountID(),
                Calendar.getInstance().get(Calendar.YEAR));
        double totalAccountExpenses = DataQueryingUtils.getTotalAccountExpenses(
                SessionManager.getCurrentAccount().getAccountID(),
                Calendar.getInstance().get(Calendar.YEAR));
        double netBalance = DataQueryingUtils.getNetBalance(
                SessionManager.getCurrentAccount().getAccountID(),
                Calendar.getInstance().get(Calendar.YEAR));

        totIncomeField.setText("Total Income: " + totalAccountIncome + "$");
        totExpensesField.setText("Total Expenses: " + Math.abs(totalAccountExpenses) + "$");
        netBalanceField.setText("Net Balance: " + netBalance + "$");
    }

    /**
     * Clears the text and selection in labels, fields, and date pickers related to transactions and search criteria.
     */
    private void clearLabels() {
        totIncomeField.setText("Total Income: ");
        totExpensesField.setText("Total Expenses: ");
        netBalanceField.setText("Net Balance: ");
        searchBar.clear();
        startDateField.setValue(null);
        endDateField.setValue(null);
    }

    /**
     * Initializes the columns of the transactions table view and sets up the data table.
     */
    private void initializeTableView() {
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_amount"));

        setDataTable();
    }

    /**
     * Sets the items in the transactions data table based on the current account's transactions retrieved from the database.
     */
    private void setDataTable() {
        ObservableList<Transaction> transactions = DataQueryingUtils.getTableViewData(
                SessionManager.getCurrentAccount().getAccountID()
        );
        dataTable.setItems(transactions);
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources used to localize the root object.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSelectAccountCombo();

        selectAccountCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SessionManager.setCurrentAccount(
                        AccountServices.getAccountByName(SessionManager.getCurrentUser().getId(), newValue)
                );
                barChart.getData().clear();
                setLabels();
                initializeTableView();
                setExpenseGraph();

                PauseTransition delay = new PauseTransition(Duration.seconds(1));
                delay.setOnFinished(event -> {
                    setIncomeGraph();
                    barChart.layout();
                    barChart.setLegendVisible(true);
                });
                delay.play();
            } else {
                barChart.getData().clear();
                clearLabels();
            }
        });
    }
}
