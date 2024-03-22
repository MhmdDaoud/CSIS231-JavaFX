package com.app.project1;

import com.app.project1.services.UserServices;
import com.app.project1.utils.UserSecurityUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    TextField usernameField, emailField;

    @FXML
    PasswordField passwordField, confirmPasswordField;

    @FXML
    Label errorLabel;

    @FXML
    Button registerBtn, loginBtn;

    private MainApplication mainApplication;

    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    public void loginPressed() {
        try {
            this.mainApplication.changeFXML("login.fxml");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

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

        UserServices.insertUser(username, email, password1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
