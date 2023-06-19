package com.example.kursproject.controllers.main;

import com.example.kursproject.URLs;
import com.example.kursproject.classesTable.CategoriesExpense;
import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.General;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Collections;

public class CategoriesExpenseController {
    private static final String EXPENSE_CATEGORIES = "expense_categories";
    private static final String EXPENSE_CATEGORIES_V = "expensecategoriesview";
    public TextField field_search;
    @FXML
    private TableView<CategoriesExpense> tableView;
    @FXML
    private TableColumn<CategoriesExpense, Integer> column0;
    @FXML
    private TableColumn<CategoriesExpense, String> column1;
    private String NAME_TABLE;

    private void initData() {
        CommandsSQL.fillTable(tableView, NAME_TABLE, CategoriesExpense.class);
    }

    @FXML
    protected void initialize() {
        if (ControlStages.getSceneURL().equals(URLs.URL_EXPENSE_CATEGORIES_T)) {
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            NAME_TABLE = EXPENSE_CATEGORIES;
        } else NAME_TABLE = EXPENSE_CATEGORIES_V;
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        initData();
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

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    private CategoriesExpense getSelectItem() {
        return tableView.getSelectionModel().getSelectedItem();
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

    private void searchOnField() {
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, NAME_TABLE, CategoriesExpense.class));
    }
    private void interactionAddition() {
        CategoriesExpense item = General.createInputDialog(
                "Введите данные",
                Collections.singletonList("Наименование:"),
                Collections.singletonList(true),
                new CategoriesExpense(-1, ""));
        if (item != null) {
            if (!item.getName().isEmpty()) {
                CommandsSQL.insertDataIntoTable(EXPENSE_CATEGORIES, new CategoriesExpense(CommandsSQL.getFreeID(EXPENSE_CATEGORIES), item.getName()));
                initData();
                General.successfully("добавлено");
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void interactionEdition() {
        if (getSelectItem() != null) {
            CategoriesExpense item = General.createInputDialog(
                    "Введите данные",
                    Collections.singletonList("Наименование:"),
                    Collections.singletonList(true),
                    getSelectItem());
            if (item != null) {
                if (!item.getName().isEmpty()) {
                    CommandsSQL.updateDataInTable(EXPENSE_CATEGORIES, item, "id = " + item.getId());
                    initData();
                    General.successfully("изменено");
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void interactionDeletion() {
        if (getSelectItem() != null) {
            CategoriesExpense item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" + "\nНаименование: " + item.getName())) {
                CommandsSQL.deleteDataFromTable(EXPENSE_CATEGORIES, "id = " + item.getId() + " AND name = '" + item.getName() + "';");
                initData();
                General.successfully("удалено");
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }
    @FXML
    protected void gotoTable() throws IOException {
        ControlStages.changeScene(URLs.URL_EXPENSE_CATEGORIES_T);
    }

    @FXML
    protected void gotoView() throws IOException {
        ControlStages.changeScene(URLs.URL_EXPENSE_CATEGORIES_V);
    }
}
