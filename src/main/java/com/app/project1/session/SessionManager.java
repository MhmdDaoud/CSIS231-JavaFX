package com.app.project1.session;

/**
 * Manages the current user session and account information.
 */
public class SessionManager {
    private static volatile User currentUser;
    private static volatile Account currentAccount;

    /**
     * Logs in the specified user.
     *
     * @param user The user to log in.
     */
    public static synchronized void login(User user) {
        currentUser = user;
    }

    /**
     * Logs out the current user.
     */
    public static synchronized void logout() {
        currentUser = null;
    }

    /**
     * Sets the current account.
     *
     * @param account The account to set as current.
     */
    public static synchronized void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return True if a user is logged in, false otherwise.
     */
    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return The current user or null if no user is logged in.
     */
    public static synchronized User getCurrentUser() {
        try {
            return currentUser;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }

    /**
     * Gets the current account.
     *
     * @return The current account or null if no account is set.
     */
    public static synchronized Account getCurrentAccount() {
        try {
            return currentAccount;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }
}
