package com.app.project1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        try {
            this.stage = stage;
            changeFXML("login.fxml");
            stage.setTitle("Finance Application");
            stage.setResizable(false);
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
    }

    public Initializable changeFXML(String fxml) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource((fxml)));
            stage.setScene(new Scene(fxmlLoader.load()));

            switch (fxml) {
                case "login.fxml" -> {
                    LoginController loginController = fxmlLoader.getController();
                    loginController.setApplication(this);
                }
                case "homepage.fxml" -> {
                    HomepageController homepageController = fxmlLoader.getController();
                    homepageController.setApplication(this);
                }
                case "register.fxml" -> {
                    RegisterController registerController = fxmlLoader.getController();
                    registerController.setApplication(this);
                }
            }

            stage.show();
            return fxmlLoader.getController();
        } catch (Exception exe) {
            System.out.println(exe.getMessage());
        }
        return null;
    }
}