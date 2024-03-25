package com.app.project1.utils;

import com.app.project1.database.DBHandler;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for user security-related operations such as validation, encryption, and authentication.
 */
public class UserSecurityUtils {
    private static final String ENCRYPTION_ALGORITHM = "AES";

    /**
     * Validates an email address and password for strong security criteria.
     *
     * @param email    The email address to validate.
     * @param password The password to validate.
     * @return true if the email and password meet the validation criteria, false otherwise.
     */
    public static boolean validate(String email, String password) {
        return isEmail(email) && isStrongPassword(password);
    }

    /**
     * Checks if a given string is a valid email address.
     *
     * @param email The string to check.
     * @return true if the string is a valid email address, false otherwise.
     */
    public static boolean isEmail(String email) {
        try {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
            Pattern pattern = Pattern.compile(emailRegex);
            Matcher matcher = pattern.matcher(email);
            return matcher.matches();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a password meets strong security criteria.
     *
     * @param password The password to check.
     * @return true if the password meets the criteria, false otherwise.
     */
    public static boolean isStrongPassword(String password) {
        boolean hasLowerCase = false;
        boolean hasUpperCase = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {
            if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }
        return password.length() >= 8 && hasLowerCase && hasUpperCase && hasNumber;
    }

    /**
     * Checks if a username exists in the database.
     *
     * @param username The username to check.
     * @return true if the username exists, false otherwise.
     */
    public static boolean usernameExists(String username) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE LOWER(username) = LOWER(?);"
            );
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                resultSet.close();
                statement.close();
                return true;
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }

    /**
     * Authenticates a user's email and password.
     *
     * @param email    The user's email address.
     * @param password The user's password.
     * @return true if the authentication is successful, false otherwise.
     */
    public static boolean authenticate(String email, String password) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT user_password, encryption_key FROM users WHERE user_email = ?;"
            );
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("user_password");
                String secretKey = resultSet.getString("encryption_key");

                String decryptedStoredPassword = decryptMessage(storedPassword, secretKey);

                if (decryptedStoredPassword != null && decryptedStoredPassword.equals(password)) {
                    resultSet.close();
                    statement.close();
                    return true;
                }
            }
        } catch (SQLException exe) {
            System.out.println(exe.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }

    /**
     * Encrypts a message using a secret key.
     *
     * @param message   The message to encrypt.
     * @param secretKey The secret key for encryption.
     * @return The encrypted message as a Base64-encoded string.
     */
    public static String encryptMessage(String message, String secretKey) {
        try {
            SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), ENCRYPTION_ALGORITHM);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM + "/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(message.getBytes());

            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Decrypts an encrypted message using a secret key.
     *
     * @param encryptedMessage The encrypted message to decrypt.
     * @param secretKey        The secret key for decryption.
     * @return The decrypted message.
     */
    public static String decryptMessage(String encryptedMessage, String secretKey) {
        try {
            SecretKey key = new SecretKeySpec(Base64.getDecoder().decode(secretKey), ENCRYPTION_ALGORITHM);

            Cipher cipher = Cipher.getInstance(ENCRYPTION_ALGORITHM + "/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedMessage));

            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generates a new encryption key.
     *
     * @return The generated encryption key as a Base64-encoded string.
     */
    public static String generateEncryptionKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM);
            SecretKey secretKey = keyGen.generateKey();

            byte[] keyBytes = secretKey.getEncoded();
            return Base64.getEncoder().encodeToString(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if the specified email exists in the users table of the database.
     *
     * @param email The email to check for existence.
     * @return true if the email exists in the database, false otherwise.
     */
    public static boolean emailExists(String email) {
        DBHandler dbHandler = new DBHandler();
        try {
            PreparedStatement statement = dbHandler.getConnection().prepareStatement(
                    "SELECT * FROM users WHERE LOWER(user_email) = ?;"
            );
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            dbHandler.closeConnection();
        }
        return false;
    }
}
