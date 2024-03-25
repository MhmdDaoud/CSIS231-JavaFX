package com.app.project1.session;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a budget entity with specific attributes.
 */
public class Budget {
    private int budget_id;
    private int account_id;
    private String category_name;
    private int category_id;
    private double budget_amount;
    private Date start_date;
    private Date end_date;
    private double actual_expenses;
    private double remaining_amount;
    private String budgetMonth;


    /**
     * Constructs a new Budget object with the specified parameters.
     *
     * @param budget_id     The ID of the budget.
     * @param account_id    The ID of the associated account.
     * @param category_name The name of the associated category.
     * @param budget_amount The budget amount.
     * @param start_date    The start date of the budget.
     * @param end_date      The end date of the budget.
     */
    public Budget(int budget_id, int account_id, String category_name,
                  double budget_amount, Date start_date, Date end_date) {
        setBudget(budget_id, account_id, category_name, budget_amount, start_date, end_date);
    }

    /**
     * Sets the budget attributes with the specified values.
     *
     * @param budget_id     The ID of the budget.
     * @param account_id    The ID of the associated account.
     * @param category_name The name of the associated category.
     * @param budget_amount The budget amount.
     * @param start_date    The start date of the budget.
     * @param end_date      The end date of the budget.
     */
    public void setBudget(int budget_id, int account_id, String category_name,
                          double budget_amount, Date start_date, Date end_date) {
        setBudgetId(budget_id);
        setAccountId(account_id);
        setCategoryName(category_name);
        setBudgetAmount(budget_amount);
        setStartDate(start_date);
        setEndDate(end_date);
    }

    /**
     * Gets the budget ID.
     *
     * @return The budget ID.
     */
    public int getBudgetId() {
        return budget_id;
    }

    /**
     * Sets the budget ID.
     *
     * @param budget_id The budget ID to set.
     */
    public void setBudgetId(int budget_id) {
        this.budget_id = budget_id;
    }

    /**
     * Gets the account ID associated with the budget.
     *
     * @return The account ID.
     */
    public int getAccountId() {
        return account_id;
    }

    /**
     * Sets the account ID associated with the budget.
     *
     * @param account_id The account ID to set.
     */
    public void setAccountId(int account_id) {
        this.account_id = account_id;
    }

    /**
     * Gets the category name associated with the budget.
     *
     * @return The category name.
     */
    public String getCategoryName() {
        return category_name;
    }

    /**
     * Sets the category name associated with the budget.
     *
     * @param category_name The category name to set.
     */
    public void setCategoryName(String category_name) {
        this.category_name = category_name;
    }

    /**
     * Gets the category ID associated with the budget.
     *
     * @return The category ID.
     */
    public int getCategoryId() {
        return category_id;
    }

    /**
     * Sets the category ID for the budget.
     *
     * @param category_id The category ID to set.
     */
    public void setCategoryId(int category_id) {
        this.category_id = category_id;
    }

    /**
     * Gets the budget amount.
     *
     * @return The budget amount.
     */
    public double getBudgetAmount() {
        return budget_amount;
    }

    /**
     * Sets the budget amount.
     *
     * @param budget_amount The budget amount to set.
     */
    public void setBudgetAmount(double budget_amount) {
        this.budget_amount = budget_amount;
    }

    /**
     * Gets the start date of the budget.
     *
     * @return The start date of the budget.
     */
    public Date getStartDate() {
        return start_date;
    }

    /**
     * Sets the start date of the budget.
     *
     * @param start_date The start date to set to.
     */
    public void setStartDate(Date start_date) {
        this.start_date = start_date;
    }

    /**
     * Gets the end date of the budget.
     *
     * @return The end date of the budget.
     */
    public Date getEndDate() {
        return end_date;
    }

    /**
     * Sets the end date of the budget
     *
     * @param end_date The end date to set to.
     */
    public void setEndDate(Date end_date) {
        this.end_date = end_date;
    }

    public double getActualExpenses() {
        return actual_expenses;
    }

    /**
     * Sets the actual expenses of the budget.
     *
     * @param actual_expenses The actual expenses to set.
     */
    public void setActualExpenses(double actual_expenses) {
        this.actual_expenses = actual_expenses;
    }

    public double getRemainingAmount() {
        return remaining_amount;
    }

    /**
     * Sets the remaining amount of the budget.
     *
     * @param remaining_amount The remaining amount to set.
     */
    public void setRemainingAmount(double remaining_amount) {
        this.remaining_amount = remaining_amount;
    }

    /**
     * Sets the budget month based on the start date.
     *
     * */
    public void setBudgetMonth() {
        LocalDate localStartDate = start_date.toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
        this.budgetMonth = localStartDate.format(formatter);
    }


    /**
     * Gets the month of the budget.
     *
     * @return The month of the budget.
     */
    public String getBudgetMonth() {
        return budgetMonth;
    }
}
