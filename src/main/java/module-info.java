module com.example.kursproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;
    requires java.desktop;

    opens com.example.kursproject.controllers.main to javafx.fxml;
    opens com.example.kursproject.controllers.second to javafx.fxml;
    opens com.example.kursproject.controllers to javafx.fxml;
    opens com.example.kursproject.classesTable to javafx.fxml;
    // Add this line to open the package

    exports com.example.kursproject;
    exports com.example.kursproject.controllers.main;
    exports com.example.kursproject.controllers.second;
    exports com.example.kursproject.controllers;
    exports com.example.kursproject.classesTable;
}
