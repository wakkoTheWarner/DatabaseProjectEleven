module org.eduardososa.databaseprojecteleven {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens org.eduardososa.databaseprojecteleven to javafx.fxml;
    opens org.eduardososa.databaseprojecteleven.controllers to javafx.fxml;
    opens org.eduardososa.databaseprojecteleven.backend to javafx.base;
    exports org.eduardososa.databaseprojecteleven;
    exports org.eduardososa.databaseprojecteleven.controllers to javafx.fxml;
}