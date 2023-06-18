package com.example.kursproject.controllers.views;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.General;
import com.example.kursproject.classesTable.CategoriesExpense;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.Collections;

public class ViewCategoriesExpenseController {
    private static final String EXPENSE_CATEGORIES = "expense_categories";
    public TextField field_search;
    @FXML
    private TableView<CategoriesExpense> tableView;
    @FXML
    private TableColumn<CategoriesExpense, Integer> column0;
    @FXML
    private TableColumn<CategoriesExpense, String> column1;

    private void initData() {
        CommandsSQL.fillTable(tableView, EXPENSE_CATEGORIES, CategoriesExpense.class);
    }

    @FXML
    protected void initialize() {
        column0.setCellValueFactory(new PropertyValueFactory<>("id"));
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
        ControlStages.changeScene("scenes/tables-window.fxml");
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
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, EXPENSE_CATEGORIES, CategoriesExpense.class));
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
}
