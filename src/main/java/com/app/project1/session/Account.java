package com.app.project1.session;

public class Account {
    private int account_id;
    private String account_name;
    private double account_balance;

    public Account(int account_id, String account_name, double account_balance) {
        setAccount(account_id, account_name, account_balance);
    }

    public void setAccount(int account_id, String account_name, double account_balance) {
        setID(account_id);
        setAcctName(account_name);
        setBalance(account_balance);
    }

    public void setID(int account_id) {
        this.account_id = account_id;
    }

    public void setAcctName(String name) {
        this.account_name = name;
    }

    public void setBalance(double account_balance) {
        this.account_balance = account_balance;
    }

    public int getAccountID() {
        return account_id;
    }

    public String getAccountName() {
        return account_name;
    }

    public double getAccountBalance() {
        return account_balance;
    }
}
