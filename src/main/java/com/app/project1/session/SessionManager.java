package com.app.project1.session;

public class SessionManager {
    private static volatile User currentUser;
    private static volatile Account currentAccount;

    public static synchronized void login(User user) {
        currentUser = user;
    }

    public static synchronized void logout() {
        currentUser = null;
    }

    public static synchronized void setCurrentAccount(Account account) {
        currentAccount = account;
    }

    public static synchronized boolean isLoggedIn() {
        return currentUser != null;
    }

    public static synchronized User getCurrentUser() {
        try {
            return currentUser;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }

    public static synchronized Account getCurrentAccount() {
        try {
            return currentAccount;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }
}

