package org.eduardososa.databaseprojecteleven.controllers;

import org.eduardososa.databaseprojecteleven.backend.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private Label userNameLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameLabel.setText(LoginController.firstName + " " + LoginController.lastName);
        loadProgramData();
        programTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Program selectedProgram = programTable.getSelectionModel().getSelectedItem();
                programIDTextField.setText(String.valueOf(selectedProgram.getProgramId()));
                programNameTextField.setText(selectedProgram.getProgramName());
            }
        });
        loadCourseData();
        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
                courseIdTextBox.setText(String.valueOf(selectedCourse.getCourseId()));
                courseCompetencyIdComboBox.getSelectionModel().select(Integer.valueOf(selectedCourse.getCompetencyId()));
                courseProfessorIdComboBox.getSelectionModel().select(String.valueOf(selectedCourse.getProfessorId()));
                courseNameTextBox.setText(String.valueOf(selectedCourse.getCourseName()));
                courseObjectiveTextBox.setText(String.valueOf(selectedCourse.getObjectiveDescription()));
                courseEvalTextBox.setText(String.valueOf(selectedCourse.getEvaluationInstrument()));
                courseCompTextBox.setText(String.valueOf(selectedCourse.getCompMetric()));
            }
        });
        loadProfessorData();
        professorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Professor selectedProfessor = professorTable.getSelectionModel().getSelectedItem();
                profIdTextBox.setText(String.valueOf(selectedProfessor.getProfessorID()));
                profFirstNameTextBox.setText(String.valueOf(selectedProfessor.getProfessorName()));
                profLastNameTextBox.setText(String.valueOf(selectedProfessor.getProfessorLastName()));
                profEmailTextBox.setText(String.valueOf(selectedProfessor.getProfessorEmail()));
                profPhoneTextBox.setText(String.valueOf(selectedProfessor.getProfessorPhone()));
            }
        });
        loadCompetencyData();
        competencyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Competency selectedCompetency = competencyTable.getSelectionModel().getSelectedItem();
                competencyIDTextField.setText(String.valueOf(selectedCompetency.getCompetencyId()));
                compDescTextField.setText(String.valueOf(selectedCompetency.getCompetencyDescription()));
            }
        });
        loadProgramSelectionData();
        programSelectionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ProgSelection selectedProgram = programSelectionTable.getSelectionModel().getSelectedItem();
                programSelectionTextField.setText(String.valueOf(selectedProgram.getProgSelection()));
                newSelectionButton.setDisable(false);
                programcourseTable.getSelectionModel().clearSelection();
                selectionIDTextField.clear();
                updateSelectionButton.setDisable(true);
            }
        });
        courseSelectionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                CourseSelection selectedCourse = courseSelectionTable.getSelectionModel().getSelectedItem();
                courseSelectionTextField.setText(String.valueOf(selectedCourse.getCourseSelection()));
            }
        });
        programcourseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                ProgramCourse selectedProgramCourse = programcourseTable.getSelectionModel().getSelectedItem();
                selectionIDTextField.setText(String.valueOf(selectedProgramCourse.getSelectionId()));
                programSelectionTextField.setText(String.valueOf(selectedProgramCourse.getProgramNameSelection()));
                courseSelectionTextField.setText(String.valueOf(selectedProgramCourse.getCourseIdSelection()));
                newSelectionButton.setDisable(true);
                updateSelectionButton.setDisable(false);
                programSelectionTable.getSelectionModel().clearSelection();
                courseSelectionTable.getSelectionModel().clearSelection();
            }
        });
    }

    /*
     * 
     * ================================================================================================================================
     * 
     * This Section contains univsersal Exit and Log Out Buttons----------------------------------------------------------------
     * 
     * ================================================================================================================================
     *
     */

    @FXML
    private Button exitButton;

    /**
     * Close the current stage
     */
    public void exitButtonOnAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Logs out the user and redirects to login page
     * @param_event
     */
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

    public void userEditorButtonOnAction(ActionEvent actionEvent) {
        try {
            Parent userEditorParent = FXMLLoader.load(getClass().getResource("/org/eduardososa/databaseprojecteleven/userEditorPage.fxml"));
            Scene userEditorScene = new Scene(userEditorParent);

            // Get the Stage information
            Stage window = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();

            window.setScene(userEditorScene);
            window.show();
        } catch (IOException e) {
            System.err.println("Failed to open user editor page: " + e.getMessage());
        }
    }

    /*
     * 
     * ================================================================================================================================
     * 
     * This Section contains Program Table Edits----------------------------------------------------------------
     * 
     * ================================================================================================================================
     *
     */

    @FXML
    private TextField programIDTextField;
    @FXML
    private TextField programNameTextField;

    int validator = 0;

    @FXML
    private TableView<Program> programTable;
    @FXML
    private TableColumn<Program, Integer> programIdCol;
    @FXML
    private TableColumn<Program, String> programNameCol;

    /**
     * Fetches updated data and updates the program in the database
     *
     * @param actionEvent the event triggered when the update program button is clicked
     */
    public void updateProgramButtonOnClick(ActionEvent actionEvent) {

        // Retrieve values from all text fields
        String programName = programNameTextField.getText().trim();

        // Validate the input
        ValidationResult validationProgResult = validateProgInput(programName);
        if (!validationProgResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationProgResult.getErrorMessage());
            return;
        }

        // Check and parse integer
        String programIDText = programIDTextField.getText();
        int programID;
        try {
            programID = Integer.parseInt(programIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid Program ID: Must be an integer!");
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Connect to the database
            conn = DatabaseConnection.connect();

            // Prepare the SQL query to update all fields
            String sql = "UPDATE academicprogram SET programName = ? WHERE programID = ?";
            pstmt = conn.prepareStatement(sql);

            // Set parameters for the prepared statement
            pstmt.setString(1, programName);
            pstmt.setInt(2, programID);

            // Execute the update
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Program Updated Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Program Update Failed: " + e.getMessage());
            //Consider logging as well here.
        } finally {
            try {
                // Always ensure the resources are closed to prevent leaks.
                if(pstmt != null) {
                    pstmt.close();
                }
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                //Consider logging this as well.
                JOptionPane.showMessageDialog(null, "Error while closing the database connection and statement.");
            }
        }

        loadProgramData();
        automaticResetProgram();
    }

    /**
     * Re-load data from database
     * @param_actionEvent
     */
    public void refreshProgramButtonOnAction(ActionEvent actionEvent) {
        loadProgramData();
        loadCourseData();
        loadProfessorData();
        loadCompetencyData();
    }

    /**
     * Inserts a new program in database
     * @param_actionEvent
     */
    public void newProgramButtonOnClick(ActionEvent actionEvent) {
        String programName = programNameTextField.getText().trim();
        ValidationResult validationResult = validateProgInput(programName);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "INSERT INTO academicprogram (programName) VALUES (?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, programName);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Program Created Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Program Creation Failed: " + e.getMessage());
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Failed to close PreparedStatement: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(null, "Failed to close Connection: " + e.getMessage());
                }
            }
        }
        loadProgramData();
        automaticResetProgram();
    }

    /**
     * Deletes program from database
     * @param_actionEvent
     */
    public void deleteProgramButtonOnAction(ActionEvent actionEvent) {
        Program selectedProgram = programTable.getSelectionModel().getSelectedItem();
        if (selectedProgram != null) {
            int programId = selectedProgram.getProgramId();
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM academicprogram WHERE programId = ?")) {
                pstmt.setInt(1, programId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Program Deleted Successfully");
                    loadProgramData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "Program Deletion Failed");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error during deletion process: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a Program to delete");
        }
    }

    /**
     * Resets program text fields
     */
    public void resetProgramTextButtonOnClick(ActionEvent actionEvent) {
        automaticResetProgram();
    }

    /**
     * Loads program data from database
     */
    private void loadProgramData() {
        ObservableList<Program> programList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM academicprogram")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int programId = rs.getInt("programID");
                String programName = rs.getString("programName");
                Program program = new Program(programId, programName);
                programList.add(program);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during data loading: " + e.getMessage());
        }

        programTable.setItems(programList);
        programIdCol.setCellValueFactory(new PropertyValueFactory<>("programId"));
        programNameCol.setCellValueFactory(new PropertyValueFactory<>("programName"));
    }

    private void automaticResetProgram() {
        programIDTextField.clear();
        programNameTextField.clear();
        programTable.getSelectionModel().clearSelection();
    }

    private ValidationResult validateProgInput(String programName) {
        // Check if any field is empty
        if (programName.isEmpty()) {
            return new ValidationResult(false, "All fields must be filled");
        }

        // If all checks pass, return a valid result
        return new ValidationResult(true, "");
    }

    /*
     * 
     * ================================================================================================================================
     * 
     * This Section contains Course Table Edits ----------------------------------------------------------------
     * 
     * ================================================================================================================================
     *
     */

    @FXML
    private TextField courseIdTextBox;
    @FXML
    private ComboBox<Integer> courseCompetencyIdComboBox;
    @FXML
    private ComboBox<String> courseProfessorIdComboBox;
    @FXML
    private TextField courseNameTextBox;
    @FXML
    private TextField courseObjectiveTextBox;
    @FXML
    private TextField courseEvalTextBox;
    @FXML
    private TextField courseCompTextBox;

    // -- //

    @FXML
    private TableView <Course> courseTable;
    @FXML
    private TableColumn<Course, String> courseIdCol;
    @FXML
    private TableColumn<Course, Integer> courseCompetencyIdCol;
    @FXML
    private TableColumn<Course, String> courseProfessorIdCol;
    @FXML
    private TableColumn<Course, String> courseNameCol;
    @FXML
    private TableColumn<Course, String> objectiveDescCol;
    @FXML
    private TableColumn<Course, String> evalInsCol;
    @FXML
    private TableColumn<Course, String> compMetricCol;

    /**
     * Fetches updated data and update in database
    * @param_actionEvent
    */
    public void updateCourseButtonOnClick(ActionEvent actionEvent) {
        // Retrieve values from all text fields
        String courseId = courseIdTextBox.getText().trim();
        Integer competencyId = courseCompetencyIdComboBox.getSelectionModel().getSelectedItem();
        String professorId = courseProfessorIdComboBox.getSelectionModel().getSelectedItem();
        String courseName = courseNameTextBox.getText().trim();
        String objectiveDescription = courseObjectiveTextBox.getText().trim();
        String evaluationInstrument = courseEvalTextBox.getText().trim();
        String compMetric = courseCompTextBox.getText().trim();

        if (!courseId.isEmpty() && !courseName.isEmpty()) {
            // Connect to the database
            try (Connection conn = DatabaseConnection.connect()) {
                // Prepare the SQL query to update all fields
                String sql = "UPDATE concentrationcourses SET competencyId = ?, professorId = ?, courseName = ?, objectiveDescription = ?, evaluationInstrument = ?, compMetric = ? WHERE courseId = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);

                // Set parameters for the prepared statement
                pstmt.setInt(1, competencyId);
                pstmt.setString(2, professorId);
                pstmt.setString(3, courseName);
                pstmt.setString(4, objectiveDescription);
                pstmt.setString(5, evaluationInstrument);
                pstmt.setString(6, compMetric);
                pstmt.setString(7, courseId);

                // Execute the update
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    loadCourseData();
                    JOptionPane.showMessageDialog(null, "Course Updated Successfully");
                } else {
                    loadCourseData();
                    JOptionPane.showMessageDialog(null, "Course Update Failed.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "An error occurred while updating the course: " + e.getMessage());
                e.printStackTrace();
            } catch (NullPointerException e) {
                JOptionPane.showMessageDialog(null, "Please select valid values for competency and/or professor.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Course ID and name must not be empty.");
        }
    }

    /**
     * Re-load data from database
    * @param_actionEvent
    */
    public void refreshCourseButtonOnAction(ActionEvent actionEvent) {
        loadProgramData();
        loadCourseData();
        loadProfessorData();
        loadCompetencyData();
    }

    /**
     * Inserts a new course in database
    * @param_actionEvent
    */
    public void newCourseButtonOnClick(ActionEvent actionEvent) {
        // Assume you have text fields for each course attribute
        String courseId = Optional.ofNullable(courseIdTextBox.getText()).orElse("").trim();
        Integer competencyId = Optional.ofNullable(courseCompetencyIdComboBox.getSelectionModel().getSelectedItem()).orElse(0);
        String professorId = Optional.ofNullable(courseProfessorIdComboBox.getSelectionModel().getSelectedItem()).orElse("").trim();
        String courseName = Optional.ofNullable(courseNameTextBox.getText()).orElse("").trim();
        String objectiveDescription = Optional.ofNullable(courseObjectiveTextBox.getText()).orElse("").trim();
        String evaluationInstrument = Optional.ofNullable(courseEvalTextBox.getText()).orElse("").trim();
        String compMetric = Optional.ofNullable(courseCompTextBox.getText()).orElse("").trim();

        if (courseId.isEmpty() || competencyId == null || competencyId == 0 || professorId.isEmpty() || courseName.isEmpty() || objectiveDescription.isEmpty() || evaluationInstrument.isEmpty() || compMetric.isEmpty()) {
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "All fields must be filled").showAndWait());
            return;
        }

        Task<Integer> databaseTask = new Task<>() {
            @Override protected Integer call() throws Exception {
                try (Connection conn = DatabaseConnection.connect();
                     PreparedStatement pstmt = conn.prepareStatement("INSERT INTO concentrationcourses (courseId, competencyId, professorId," +
                             "courseName, objectiveDescription, evaluationInstrument, compMetric) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, courseId);
                    pstmt.setInt(2, competencyId);
                    pstmt.setString(3, professorId);
                    pstmt.setString(4, courseName);
                    pstmt.setString(5, objectiveDescription);
                    pstmt.setString(6, evaluationInstrument);
                    pstmt.setString(7, compMetric);
                    return pstmt.executeUpdate();
                } catch (Exception e) {
                    throw new RuntimeException("Database error: " + e.getMessage());
                }
            }
        };

        databaseTask.setOnSucceeded(event -> {
            int affectedRows = databaseTask.getValue();
            if (affectedRows > 0) {
                Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Course Created Successfully").showAndWait());
                loadCourseData();
            } else {
                Platform.runLater(() -> new Alert(Alert.AlertType.WARNING, "Failed to create course").showAndWait());
            }
        });

        databaseTask.setOnFailed(event -> {
            Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Database error: " +
                    databaseTask.getException().getMessage()).showAndWait());
        });

        new Thread(databaseTask).start();
    }

    /**
     * Deletes course from database
    * @param_actionEvent
    */
    public void deleteCourseButtonOnAction() {
        Course selectedCourse = courseTable.getSelectionModel().getSelectedItem();
        if (selectedCourse == null) {
            showErrorDialog("Please select a Program to delete");
            return;
        }

        String courseId = selectedCourse.getCourseId();
        if (courseId == null || courseId.isEmpty()) {
            showErrorDialog("Invalid Course ID");
            return;
        }

        // Ensure database is connected before proceeding
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            showErrorDialog("Database connection error");
            return;
        }

        try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM concentrationcourses WHERE courseId = ?")) {
            pstmt.setString(1, courseId);
            int affectedRows = pstmt.executeUpdate();

            // If rows affected, programs has been deleted
            if (affectedRows > 0) {
                showInfoDialog("Program Deleted Successfully");
                loadCourseData(); // Refresh the table
            } else {
                showErrorDialog("Program Deletion Failed: No such program exists");
            }
        } catch (SQLException e) {
            showErrorDialog("Database error during deletion process: " + e.getMessage());
            // Log the exception for debugging purposes
            e.printStackTrace();
        } catch (Exception e) {
            // General exception handler
            showErrorDialog("Unexpected error occurred during deletion process: " + e.getMessage());
            // Log the exception for debugging purposes
            e.printStackTrace();
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfoDialog(String message) {
        JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Resets course text fields
    */
    public void resetCourseTextButtonOnClick(ActionEvent actionEvent) {
        automaticReset();
    }

    /**
     * Loads course data from database
    */
    private void loadCourseData() {
        ObservableList<Course> courseList = FXCollections.observableArrayList();
        courseCompetencyIdComboBox.getItems().clear();
        courseProfessorIdComboBox.getItems().clear();

        try (Connection conn = DatabaseConnection.connect()) {
            loadCoursesFromDatabase(conn, courseList);
            loadCompetencyIdsFromDatabase(conn);
            loadProfessorIdsFromDatabase(conn);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Database connection error: " + ex.getMessage());
        }

        courseTable.setItems(courseList);
        setCellValueFactories();

        automaticReset();
    }

    private void loadCoursesFromDatabase(Connection conn, ObservableList<Course> courseList) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM concentrationcourses")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Course course = createCourseFromResultSet(rs);
                courseList.add(course);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading courses: " + e.getMessage());
        }
    }

    private Course createCourseFromResultSet(ResultSet rs) throws SQLException {
        String courseId = rs.getString("courseId");
        int competencyId = rs.getInt("competencyId");
        String professorId = rs.getString("professorId");
        String courseName = rs.getString("courseName");
        String objectiveDesc = rs.getString("objectiveDescription");
        String evaluationInstrument = rs.getString("evaluationInstrument");
        String compMetric = rs.getString("compMetric");
        return new Course(courseId, competencyId, professorId, courseName, objectiveDesc, evaluationInstrument, compMetric);
    }

    private void loadCompetencyIdsFromDatabase(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT competencyID FROM coursecompetency")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int competencyId = rs.getInt("competencyId");
                courseCompetencyIdComboBox.getItems().add(competencyId);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading competency IDs: " + e.getMessage());
        }
    }

    private void loadProfessorIdsFromDatabase(Connection conn) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("SELECT DISTINCT professorID FROM professorstable")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String professorId = rs.getString("professorId");
                courseProfessorIdComboBox.getItems().add(professorId);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading professor IDs: " + e.getMessage());
        }
    }

    private void setCellValueFactories() {
        courseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseId"));
        courseCompetencyIdCol.setCellValueFactory(new PropertyValueFactory<>("competencyId"));
        courseProfessorIdCol.setCellValueFactory(new PropertyValueFactory<>("professorId"));
        courseNameCol.setCellValueFactory(new PropertyValueFactory<>("courseName"));
        objectiveDescCol.setCellValueFactory(new PropertyValueFactory<>("objectiveDescription"));
        evalInsCol.setCellValueFactory(new PropertyValueFactory<>("evaluationInstrument"));
        compMetricCol.setCellValueFactory(new PropertyValueFactory<>("compMetric"));
    }

    public void automaticReset() {
        courseIdTextBox.clear();
        courseCompetencyIdComboBox.getSelectionModel().clearSelection();
        courseProfessorIdComboBox.getSelectionModel().clearSelection();
        courseNameTextBox.clear();
        courseObjectiveTextBox.clear();
        courseEvalTextBox.clear();
        courseCompTextBox.clear();
        courseTable.getSelectionModel().clearSelection();
    }

    public void objectiveDescMoreOnAction(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/eduardososa/databaseprojecteleven/universalTextPage.fxml"));
            Parent root = loader.load();
            TextareaController controller = loader.getController();
            controller.setText(courseObjectiveTextBox.getText(), "Objective Description");

            controller.setSubmitCallback(text -> {
                courseObjectiveTextBox.setText(text);
            });

            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            newStage.showAndWait();
        } catch (IOException e) {
            System.err.println("Failed to load the universalTextPage.fxml: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void evalInstrumentMoreOnAction(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/eduardososa/databaseprojecteleven/universalTextPage.fxml"));
            Parent root = loader.load();
            TextareaController controller = loader.getController();
            controller.setText(courseEvalTextBox.getText(), "Evaluation Instrument");

            controller.setSubmitCallback(text -> {
                courseEvalTextBox.setText(text);
            });
    
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            newStage.showAndWait();
        } catch(IOException e) {
            System.err.println("Failed to load the universalTextPage.fxml: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    public void compMetricMoreOnAction(ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/eduardososa/databaseprojecteleven/universalTextPage.fxml"));
            Parent root = loader.load();
            TextareaController controller = loader.getController();
            controller.setText(courseCompTextBox.getText(), "Competency Metric");

            controller.setSubmitCallback(text -> {
                courseCompTextBox.setText(text);
            });
    
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            newStage.showAndWait();
        } catch(IOException e) {
            System.err.println("Failed to load the universalTextPage.fxml: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /*
     * 
     * ================================================================================================================================
     * 
     * This Section contains Professor Table Edits ----------------------------------------------------------------
     * 
     * ================================================================================================================================
     * 
     */

    @FXML
    private TextField profIdTextBox;
    @FXML
    private TextField profFirstNameTextBox;
    @FXML
    private TextField profLastNameTextBox;
    @FXML
    private TextField profEmailTextBox;
    @FXML
    private TextField profPhoneTextBox;

    // -- //

    @FXML
    private TableView <Professor> professorTable;
    @FXML
    private TableColumn<Professor, String> professorIdCol;
    @FXML
    private TableColumn<Professor, String> profFirstNameCol;
    @FXML
    private TableColumn<Professor, String> profLastNameCol;
    @FXML
    private TableColumn<Professor, String> profEmailCol;
    @FXML
    private TableColumn<Professor, String> profPhoneCol;

    /**
     * Fetches updated data and update in database
     * @param_actionEvent
     */
    public void updateProfButtonOnClick(ActionEvent actionEvent) {
        // Retrieve values from all text fields
        String professorId = profIdTextBox.getText().trim();
        String firstName = profFirstNameTextBox.getText().trim();
        String lastName = profLastNameTextBox.getText().trim();
        String email = profEmailTextBox.getText().trim();
        String phoneNum = profPhoneTextBox.getText().trim();

        // Validate the input
        ValidationResult validationProfResult = validateProfInput(professorId, firstName, lastName, email, phoneNum);
        if (!validationProfResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationProfResult.getErrorMessage());
            return;
        }

        // Prepare the SQL query
        String sql = "UPDATE professorstable SET firstName = ?, lastName = ?, email = ?, phoneNum = ? WHERE professorID = ?";

        try(
                // Connect to the database and prepare the SQL statement
                Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql);
        ) {
            // Set parameters for the prepared statement
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phoneNum);
            pstmt.setString(5, professorId);

            // Execute the update
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Professor Updated Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Professor Update Failed: " + e.getMessage());
            e.printStackTrace();
        }

        loadProfessorData();
        automaticResetProf();
    }

    /**
     * Re-load data from database
     * @param_actionEvent
     */
    public void refreshProfButtonOnAction(ActionEvent actionEvent) {
        loadProgramData();
        loadCourseData();
        loadProfessorData();
        loadCompetencyData();
    }

    /**
     * Inserts a new professor in database
     * @param_actionEvent
     */
    public void newProfButtonOnClick(ActionEvent actionEvent) {
        // Retrieve values from all text fields
        String professorId = profIdTextBox.getText().trim();
        String firstName = profFirstNameTextBox.getText().trim();
        String lastName = profLastNameTextBox.getText().trim();
        String email = profEmailTextBox.getText().trim();
        String phoneNum = profPhoneTextBox.getText().trim();

        // Validate the input
        ValidationResult validationResult = validateProfInput(professorId, firstName, lastName, email, phoneNum);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }

        // Prepare the SQL query to insert a new professor
        String sql = "INSERT INTO professorstable (professorID, firstName, lastName, email, phoneNum) VALUES (?, ?, ?, ?, ?)";

        try (   // Try-with-resources to clearly release resources after usage
                // Connect to the database
                Connection conn = DatabaseConnection.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)
        ) {

            // Set parameters for the prepared statement
            pstmt.setString(1, professorId);
            pstmt.setString(2, firstName);
            pstmt.setString(3, lastName);
            pstmt.setString(4, email);
            pstmt.setString(5, phoneNum);

            // Execute the query resulting in insertion of a new professor to the professorstable
            pstmt.executeUpdate();

            // Success message
            JOptionPane.showMessageDialog(null, "Professor Created Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Professor Creation Failed: " + e.getMessage());
        }

        loadProfessorData();
        automaticResetProf();
    }

    /**
     * Deletes professor from database
     * @param_actionEvent
     */
    synchronized public void deleteProfButtonOnAction(ActionEvent actionEvent) {
        if (professorTable == null) {
            JOptionPane.showMessageDialog(null, "No Professors List loaded");
            return;
        }

        Professor selectedProfessor = professorTable.getSelectionModel().getSelectedItem();

        if (selectedProfessor == null) {
            JOptionPane.showMessageDialog(null, "Please select a Professor to delete");
            return;
        }

        String professorId = selectedProfessor.getProfessorID();

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                JOptionPane.showMessageDialog(null, "Database Connection Failed");
                return;
            }

            try (PreparedStatement pstmt = conn.prepareStatement("DELETE FROM professorstable WHERE professorId = ?")) {
                pstmt.setString(1, professorId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Professor Deleted Successfully");
                    loadProfessorData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "Professor Deletion Failed: No Professors with ID " + professorId);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error during deletion process: " + e.getMessage());
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error in Database Connection: " + e.getMessage());
        }

        automaticResetProf();
    }

    /**
     * Resets professor text fields
     */
    public void resetProfTextButtonOnClick(ActionEvent actionEvent) {
        automaticResetProf();
    }

    /**
     * Loads professor data from database
     */
    private void loadProfessorData() {
        ObservableList<Professor> professorList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM professorstable")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String professorId = rs.getString("professorID");
                String firstName = rs.getString("firstName");
                String lastName = rs.getString("lastName");
                String email = rs.getString("email");
                String phoneNum = rs.getString("phoneNum");
                Professor professor = new Professor(professorId, firstName, lastName, email, phoneNum);
                professorList.add(professor);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during data loading: " + e.getMessage());
        }

        professorTable.setItems(professorList);
        professorIdCol.setCellValueFactory(new PropertyValueFactory<>("professorID"));
        profFirstNameCol.setCellValueFactory(new PropertyValueFactory<>("professorName"));
        profLastNameCol.setCellValueFactory(new PropertyValueFactory<>("professorLastName"));
        profEmailCol.setCellValueFactory(new PropertyValueFactory<>("professorEmail"));
        profPhoneCol.setCellValueFactory(new PropertyValueFactory<>("professorPhone"));
    }

    public void automaticResetProf() {
        profIdTextBox.clear();
        profFirstNameTextBox.clear();
        profLastNameTextBox.clear();
        profEmailTextBox.clear();
        profPhoneTextBox.clear();
        professorTable.getSelectionModel().clearSelection();
    }

    private ValidationResult validateProfInput(String professorId, String firstName, String lastName, String email, String phoneNum) {
        // Check if any field is empty
        if (professorId.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNum.isEmpty()) {
            return new ValidationResult(false, "All fields must be filled");
        }

        /*
        // Check if email is valid
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            return new ValidationResult(false, "Email is not valid");
        }
    
        // Check if phone number is valid
        if (!phoneNum.matches("\\d{10}")) {
            return new ValidationResult(false, "Phone number is not valid");
        }
        */
    
        // If all checks pass, return a valid result
        return new ValidationResult(true, "");
    }

    /*
     * 
     * ================================================================================================================================
     * 
     * This Section contains Competency Table Edits ----------------------------------------------------------------
     * 
     * ================================================================================================================================
     * 
     */

    @FXML
    private TextField competencyIDTextField;
    @FXML
    private TextField compDescTextField;

    // -- //

    @FXML
    private TableView <Competency> competencyTable;
    @FXML
    private TableColumn<Competency, Integer> competencyIdCol;
    @FXML
    private TableColumn<Competency, String> competencyDescCol;

    /**
     * Fetches updated data and update in database
     * @param_actionEvent
     */
    public void updateCompButtonOnClick(ActionEvent actionEvent){

        String competencyDesc = compDescTextField.getText().trim();
        String competencyId = competencyIDTextField.getText().trim();

        ValidationResult validationCompResult = validateSecondCompInput(competencyDesc, competencyId);

        if (!validationCompResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationCompResult.getErrorMessage());
            return;
        }

        try {
            Connection conn = null;
            PreparedStatement pstmt = null;

            try {
                // Connect to the database
                conn = DatabaseConnection.connect();

                String sql = "UPDATE coursecompetency SET competencyDescription = ? WHERE competencyID = ?";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, competencyDesc);
                pstmt.setInt(2, Integer.parseInt(competencyId));

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Competency Updated Successfully");


            } finally {

                if (pstmt != null) {
                    pstmt.close();
                }

                if (conn != null) {
                    conn.close();
                }
            }
        }catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Competency Update Failed. Please check your inputs and try again.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Competency ID should be a number!");

        }
        refreshCompButtonOnAction(actionEvent);
        automaticResetComp();
    }

    /**
     * Re-load data from database
     * @param_actionEvent
     */
    public void refreshCompButtonOnAction(ActionEvent actionEvent) {
        loadProgramData();
        loadCourseData();
        loadProfessorData();
        loadCompetencyData();
    }

    /**
     * Inserts a new competency in database
     * @param_actionEvent
     */
    public void newCompButtonOnClick(ActionEvent actionEvent) {
        String competencyDesc = compDescTextField.getText().trim();

        ValidationResult validationResult = validateCompInput(competencyDesc);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DatabaseConnection.connect();
            String sql = "INSERT INTO coursecompetency (competencyDescription) VALUES (?)";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, competencyDesc);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Competency Created Successfully");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Competency Creation Failed: " + e.getMessage());
        } finally {
            if (pstmt != null){
                try {
                    pstmt.close();
                } catch (SQLException e) { /* ignored */}
            }
            if (conn != null){
                try {
                    conn.close();
                } catch (SQLException e) { /* ignored */}
            }
        }

        loadCompetencyData();
        automaticResetComp();
    }

    /**
     * Deletes competency from database
     * @param_actionEvent
     */
    public void deleteCompButtonOnAction(ActionEvent actionEvent) {
        Competency selectedCompetency = competencyTable.getSelectionModel().getSelectedItem();
        if (selectedCompetency != null) {
            int competencyId = selectedCompetency.getCompetencyId();
            try (Connection conn = Objects.requireNonNull(DatabaseConnection.connect());
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM coursecompetency WHERE competencyId = ?")) {
                pstmt.setInt(1, competencyId);
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    showSuccessDialog("Competency Deleted Successfully");
                    loadCompetencyData(); // Refresh the table
                } else {
                    showErrorDialog("No Competency found with given ID.");
                }
            } catch (SQLException e) {
                showErrorDialog("Error during deletion process: " + e.getMessage());
                e.printStackTrace(); // Logs the stack trace for the exception
            }
        } else {
            showErrorDialog("Please select a Competency to delete");
        }
        automaticResetComp();
    }

    private void showSuccessDialog(String message) {
        // Code to show dialog with success message
        System.out.println(message); // Placeholder, replace with actual implementation
    }

    /**
     * Resets competency text fields
     */
    public void resetCompTextButtonOnClick(ActionEvent actionEvent) {
        automaticResetComp();
    }

    /**
     * Loads competency data from database
     */
    private void loadCompetencyData() {
        ObservableList<Competency> competencyList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM coursecompetency")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int competencyId = rs.getInt("competencyID");
                String competencyDesc = rs.getString("competencyDescription");
                Competency competency = new Competency(competencyId, competencyDesc);
                competencyList.add(competency);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during data loading: " + e.getMessage());
        }

        competencyTable.setItems(competencyList);
        competencyIdCol.setCellValueFactory(new PropertyValueFactory<>("competencyId"));
        competencyDescCol.setCellValueFactory(new PropertyValueFactory<>("competencyDescription"));
    }

    public void automaticResetComp() {
        competencyIDTextField.clear();
        compDescTextField.clear();
        competencyTable.getSelectionModel().clearSelection();
    }

    private ValidationResult validateCompInput(String competencyDesc) {
        // Check if any field is empty
        if (competencyDesc.isEmpty()) {
            return new ValidationResult(false, "All fields must be filled");
        }

        // If all checks pass, return a valid result
        return new ValidationResult(true, "");
    }

    private ValidationResult validateSecondCompInput(String competencyId, String competencyDesc) {
        // Check if any field is empty
        if (competencyDesc.isEmpty()) {
            return new ValidationResult(false, "All fields must be filled");
        }

        // If all checks pass, return a valid result
        return new ValidationResult(true, "");
    }

    public void compDescMoreOnAction (ActionEvent actionEvent) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/eduardososa/databaseprojecteleven/universalTextPage.fxml"));
            Parent root = loader.load();
            TextareaController controller = loader.getController();
            controller.setText(compDescTextField.getText(), "Competency Description");

            controller.setSubmitCallback(text -> {
                compDescTextField.setText(text);
            });
    
            newStage.setScene(new Scene(root));
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.initOwner(((Node)actionEvent.getSource()).getScene().getWindow());
            newStage.showAndWait();
        } catch(IOException e) {
            System.err.println("Failed to load the universalTextPage.fxml: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    /*
     * 
     * ================================================================================================================================
     * 
     * This Section contains Course/Program Selection Table Edits ----------------------------------------------------------------
     * 
     * ================================================================================================================================
     * 
     */

    @FXML
    private TextField selectionIDTextField;
    @FXML
    private TextField programSelectionTextField;
    @FXML
    private TextField courseSelectionTextField;
    @FXML
    private TextField programSelSearchBox;
    @FXML
    private TextField courseSelSearchBox;
    @FXML
    private Button newSelectionButton;
    @FXML
    private Button updateSelectionButton;

    // -- //

    @FXML
    private TableView <ProgSelection> programSelectionTable;
    @FXML
    private TableColumn<ProgSelection, String> programSelCol;
    @FXML
    private TableView <CourseSelection> courseSelectionTable;
    @FXML
    private TableColumn<CourseSelection, String> courseSelCol;
    @FXML
    private TableView<ProgramCourse> programcourseTable;
    @FXML
    private TableColumn<ProgramCourse, Integer> programcourseSelIdCol;
    @FXML
    private TableColumn<ProgramCourse, String> programcourseProgNameCol;
    @FXML
    private TableColumn<ProgramCourse, String> programcourseCourseIdCol;

    /**
     * Fetches updated data and update in database
     * @param_actionEvent
     */
    public void updateSelectionButtonOnClick(ActionEvent actionEvent) {
        int selectionId = Integer.parseInt(selectionIDTextField.getText().trim());
        String programSelection = programSelectionTextField.getText().trim();
        String courseSelection = courseSelectionTextField.getText().trim();

        ValidationResult validationResult = validateProgSelInput(programSelection, courseSelection);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "UPDATE programcourse SET programID = (SELECT programID FROM academicProgram WHERE programName = ?), courseID = (SELECT courseID FROM concentrationcourses WHERE courseName = ?) WHERE selectionID = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, programSelection);
                pstmt.setString(2, courseSelection);
                pstmt.setInt(3, selectionId);

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(null, "Program Selection Updated Successfully");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Program Selection Update Failed: " + e.getMessage());
        }

        loadProgramSelectionData();
        automaticResetProgramSelection();
    }

    /**
     * Re-load data from database
     * @param_actionEvent
     */
    public void refreshSelectionButtonOnAction(ActionEvent actionEvent) {
        loadProgramData();
        loadCourseData();
        loadProfessorData();
        loadCompetencyData();
        loadProgramSelectionData();
    }

    /**
     * Inserts a new program selection in database
     * @param_actionEvent
     */
    public void newSelectionButtonOnClick(ActionEvent actionEvent) {
        String programSelection = programSelectionTextField.getText().trim();
        String courseSelection = courseSelectionTextField.getText().trim();

        ValidationResult validationResult = validateProgSelInput(programSelection, courseSelection);
        if (!validationResult.isValid()) {
            JOptionPane.showMessageDialog(null, validationResult.getErrorMessage());
            return;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String sql = "INSERT INTO programcourse (programID, courseID) SELECT ap.programID, cc.courseID FROM academicprogram ap JOIN concentrationcourses cc ON ap.programName = ? AND cc.courseName = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, programSelection);
                pstmt.setString(2, courseSelection);

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    JOptionPane.showMessageDialog(null, "Program Selection Created Successfully");
                }            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Program Selection Creation Failed: " + e.getMessage());
        }

        loadProgramSelectionData();
        automaticResetProgramSelection();
    }

    /**
     * Deletes program selection from database
     * @param_actionEvent
     */
    public void deleteSelectionButtonOnAction(ActionEvent actionEvent) {
        ProgramCourse selectedProgramCourse = programcourseTable.getSelectionModel().getSelectedItem();
        if (selectedProgramCourse != null) {
            int selectionId = selectedProgramCourse.getSelectionId();
            try (Connection conn = DatabaseConnection.connect();
                 PreparedStatement pstmt = conn.prepareStatement("DELETE FROM programcourse WHERE selectionID = ?")) {
                pstmt.setInt(1, selectionId);
                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    JOptionPane.showMessageDialog(null, "Program Selection Deleted Successfully");
                    loadProgramSelectionData(); // Refresh the table
                } else {
                    JOptionPane.showMessageDialog(null, "Program Selection Deletion Failed");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error during deletion process: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a Program Selection to delete");
        }
        automaticResetProgramSelection();
    }

    /**
     * Resets program selection text fields
     */
    public void resetSelectionTextButtonOnClick(ActionEvent actionEvent) {
        automaticResetProgramSelection();
    }

    /**
     * Loads program selection data from database
     */
    private void loadProgramSelectionData() {
        ObservableList<ProgSelection> programSelectionList = FXCollections.observableArrayList();
        ObservableList<CourseSelection> courseSelectionList = FXCollections.observableArrayList();
        ObservableList<ProgramCourse> programCourseList = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT programName FROM academicprogram")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String programId = rs.getString("programName");
                ProgSelection program = new ProgSelection(programId);
                programSelectionList.add(program);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during data loading: " + e.getMessage());
        }
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT courseName FROM concentrationcourses")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String courseId = rs.getString("courseName");
                CourseSelection course = new CourseSelection(courseId);
                courseSelectionList.add(course);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during data loading: " + e.getMessage());
        }
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement ps = conn.prepareStatement("SELECT pc.selectionID, ap.programName, pc.courseID FROM programcourse pc JOIN academicprogram ap ON pc.programID = ap.programID")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Integer selectionId = rs.getInt("selectionID");
                String programName = rs.getString("programName");
                String courseId = rs.getString("courseID");
                ProgramCourse programCourse = new ProgramCourse(selectionId, programName, courseId);
                programCourseList.add(programCourse);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error during data loading: " + e.getMessage());
        }

        programSelectionTable.setItems(programSelectionList);
        programSelCol.setCellValueFactory(new PropertyValueFactory<>("progSelection"));

        courseSelectionTable.setItems(courseSelectionList);
        courseSelCol.setCellValueFactory(new PropertyValueFactory<>("courseSelection"));

        programcourseTable.setItems(programCourseList);
        programcourseSelIdCol.setCellValueFactory(new PropertyValueFactory<>("selectionId"));
        programcourseProgNameCol.setCellValueFactory(new PropertyValueFactory<>("programNameSelection"));
        programcourseCourseIdCol.setCellValueFactory(new PropertyValueFactory<>("courseIdSelection"));
    }

    public void automaticResetProgramSelection() {
        selectionIDTextField.clear();
        programSelectionTextField.clear();
        courseSelectionTextField.clear();
        programSelectionTable.getSelectionModel().clearSelection();
        courseSelectionTable.getSelectionModel().clearSelection();
        programcourseTable.getSelectionModel().clearSelection();
    }

    private ValidationResult validateProgSelInput(String programId, String courseId) {
        // Check if any field is empty
        if (programId.isEmpty() || courseId.isEmpty()) {
            return new ValidationResult(false, "All fields must be filled");
        }

        // If all checks pass, return a valid result
        return new ValidationResult(true, "");
    }
}