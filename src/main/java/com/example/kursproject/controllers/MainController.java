package com.example.kursproject.controllers;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.URLs;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class MainController {
    @FXML
    private Label main_menu_amount_of_funds;
    @FXML
    protected void initialize() {
        updateSum();
    }
    private void updateSum() {
        main_menu_amount_of_funds.setText(String.format("%.2f ₽", CommandsSQL.calculateTotalBalance()));
    }

    public void onOpenWindowTables() throws IOException {
        ControlStages.changeScene(URLs.URL_TABLES);
    }

    public void onOpenReference() throws IOException {
        ControlStages.changeScene(URLs.URL_REFERENCE);
    }
}
