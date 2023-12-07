package org.eduardososa.databaseprojecteleven;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ProjectMain extends Application {
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(ProjectMain.class.getResource("loginPage.fxml"));
            Parent root = fxmlLoader.load();
            stage.initStyle(StageStyle.UNDECORATED);
            Scene scene = new Scene(root, 600, 400);
            stage.centerOnScreen();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to create the main application scene: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
        Platform.exit();
    }
}