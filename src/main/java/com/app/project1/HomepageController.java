package com.app.project1;

import com.app.project1.session.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomepageController implements Initializable {

    @FXML
    Button transBtn, expensesBtn, budgetsBtn,  settingsBtn;

    @FXML
    Label welcomeLabel;
    private MainApplication mainApplication;
    public void setApplication(MainApplication mainApplication) {
        try {
            this.mainApplication = mainApplication;
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    private void setWelcomeLabel() {
        try {
            welcomeLabel.setText("Welcome, " + SessionManager.getCurrentUser().getUsername() + "!");
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setWelcomeLabel();
    }
}
