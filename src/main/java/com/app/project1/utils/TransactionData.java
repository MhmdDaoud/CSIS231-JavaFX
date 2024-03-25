package com.app.project1.utils;

import java.time.LocalDate;

/**
 * Represents transaction data with description, date, category, and amount.
 */
public class TransactionData {
    private String transactionDescription;
    private LocalDate transactionDate;
    private String transactionCategory;
    private double transactionAmount;

    /**
     * Constructs a TransactionData object with the specified attributes.
     *
     * @param transactionDescription The description of the transaction.
     * @param transactionDate        The date of the transaction.
     * @param transactionCategory    The category of the transaction.
     * @param transactionAmount      The amount of the transaction.
     */
    public TransactionData(String transactionDescription, LocalDate transactionDate,
                           String transactionCategory, double transactionAmount) {
        setTransactionDescription(transactionDescription);
        setTransactionDate(transactionDate);
        setTransactionCategory(transactionCategory);
        setTransactionAmount(transactionAmount);
    }

    /**
     * Gets the description of the transaction.
     *
     * @return The transaction description.
     */
    public String getTransactionDescription() {
        return transactionDescription;
    }

    /**
     * Sets the description of the transaction.
     *
     * @param transactionDescription The transaction description to set.
     */
    public void setTransactionDescription(String transactionDescription) {
        this.transactionDescription = transactionDescription;
    }

    /**
     * Gets the date of the transaction.
     *
     * @return The transaction date.
     */
    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the date of the transaction.
     *
     * @param transactionDate The transaction date to set.
     */
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    /**
     * Gets the category of the transaction.
     *
     * @return The transaction category.
     */
    public String getTransactionCategory() {
        return transactionCategory;
    }

    /**
     * Sets the category of the transaction.
     *
     * @param transactionCategory The transaction category to set.
     */
    public void setTransactionCategory(String transactionCategory) {
        this.transactionCategory = transactionCategory;
    }

    /**
     * Gets the amount of the transaction as a string.
     *
     * @return The transaction amount as a string.
     */
    public String getTransactionAmount() {
        return Double.toString(transactionAmount);
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param transactionAmount The transaction amount to set.
     */
    public void setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }
}
