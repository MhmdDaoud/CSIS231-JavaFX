package com.app.project1.utils;

import java.time.LocalDate;

public class TransactionData {
    private String transactionDescription;
    private LocalDate transactionDate;
    private String transactionCategory;
    private double transactionAmount;

    public TransactionData(String transactionDescription, LocalDate transactionDate,
                           String transactionCategory, double transactionAmount) {
        this.transactionDescription = transactionDescription;
        this.transactionDate = transactionDate;
        this.transactionCategory = transactionCategory;
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionDescription() {
        return transactionDescription;
    }

    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionCategory() {
        return transactionCategory;
    }

    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    public String getTransactionAmount() {
        return Double.toString(transactionAmount);
    }

    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
