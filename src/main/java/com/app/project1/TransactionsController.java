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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

import static com.app.project1.services.CategoryServices.getCategoryID;
import static com.app.project1.services.TransactionServices.*;

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

    public void setApplication(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }

    public void backPressed(ActionEvent event) {
        try {
            mainApplication.changeFXML("homepage.fxml");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

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

    public void searchPressed(ActionEvent event) {
        if (!searchBar.getText().isEmpty()) {
            ObservableList<Transaction> transactions = DataQueryingUtils.searchForKeyword(
                    SessionManager.getCurrentAccount().getAccountID(), searchBar.getText().trim()
            );
            dataTable.setItems(transactions);
        }
    }

    public void filterPressed(ActionEvent event) {
        LocalDate startDate = startDateField.getValue();
        LocalDate endDate = endDateField.getValue();

        ObservableList<Transaction> transactions = DataQueryingUtils.filterByDate(
                startDate, endDate, SessionManager.getCurrentAccount().getAccountID()
        );
        dataTable.getItems().clear();
        dataTable.setItems(transactions);
    }

    public void addTransBtnPressed(ActionEvent event) {
        if (selectAccountCombo.getSelectionModel().isEmpty()) {
            return;
        }

        Stage tempStage = new Stage();
        VBox vbox = new VBox();
        HBox hBox1 = new HBox();
        HBox hBox2 = new HBox();
        VBox errorVbox = new VBox();
        vbox.getChildren().addAll(hBox1, hBox2);
        vbox.setAlignment(Pos.CENTER);

        Label errorLabel = new Label("");
        errorVbox.setAlignment(Pos.CENTER);
        errorVbox.getChildren().add(errorLabel);
        vbox.getChildren().add(errorLabel);

        TextField transactionDescField = new TextField();
        transactionDescField.setPromptText("Enter transaction description");

        DatePicker transactionDateField = new DatePicker();
        transactionDateField.setPromptText("Select transaction date");

        TextField transactionAmountField = new TextField();
        transactionAmountField.setPromptText("Enter transaction amount");

        ComboBox<String> categorySelector = new ComboBox<>();
        ObservableList<String> categories = CategoryServices.getAllCategories();
        categorySelector.setItems(categories);
        categorySelector.setMinWidth(150);

        Button addBtn = new Button("ADD");

        hBox1.getChildren().addAll(transactionDescField, transactionDateField);
        hBox1.setSpacing(5);
        hBox2.getChildren().addAll(categorySelector, transactionAmountField, addBtn);
        hBox2.setSpacing(5);

        tempStage.setScene(new Scene(vbox, 350, 100));
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

                if (insertTransaction(transactionDesc, categoryId, transactionDate, transactionAmount)) {
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

    public void deleteTransPressed() {
        if (selectAccountCombo.getSelectionModel().isEmpty()) {
            return;
        }

        Stage tempStage = new Stage();
        tempStage.setTitle("Transaction Deletion");
        tempStage.setResizable(false);

        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        Label instructLabel = new Label("Enter transaction ID");
        TextField idField = new TextField();
        idField.setPromptText("Enter transaction ID");
        Button delete = new Button("DELETE");
        Label errorLabel = new Label("");

        vbox.getChildren().addAll(instructLabel, idField, delete, errorLabel);
        vbox.setSpacing(5);
        tempStage.setScene(new Scene(vbox, 150, 125));
        tempStage.show();

        delete.setOnAction(e -> {
            if (idField.getText().isEmpty()) {
                return;
            }
            boolean confirmed = ConfirmationBox.show("Confirmation", "Are you sure you want to delete?");
            if (confirmed)
                try {
                    int transactionID = Integer.parseInt(idField.getText());
                    if (deleteTransaction(transactionID)) {
                        tempStage.close();
                    }
                } catch (Exception exe) {
                    errorLabel.setText("Invalid Transaction ID.");
                }
        });
    }

    public void editTransPressed() {
        if (selectAccountCombo.getSelectionModel().isEmpty()) {
            return;
        }

        Stage tempStage = new Stage();
        tempStage.setTitle("Edit Transaction");
        tempStage.setResizable(false);

        VBox vbox = new VBox();
        VBox vbox1 = new VBox();
        HBox hBox1 = new HBox();
        vbox1.setAlignment(Pos.CENTER);

        Label instructionLabel = new Label("Enter transaction ID to edit");
        TextField idField = new TextField();
        idField.setDisable(false);
        idField.setPromptText("Enter transaction ID");
        Button retrieveBtn = new Button("RETRIEVE");
        Label errorLabel = new Label("");

        hBox1.getChildren().addAll(instructionLabel, idField, retrieveBtn);
        hBox1.setSpacing(10);
        vbox.getChildren().addAll(hBox1, errorLabel, vbox1);

        Label descLabel = new Label("Transaction Description");
        TextField descField = new TextField();
        Label dateLabel = new Label("Transaction Date");
        DatePicker dateField = new DatePicker();
        Label amountLabel = new Label("Transaction Amount");
        TextField amountField = new TextField();
        Label catLabel = new Label("Transaction Category");
        ComboBox<String> catSelector = new ComboBox<>();
        ObservableList<String> categories = CategoryServices.getAllCategories();
        catSelector.setItems(categories);
        Button updateBtn = new Button("UPDATE");

        vbox1.getChildren().addAll(
                new HBox(10, descLabel, descField),
                new HBox(10, dateLabel, dateField),
                new HBox(10, amountLabel, amountField),
                new HBox(10, catLabel, catSelector),
                new HBox(10, updateBtn)
        );
        vbox1.setSpacing(10);
        vbox1.getChildren().forEach(node -> {
            if (node instanceof HBox hbox) {
                hbox.getChildren().forEach(child -> {
                    if (child instanceof Control control) {
                        control.setVisible(false);
                        control.setDisable(true);
                    }
                });
            }
        });

        retrieveBtn.setOnAction(e -> {
            try {
                int transaction_id = Integer.parseInt(idField.getText());
                TransactionData transactionData = TransactionServices.getTransactionFields(
                        transaction_id, SessionManager.getCurrentAccount().getAccountID()
                );

                if (transactionData != null) {
                    vbox1.getChildren().forEach(node -> {
                        if (node instanceof HBox hbox) {
                            hbox.getChildren().forEach(child -> {
                                if (child instanceof Control control) {
                                    control.setVisible(true);
                                    control.setDisable(false);
                                }
                            });
                        }
                    });
                    idField.setDisable(true);

                    descField.setText(transactionData.getTransactionDescription());
                    dateField.setValue(transactionData.getTransactionDate());
                    catSelector.setValue(transactionData.getTransactionCategory());
                    amountField.setText(transactionData.getTransactionAmount());

                    updateBtn.setOnAction(u -> {
                        String transaction_desc = descField.getText();
                        java.sql.Date transaction_date = java.sql.Date.valueOf(dateField.getValue());
                        int category_id = getCategoryID(String.valueOf(catSelector.getSelectionModel()));
                        double transaction_amount = Double.parseDouble(amountField.getText());

                        if (updateTransaction(transaction_id, transaction_desc, transaction_date, category_id, transaction_amount)) {
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

        vbox.setAlignment(Pos.CENTER);
        tempStage.setScene(new Scene(vbox, 400, 250));
        tempStage.show();
    }

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

    private void clearLabels() {
        totIncomeField.setText("Total Income: ");
        totExpensesField.setText("Total Expenses: ");
        netBalanceField.setText("Net Balance: ");
        searchBar.clear();
        startDateField.setValue(null);
        endDateField.setValue(null);
    }

    private void initializeTableView() {
        transactionIdColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_description"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_date"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_category"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("transaction_amount"));

        setDataTable();
    }

    private void setDataTable() {
        ObservableList<Transaction> transactions = DataQueryingUtils.getTableViewData(
                SessionManager.getCurrentAccount().getAccountID()
        );
        dataTable.setItems(transactions);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setSelectAccountCombo();

        selectAccountCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                SessionManager.setCurrentAccount(AccountServices.getAccountByName(newValue));
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
