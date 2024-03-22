package com.app.project1;

import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

public class BudgetsController implements Initializable {

    private MainApplication mainApplication;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setApplication(MainApplication mainApplication) {
        this.mainApplication = mainApplication;
    }
}
