package org.eduardososa.databaseprojecteleven.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TextareaController {
    @FXML
    private TextArea universalTextArea;
    @FXML
    private Button textAreaCancelButton;
    @FXML
    private Button textAreaSubmitButton;
    @FXML
    private Label universalTextAreaLabel;

    // Define the callback interface
    public interface SubmitCallback {
        void onSubmit(String text);
    }

    // Add a private field for the callback
    private SubmitCallback callback;

    public void setText(String text, String label) {
        universalTextArea.setText(text);
        universalTextAreaLabel.setText(label);
    }

    // Add a method to set the callback
    public void setSubmitCallback(SubmitCallback callback) {
        this.callback = callback;
    }

    public void cancelButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) textAreaCancelButton.getScene().getWindow();
        stage.close();
    }

    public void submitButtonOnAction(ActionEvent actionEvent) {
        // Call the callback with the text from the TextArea
        if (callback != null) {
            callback.onSubmit(universalTextArea.getText());
        }

        // Close the window
        Stage stage = (Stage) textAreaSubmitButton.getScene().getWindow();
        stage.close();
    }
}
