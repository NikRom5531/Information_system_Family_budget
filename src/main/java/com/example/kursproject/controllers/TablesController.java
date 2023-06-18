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
        ControlStages.changeScene(URLs.URL_INCOME_T);
    }

    @FXML
    protected void onGoToExpensesWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_EXPENSES_T);
    }

    @FXML
    protected void onGoToFamilyMembersWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_FAMILY_MEMBER_T);
    }

    @FXML
    protected void onGoToSourcesIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SOURCE_T);
    }

    @FXML
    protected void onGoToCategoriesIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_CATEGORIES_T);
    }

    @FXML
    protected void onGoToSubcategoriesIncomeWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SUBCATEGORIES_T);
    }

    @FXML
    protected void onGoToCategoriesExpenditureWindowButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_EXPENSE_CATEGORIES_T);
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
/*
Название                | Name                      | Controller                        | table
------------------------------------------------------------------------------------------------------------------------
доходы                  | income                    | IncomeController                  | incomes
расходы                 | expenses                  | ExpensesController                | expenses
Члены семьи             | family members            | FamilyMembersController           | family_members
источники доходов       | sources of income         | SourcesIncomeController           | income_sources
категории доходов       | income categories         | CategoriesIncomeController        | income_categories
подкатегории доходов    | income subcategories      | SubcategoriesIncomeController     | income_subcategories
категории расходов      | expenditure categories    | CategoriesExpenditureController   | expense_categories
------------------------------------------------------------------------------------------------------------------------

onGoToExpensesWindowButtonClick                 // "Открыть таблицу расходов"
onGoToFamilyMembersWindowButtonClick            // "Открыть таблицу членов семьи"
onGoToSourcesIncomeWindowButtonClick            // "Открыть таблицу источников дохода"
onGoToCategoriesIncomeWindowButtonClick         // "Открыть таблицу категорий доходов"
onGoToSubcategoriesIncomeWindowButtonClick      // "Открыть таблицу подкатегорий дохода"
onGoToCategoriesExpenditureWindowButtonClick    // "Открыть таблицу категорий расходов"
*/