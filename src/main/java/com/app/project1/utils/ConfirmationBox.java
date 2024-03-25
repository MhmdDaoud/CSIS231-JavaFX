package com.app.project1.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * Utility class for displaying confirmation dialogs.
 */
public class ConfirmationBox {

    /**
     * Displays a confirmation dialog with the specified title and message.
     *
     * @param title   The title of the confirmation dialog.
     * @param message The message displayed in the confirmation dialog.
     * @return True if the user clicks OK, false otherwise.
     */
    public static boolean show(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }
}
