package com.app.project1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main entry point for the finance application.
 */
public class MainApplication extends Application {

    /**
     * The primary stage for the application.
     */
    public Stage stage;

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Starts the application by setting up the initial stage and loading the login view.
     *
     * @param stage The primary stage for the application.
     */
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

    /**
     * Changes the FXML view based on the provided FXML file name and sets up the corresponding controller.
     *
     * @param fxml The name of the FXML file to load.
     * @return The initialized controller for the loaded FXML view.
     */
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
                case "transactions.fxml" -> {
                    TransactionsController transactionsController = fxmlLoader.getController();
                    transactionsController.setApplication(this);
                }
                case "budgets.fxml" -> {
                    BudgetsController budgetsController = fxmlLoader.getController();
                    budgetsController.setApplication(this);
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
