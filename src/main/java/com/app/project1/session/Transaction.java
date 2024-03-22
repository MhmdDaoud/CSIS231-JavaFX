package com.app.project1.session;

import java.util.Date;

public class Transaction {

    private int transaction_id;
    private String transaction_description;
    private Date transaction_date;
    private String transaction_category;
    private double transaction_amount;

    public Transaction(int transaction_id, String transaction_description,
                       Date transaction_date, String transaction_category, double transaction_amount) {
        setTransaction_id(transaction_id);
        setTransaction_description(transaction_description);
        setTransaction_date(transaction_date);
        setTransaction_category(transaction_category);
        setTransaction_amount(transaction_amount);
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getTransaction_description() {
        return transaction_description;
    }

    public void setTransaction_description(String transaction_description) {
        this.transaction_description = transaction_description;
    }

    public Date getTransaction_date() {
        return transaction_date;
    }

    public void setTransaction_date(Date transaction_date) {
        this.transaction_date = transaction_date;
    }

    public String getTransaction_category() {
        return transaction_category;
    }

    public void setTransaction_category(String transaction_category) {
        this.transaction_category = transaction_category;
    }

    public double getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(double transaction_amount) {
        this.transaction_amount = transaction_amount;
    }
}
