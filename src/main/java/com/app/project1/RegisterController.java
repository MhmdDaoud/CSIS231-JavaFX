package com.app.project1;

import com.app.project1.services.UserServices;
import com.app.project1.utils.UserSecurityUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the user registration view.
 */
public class RegisterController implements Initializable {

    @FXML
    TextField usernameField, emailField;

    @FXML
    PasswordField passwordField, confirmPasswordField;

    @FXML
    Label errorLabel;

    @FXML
    Button registerBtn;
    @FXML
    Hyperlink loginBtn;

    private MainApplication mainApplication;

    /**
     * Sets the main application for this controller.
     *
     * @param mainApplication The main application instance.
     */
    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Handles the action when the login button is pressed.
     */
    public void loginPressed() {
        try {
            this.mainApplication.changeFXML("login.fxml");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Processes the user registration when the register button is pressed.
     *
     * @param event The action event triggered by the register button.
     */
    public void processRegister(ActionEvent event) {
        String username = usernameField.getText(), email = emailField.getText(),
                password1 = passwordField.getText(), password2 = confirmPasswordField.getText();

        if (!UserSecurityUtils.isStrongPassword(password1)) {
            errorLabel.setText("Invalid Password");
        }

        if (!UserSecurityUtils.isEmail(email)) {
            errorLabel.setText("Invalid email address");
        }

        if (!password1.equals(password2)) {
            errorLabel.setText("Passwords do not match");
        }

        if (UserSecurityUtils.usernameExists(username)) {
            errorLabel.setText("Username already exists");
        }

        String generatedKey = UserSecurityUtils.generateEncryptionKey();
        if (UserServices.insertUser(username, email,
                UserSecurityUtils.encryptMessage(password1, generatedKey), generatedKey)) {
            this.mainApplication.changeFXML("login.fxml");
        }
    }

    /**
     * Initializes the controller after its root element has been completely processed.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resources specific to this controller.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
