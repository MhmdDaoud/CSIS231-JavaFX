package com.app.project1.session;

public class SessionManager {
    private static volatile User currentUser;

    public static synchronized void login(User user) {
        currentUser = user;
    }

    public static synchronized void logout() {
        currentUser = null;
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
}

