package com.app.project1.session;

/**
 * Represents a user's financial account in the application.
 */
public class Account {
    private int account_id;
    private String account_name;
    private double account_balance;

    /**
     * Constructs a new Account instance with the specified details.
     *
     * @param account_id      The account ID.
     * @param account_name    The name of the account.
     * @param account_balance The balance of the account.
     */
    public Account(int account_id, String account_name, double account_balance) {
        setAccount(account_id, account_name, account_balance);
    }

    /**
     * Sets the account details.
     *
     * @param account_id      The account ID.
     * @param account_name    The name of the account.
     * @param account_balance The balance of the account.
     */
    public void setAccount(int account_id, String account_name, double account_balance) {
        setID(account_id);
        setAcctName(account_name);
        setBalance(account_balance);
    }

    /**
     * Sets the account ID.
     *
     * @param account_id The account ID.
     */
    public void setID(int account_id) {
        this.account_id = account_id;
    }

    /**
     * Sets the name of the account.
     *
     * @param name The name of the account.
     */
    public void setAcctName(String name) {
        this.account_name = name;
    }

    /**
     * Sets the balance of the account.
     *
     * @param account_balance The balance of the account.
     */
    public void setBalance(double account_balance) {
        this.account_balance = account_balance;
    }

    /**
     * Returns the account ID.
     *
     * @return The account ID.
     */
    public int getAccountID() {
        return account_id;
    }

    /**
     * Returns the name of the account.
     *
     * @return The name of the account.
     */
    public String getAccountName() {
        return account_name;
    }

    /**
     * Returns the balance of the account.
     *
     * @return The balance of the account.
     */
    public double getAccountBalance() {
        return account_balance;
    }
}
