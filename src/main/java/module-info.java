module com.app.project1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.java;

    opens com.app.project1 to javafx.fxml;
    exports com.app.project1;
    exports com.app.project1.database;
    opens com.app.project1.database to javafx.fxml;
}