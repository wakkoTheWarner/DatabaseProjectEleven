package org.eduardososa.databaseprojecteleven.controllers;

import org.eduardososa.databaseprojecteleven.backend.DatabaseConnection;
import org.eduardososa.databaseprojecteleven.backend.User;
import org.eduardososa.databaseprojecteleven.backend.ValidationResult;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class UserController implements Initializable {
    @FXML
    private Button exitButton;
    @FXML
    private Label userNameLabel;

    // -- //

    @FXML
    private TextField userIdTextField;
    @FXML
    private TextField userPasswordTextField;
    @FXML
    private TextField userEmailTextField;
    @FXML
    private TextField userFirstNameTextField;
    @FXML
    private TextField userLastNameTextField;
    @FXML
    private TextField userAccountTextField;
    @FXML
    private Button newUserButton;
    @FXML
    private Button updateUserButton;
    @FXML
    private Button deleteUserButton;

    // -- //

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, Integer> userIDCol;
    @FXML
    private TableColumn<User, String> userPasswordCol;
    @FXML
    private TableColumn<User, String> userEmailCol;
    @FXML
    private TableColumn<User, String> userFirstNameCol;
    @FXML
    private TableColumn<User, String> userLastNameCol;
    @FXML
    private TableColumn<User, String> userAccountTypeCol;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameLabel.setText(LoginController.firstName + " " + LoginController.lastName);
        newUserButton.setDisable(false);
        updateUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
        loadUserData();
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                userIdTextField.setText(String.valueOf(newSelection.getUserID()));
                userPasswordTextField.setText(newSelection.getUserPassword());
                userEmailTextField.setText(newSelection.getUserEmail());
                userFirstNameTextField.setText(newSelection.getUserFirstName());
                userLastNameTextField.setText(newSelection.getUserLastName());
                userAccountTextField.setText(newSelection.getUserAccountType());
                newUserButton.setDisable(true);
                updateUserButton.setDisable(false);
                deleteUserButton.setDisable(false);
            }
            if (userIdTextField.getText().equals("1") && userEmailTextField.getText().equals("admin")) {
                newUserButton.setDisable(true);
                updateUserButton.setDisable(true);
                deleteUserButton.setDisable(true);
            }
        });
    }

    public void exitButtonOnAction(ActionEvent actionEvent) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    public void logOutButtonOnAction(ActionEvent event) {
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/org/eduardososa/databaseprojecteleven/loginPage.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void homeButtonOnAction(ActionEvent actionEvent) {
        try {
            Parent dashboardParent = FXMLLoader.load(getClass().getResource("/org/eduardososa/databaseprojecteleven/dashboardPage.fxml"));
            Scene dashboardScene = new Scene(dashboardParent);

            // Get the Stage information
            Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

            window.setScene(dashboardScene);
            window.show();
        } catch (IOException e) {
            System.err.println("Failed to open dashboard page: " + e.getMessage());
        }
    }

    public void newUserOnAction(ActionEvent actionEvent) {

        ValidationResult validationResult = validateUserInput(userPasswordTextField.getText(), userEmailTextField.getText(), userFirstNameTextField.getText(), userLastNameTextField.getText(), userAccountTextField.getText());
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO usertable (password, email, firstName, lastName, accountType) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, userPasswordTextField.getText());
            ps.setString(2, userEmailTextField.getText());
            ps.setString(3, userFirstNameTextField.getText());
            ps.setString(4, userLastNameTextField.getText());
            ps.setString(5, userAccountTextField.getText());
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "User added successfully!");
            loadUserData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to add user: " + e.getMessage());
        }
    }

    public void updateUserOnAction(ActionEvent actionEvent) {
        if (userIdTextField.getText().equals("1")) {
            JOptionPane.showMessageDialog(null, "Cannot modify or delete admin user");
            universalReset();
            return;
        }

        ValidationResult validationResult = validateUserInput(userPasswordTextField.getText(), userEmailTextField.getText(), userFirstNameTextField.getText(), userLastNameTextField.getText(), userAccountTextField.getText());
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("UPDATE usertable SET password = ?, email = ?, firstName = ?, lastName = ?, accountType = ? WHERE userID = ?")) {
            ps.setString(1, userPasswordTextField.getText());
            ps.setString(2, userEmailTextField.getText());
            ps.setString(3, userFirstNameTextField.getText());
            ps.setString(4, userLastNameTextField.getText());
            ps.setString(5, userAccountTextField.getText());
            ps.setInt(6, Integer.parseInt(userIdTextField.getText()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "User updated successfully!");
            loadUserData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to update user: " + e.getMessage());
        }
    }

    public void deleteUserOnAction(ActionEvent actionEvent) {
        if (userIdTextField.getText().equals("1")) {
            JOptionPane.showMessageDialog(null, "Cannot modify or delete admin user");
            universalReset();
            return;
        }

        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("DELETE FROM usertable WHERE userID = ?")) {
            ps.setInt(1, Integer.parseInt(userIdTextField.getText()));
            if (userEmailTextField.getText().equals(LoginController.currentLoggedInUserEmail)) {
                if (JOptionPane.showConfirmDialog(null, "You are deleting your own account. You will be logged out. Continue?", "WARNING", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    ps.executeUpdate();
                    JOptionPane.showMessageDialog(null, "User deleted successfully!");
                    logOutButtonOnAction(actionEvent);
                    return;
                }
            }
            loadUserData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to delete user: " + e.getMessage());
        }
    }

    public void userResetOnAction(ActionEvent actionEvent) {
        universalReset();
    }

    public void userRefreshButtonOnAction(ActionEvent actionEvent) {
        loadUserData();
    }

    public void loadUserData() {
        ObservableList<User> userList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM usertable")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userList.add(new User(
                    rs.getInt("userID"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("accountType")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Failed to load user data: " + e.getMessage());
        }

        userTable.setItems(userList);
        userIDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        userPasswordCol.setCellValueFactory(new PropertyValueFactory<>("userPassword"));
        userEmailCol.setCellValueFactory(new PropertyValueFactory<>("userEmail"));
        userFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("userFirstName"));
        userLastNameCol.setCellValueFactory(new PropertyValueFactory<>("userLastName"));
        userAccountTypeCol.setCellValueFactory(new PropertyValueFactory<>("userAccountType"));
    }

    public ValidationResult validateUserInput(String password, String email, String firstName, String lastName, String accountType) {
        // Check if any field is empty
        if (password.isEmpty() || email.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || accountType.isEmpty()) {
            return new ValidationResult(false, "All fields are required");
        }

        // Check if email is valid
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (!email.matches(emailRegex)) {
            return new ValidationResult(false, "Email is not valid");
        }

        // Check if password is secure
        // This is a basic check, you might want to use a more robust regex for production code
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        if (!password.matches(passwordRegex)) {
            String errorMessage = "Password must meet the following requirements:\n" +
                                  "- At least one digit\n" +
                                  "- At least one lowercase letter\n" +
                                  "- At least one uppercase letter\n" +
                                  "- At least one special character (@#$%^&+=)\n" +
                                  "- No whitespace allowed\n" +
                                  "- At least 8 characters long";
            return new ValidationResult(false, errorMessage);
        }

        return new ValidationResult(true, "");
    }

    public void universalReset() {
        userIdTextField.clear();
        userPasswordTextField.clear();
        userEmailTextField.clear();
        userFirstNameTextField.clear();
        userLastNameTextField.clear();
        userAccountTextField.clear();
        userTable.getSelectionModel().clearSelection();

        newUserButton.setDisable(false);
        updateUserButton.setDisable(true);
        deleteUserButton.setDisable(true);
    }
}
