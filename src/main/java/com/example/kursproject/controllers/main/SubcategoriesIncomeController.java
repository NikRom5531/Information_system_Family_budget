package com.example.kursproject.controllers.main;


import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.General;
import com.example.kursproject.URLs;
import com.example.kursproject.classesTable.SubcategoriesIncome;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Collections;

public class SubcategoriesIncomeController {
    private static final String INCOME_SUBCATEGORIES = "income_subcategories";
    private static final String INCOME_SUBCATEGORIES_V = "incomesubcategoriesview";
    @FXML
    public TextField field_search;
    @FXML
    public TableView<SubcategoriesIncome> tableView;
    @FXML
    public TableColumn<SubcategoriesIncome, Integer> column0;
    @FXML
    public TableColumn<SubcategoriesIncome, String> column1;
    private String NAME_TABLE;

    private void initData() {
        CommandsSQL.fillTable(tableView, NAME_TABLE, SubcategoriesIncome.class);
    }

    @FXML
    protected void initialize() {
        if (ControlStages.getSceneURL().equals(URLs.URL_INCOME_SUBCATEGORIES_T)) {
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            NAME_TABLE = INCOME_SUBCATEGORIES;
        } else NAME_TABLE = INCOME_SUBCATEGORIES_V;
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        initData();
    }

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_TABLES);
    }

    @FXML
    protected void onRefreshButtonClick() {
        initData();
    }

    @FXML
    protected void onInputMethodTextChanged() {
        searchOnField();
    }

    private SubcategoriesIncome getSelectItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void searchOnField() {
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, NAME_TABLE, SubcategoriesIncome.class));
    }

    @FXML
    protected void clickOpenAdditionWin() {
        interactionAddition();
    }

    @FXML
    protected void clickOpenEditWin() {
        interactionEdition();
    }

    @FXML
    protected void clickOpenDeleteWin() {
        interactionDeletion();
    }

    private void interactionAddition() {
        SubcategoriesIncome result = General.createInputDialog(
                "Введите данные",
                Collections.singletonList("Наименование:"),
                Collections.singletonList(true),
                new SubcategoriesIncome(-1, ""));
        if (result != null) {
            if (!result.getName().isEmpty()) {
                CommandsSQL.insertDataIntoTable(INCOME_SUBCATEGORIES, new SubcategoriesIncome(CommandsSQL.getFreeID(INCOME_SUBCATEGORIES), result.getName()));
                initData();
                General.successfully("добавлено");
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void interactionEdition() {
        if (getSelectItem() != null) {
            SubcategoriesIncome result = General.createInputDialog(
                    "Введите данные",
                    Collections.singletonList("Наименование:"),
                    Collections.singletonList(true),
                    getSelectItem());
            if (result != null) {
                if (!result.getName().isEmpty()) {
                    CommandsSQL.updateDataInTable(INCOME_SUBCATEGORIES, result, "id = " + result.getId());
                    initData();
                    General.successfully("изменено");
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void interactionDeletion() {
        if (getSelectItem() != null) {
            SubcategoriesIncome item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" + "\nНаименование: " + item.getName())) {
                CommandsSQL.deleteDataFromTable(INCOME_SUBCATEGORIES, "id = " + item.getId() + " AND name = '" + item.getName() + "';");
                initData();
                General.successfully("удалено");
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }

    @FXML
    protected void gotoTable() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SUBCATEGORIES_T);
    }

    @FXML
    protected void gotoView() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SUBCATEGORIES_V);
    }
}
