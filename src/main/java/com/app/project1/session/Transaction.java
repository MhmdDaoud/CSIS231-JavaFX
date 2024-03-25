package com.app.project1.session;

import java.util.Date;

/**
 * Represents a financial transaction in the application.
 */
public class Transaction {

    private int transaction_id;
    private String transaction_description;
    private Date transaction_date;
    private String transaction_category;
    private double transaction_amount;

    /**
     * Constructs a new Transaction instance with the specified details.
     *
     * @param transaction_id         The transaction ID.
     * @param transaction_description The description of the transaction.
     * @param transaction_date       The date of the transaction.
     * @param transaction_category   The category of the transaction.
     * @param transaction_amount     The amount of the transaction.
     */
    public Transaction(int transaction_id, String transaction_description,
                       Date transaction_date, String transaction_category, double transaction_amount) {
        setTransaction_id(transaction_id);
        setTransaction_description(transaction_description);
        setTransaction_date(transaction_date);
        setTransaction_category(transaction_category);
        setTransaction_amount(transaction_amount);
    }

    /**
     * Returns the transaction ID.
     *
     * @return The transaction ID.
     */
    public int getTransaction_id() {
        return transaction_id;
    }

    /**
     * Sets the transaction ID.
     *
     * @param transaction_id The transaction ID.
     */
    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    /**
     * Returns the description of the transaction.
     *
     * @return The transaction description.
     */
    public String getTransaction_description() {
        return transaction_description;
    }

    /**
     * Sets the description of the transaction.
     *
     * @param transaction_description The transaction description.
     */
    public void setTransaction_description(String transaction_description) {
        this.transaction_description = transaction_description;
    }

    /**
     * Returns the date of the transaction.
     *
     * @return The transaction date.
     */
    public Date getTransaction_date() {
        return transaction_date;
    }

    /**
     * Sets the date of the transaction.
     *
     * @param transaction_date The transaction date.
     */
    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }

    /**
     * Returns the category of the transaction.
     *
     * @return The transaction category.
     */
    public String getTransaction_category() {
        return transaction_category;
    }

    /**
     * Sets the category of the transaction.
     *
     * @param transaction_category The transaction category.
     */
    public void setTransaction_category(String transaction_category) {
        this.transaction_category = transaction_category;
    }

    /**
     * Returns the amount of the transaction.
     *
     * @return The transaction amount.
     */
    public double getTransaction_amount() {
        return transaction_amount;
    }

    /**
     * Sets the amount of the transaction.
     *
     * @param transaction_amount The transaction amount.
     */
    public void setTransaction_amount(double transaction_amount) {
        this.transaction_amount = transaction_amount;
    }
}
