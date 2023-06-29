package com.example.kursproject.controllers;

import com.example.kursproject.ControlStages;
import com.example.kursproject.URLs;
import javafx.fxml.FXML;

import java.io.IOException;

public class ReferenceController {
    @FXML
    protected void onGoToFamilyMembersWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_FAMILY_MEMBER_V);
    }

    @FXML
    protected void onGoToSourcesIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SOURCES_V);
    }

    @FXML
    protected void onGoToCategoriesIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_CATEGORIES_V);
    }

    @FXML
    protected void onGoToSubcategoriesIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SUBCATEGORIES_V);
    }

    @FXML
    protected void onGoToCategoriesExpenditureWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_EXPENSE_CATEGORIES_V);
    }
    @FXML
    protected void onBackMain() throws IOException {
        ControlStages.changeScene(URLs.URL_MAIN);
    }
}
