module com.example.kursproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires java.naming;
    requires java.sql;
    requires static lombok;
    opens com.example.kursproject to javafx.fxml;
    exports com.example.kursproject;
    exports com.example.kursproject.controllers.main;
    opens com.example.kursproject.controllers.main to javafx.fxml;
    exports com.example.kursproject.controllers.second;
    opens com.example.kursproject.controllers.second to javafx.fxml;
    exports com.example.kursproject.controllers;
    opens com.example.kursproject.controllers to javafx.fxml;
    exports com.example.kursproject.classesTable;
    opens com.example.kursproject.classesTable to javafx.fxml;
}