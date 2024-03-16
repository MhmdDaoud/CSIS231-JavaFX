package com.app.project1.session;

import com.app.project1.utils.UserSecurityUtils;

import java.util.Date;

public class User {
    private String username;
    private String email;
    private String password;
    private Date creation_date;

    public User(String username, String email, String password, Date creation_date) {
        setUser(username, email, password, creation_date);
    }

    public void setUser(String username, String email, String password, Date creation_date) {
        setName(username);
        setEmail(email);
        setPassword(password);
        setCreationDate(creation_date);
    }

    public void setName(String username) {
        if (!username.isEmpty()) {
            this.username = username;
        }
    }

    public void setEmail(String email) {
        if (UserSecurityUtils.isEmail(email)) {
            this.email = email;
        }
    }

    public void setPassword(String password) {
        if (!password.isEmpty()) {
            this.password = password;
        }
    }

    public void setCreationDate(Date creation_date) {
        this.creation_date = creation_date;
    }

    public String getUsername() {
        try {
            return username;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }

    public String getEmail() {
        try {
            return email;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }

    public String getPassword() {
        try {
            return password;
        } catch (NullPointerException exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }

    public Date getCreationDate() {
        return creation_date;
    }
}
