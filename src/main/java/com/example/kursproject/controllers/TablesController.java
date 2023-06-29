package com.example.kursproject.controllers;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.URLs;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;

public class TablesController {
    @FXML
    private Label main_menu_amount_of_funds;

    @FXML
    protected void onGoToIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_V);
    }

    @FXML
    protected void onGoToExpensesWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_EXPENSES_V);
    }

    @FXML
    protected void onOpenStatistics() throws IOException {
        ControlStages.changeScene(URLs.URL_STATISTICS);
    }

    @FXML
    protected void initialize() {
        updateSum();
    }

    private void updateSum() {
        main_menu_amount_of_funds.setText(String.format("%.2f ₽", CommandsSQL.calculateTotalBalance()));
    }

    @FXML
    protected void onBackMain() throws IOException {
        ControlStages.changeScene(URLs.URL_MAIN);
    }
}