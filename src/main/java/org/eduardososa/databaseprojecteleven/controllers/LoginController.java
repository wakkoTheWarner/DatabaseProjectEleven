package org.eduardososa.databaseprojecteleven.controllers;

import org.eduardososa.databaseprojecteleven.backend.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;

public class LoginController {

    @FXML
    private Button cancelButton;
    @FXML
    private Label loginMessageLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private PasswordField passwordTextField;
    private int validity = 0;
    public static String firstName;
    public static String lastName;
    public static String currentLoggedInUserEmail;

    public void cancelButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void loginButtonOnAction(ActionEvent actionEvent) throws IOException {
        if (emailTextField.getText().isBlank() != true && passwordTextField.getText().isBlank() != true) {
            validateLogin();
            if (validity == 1) {
                Stage currentStage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
                currentStage.close();
                try {
                    Stage newStage = new Stage();
                    Parent root = FXMLLoader.load(getClass().getResource("/org/eduardososa/databaseprojecteleven/dashboardPage.fxml"));
                    newStage.initStyle(StageStyle.UNDECORATED);
                    newStage.setScene(new Scene(root));
                    newStage.show();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            loginMessageLabel.setText("Email and password are required");
            emailTextField.setText("");
            passwordTextField.setText("");
        }
    }

    private void validateLogin() {
        Connection connectDB = DatabaseConnection.connect();

        if(connectDB == null) {
            JOptionPane.showMessageDialog(null, "Database Connection failed");
            return;
        }

        // Check if the table "usertable" exists.
        try {
            DatabaseMetaData md = connectDB.getMetaData();
            ResultSet rs = md.getTables(null, null, "usertable", null);
            if (!rs.next()) {
                throw new SQLException("'usertable' does not exist");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "The table usertable does not exist in the database");
            return;
        }

        String verifyLogin = "SELECT * FROM usertable WHERE email = '" + emailTextField.getText() + "' AND password = '" + passwordTextField.getText() + "'";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            if (!queryResult.next()) {
                loginMessageLabel.setText("Invalid Login. Try Again.");
                emailTextField.setText("");
                passwordTextField.setText("");
            } else {
                do {
                    if (queryResult.getInt(1) >= 1) {
                        validity = 1;

                        firstName = queryResult.getString(4);
                        lastName = queryResult.getString(5);
                        currentLoggedInUserEmail = queryResult.getString(3);
                    }
                } while (queryResult.next());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "SQL Connection Error");
            throw new RuntimeException(e);
        } finally {
            DatabaseConnection.close(connectDB);
        }
    }

    public void passwordEnterOnAction(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            if (emailTextField.getText().isBlank() != true && passwordTextField.getText().isBlank() != true) {
                validateLogin();
                if (validity == 1) {
                    Stage currentStage = (Stage) ((Node)keyEvent.getSource()).getScene().getWindow();
                    currentStage.close();
                    try {
                        Stage newStage = new Stage();
                        Parent root = FXMLLoader.load(getClass().getResource("/org/eduardososa/databaseprojecteleven/dashboardPage.fxml"));
                        newStage.initStyle(StageStyle.UNDECORATED);
                        newStage.setScene(new Scene(root));
                        newStage.show();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                loginMessageLabel.setText("Email and password are required");
                emailTextField.setText("");
                passwordTextField.setText("");
            }
        }
    }
}
