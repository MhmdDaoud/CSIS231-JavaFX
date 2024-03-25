package com.app.project1;

import com.app.project1.services.UserServices;
import com.app.project1.session.SessionManager;
import com.app.project1.session.User;
import com.app.project1.utils.UserSecurityUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller class for the login view.
 */
public class LoginController implements Initializable {

    @FXML
    private Button loginBtn;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Hyperlink registerLink;

    private MainApplication mainApplication;

    /**
     * Sets the main application instance for this controller.
     *
     * @param mainApplication The MainApplication instance.
     */
    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Handles the login process when the login button is pressed.
     *
     * @param event The ActionEvent triggered by clicking the login button.
     */
    public void processLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        if (UserSecurityUtils.authenticate(email, password)) {
            User user = UserServices.getUserByEmail(email);
            SessionManager.login(user);
            mainApplication.changeFXML("homepage.fxml");
        } else {
            errorLabel.setText("Incorrect email or password");
            passwordField.clear();
        }
    }

    /**
     * Redirects the user to the registration view when the register hyperlink is clicked.
     *
     * @param event The ActionEvent triggered by clicking the register hyperlink.
     */
    public void registerRedirect(ActionEvent event) {
        try {
            mainApplication.changeFXML("register.fxml");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    /**
     * Initializes the controller.
     *
     * @param url            The location used to resolve relative paths for the root object.
     * @param resourceBundle The resource bundle that contains locale-specific data.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
