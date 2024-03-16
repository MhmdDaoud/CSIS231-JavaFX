package com.app.project1;

import com.app.project1.session.SessionManager;
import com.app.project1.session.User;
import com.app.project1.utils.UserSecurityUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    Button loginBtn;

    @FXML
    TextField emailField;

    @FXML
    PasswordField passwordField;

    @FXML
    Label errorLabel;

    @FXML
    Hyperlink registerLink;

    private MainApplication mainApplication;

    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    public void processLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (UserSecurityUtils.authenticate(email, password)) {
            User user = UserSecurityUtils.getUserByEmail(email);
            SessionManager.login(user);
            mainApplication.changeFXML("homepage.fxml");
        } else {
            errorLabel.setText("Incorrect email or password");
            passwordField.clear();
        }
    }

    public void registerRedirect(ActionEvent event) {
        try {
            mainApplication.changeFXML("register.fxml");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
