package com.app.project1.session;

import com.app.project1.utils.UserSecurityUtils;

import java.util.Date;

/**
 * Represents a user in the application.
 */
public class User {
    private int id;
    private String username;
    private String email;
    private String password;
    private Date creationDate;
    private String encryptionKey;

    /**
     * Constructs a new User instance with the specified details.
     *
     * @param id           The user ID.
     * @param username     The username.
     * @param email        The email address.
     * @param password     The password.
     * @param creationDate The creation date of the user.
     * @param encryptionKey The encryption key for the user's password.
     */
    public User(int id, String username, String email, String password, Date creationDate, String encryptionKey) {
        setUser(id, username, email, password, creationDate, encryptionKey);
    }

    /**
     * Sets the user details.
     *
     * @param id           The user ID.
     * @param username     The username.
     * @param email        The email address.
     * @param password     The password.
     * @param creationDate The creation date of the user.
     * @param encryptionKey The encryption key for the user's password.
     */
    public void setUser(int id, String username, String email, String password, Date creationDate, String encryptionKey) {
        setId(id);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setCreationDate(creationDate);
        setEncryptionKey(encryptionKey);
    }

    /**
     * Sets the user ID.
     *
     * @param id The user ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the username.
     *
     * @param username The username.
     */
    public void setUsername(String username) {
        if (!username.isEmpty()) {
            this.username = username;
        }
    }

    /**
     * Sets the email address.
     *
     * @param email The email address.
     */
    public void setEmail(String email) {
        if (UserSecurityUtils.isEmail(email)) {
            this.email = email;
        }
    }

    /**
     * Sets the password.
     *
     * @param password The password.
     */
    public void setPassword(String password) {
        if (!password.isEmpty()) {
            this.password = password;
        }
    }

    /**
     * Sets the creation date of the user.
     *
     * @param creationDate The creation date.
     */
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Sets the encryption key for the user's password.
     *
     * @param encryptionKey The encryption key.
     */
    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    /**
     * Returns the user ID.
     *
     * @return The user ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the email address.
     *
     * @return The email address.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the creation date of the user.
     *
     * @return The creation date.
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * Returns the encryption key for the user's password.
     *
     * @return The encryption key.
     */
    public String getEncryptionKey() {
        return encryptionKey;
    }
}
