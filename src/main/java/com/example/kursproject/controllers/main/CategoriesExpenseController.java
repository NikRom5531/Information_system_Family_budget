package com.example.kursproject.controllers.main;

import com.example.kursproject.*;
import com.example.kursproject.classesTable.CategoriesExpense;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesExpenseController {
    private static final String EXPENSE_CATEGORIES = "expense_categories";
    private static final String EXPENSE_CATEGORIES_V = "expensecategoriesview";
    private final List<String> fieldsTable = List.of("id", "name");
    private final List<String> stringList = List.of("Наименование");
    private final List<Boolean> booleanList = List.of(true);
    @FXML
    public TextField field_search;
    @FXML
    private TableView<CategoriesExpense> tableView;
    @FXML
    private TableColumn<CategoriesExpense, ?> column0;
    @FXML
    private TableColumn<CategoriesExpense, ?> column1;
    private String NAME_TABLE;

    @FXML
    protected void initialize() {
        if (ControlStages.getSceneURL().equals(URLs.URL_EXPENSE_CATEGORIES_T)) {
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            NAME_TABLE = EXPENSE_CATEGORIES;
        } else NAME_TABLE = EXPENSE_CATEGORIES_V;
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        initData();
    }

    private void initData() {
        CommandsSQL.fillTable(tableView, NAME_TABLE, CategoriesExpense.class);
    }

    @FXML
    protected void clickOpenAdditionWin() {
        addition();
    }

    @FXML
    protected void clickOpenEditWin() {
        edition();
    }

    @FXML
    protected void clickOpenDeleteWin() {
        deletion();
    }

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    private CategoriesExpense getSelectItem() {
        if (tableView.getSelectionModel().getSelectedItem() == null) return null;
        return CommandsSQL.retrieveObjectById(EXPENSE_CATEGORIES, tableView.getSelectionModel().getSelectedItem().getId(), CategoriesExpense.class);
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
        initData();
        tableView.setItems(General.filterTableData(tableView.getItems(), field_search.getText().trim(), tableView));
    }

    private void addition() {
        CategoriesExpense item = General.createInputDialog("Введите данные", stringList, booleanList, new CategoriesExpense(0, ""));
        if (item != null) {
            if (!item.getName().isEmpty()) {
                if (CommandsSQL.insertDataIntoTable(EXPENSE_CATEGORIES, new CategoriesExpense(CommandsSQL.getFreeID(EXPENSE_CATEGORIES), item.getName()), fieldsTable)) {
                    searchOnField();
                    General.successfully("добавлено");
                }
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void edition() {
        if (getSelectItem() != null) {
            CategoriesExpense item = General.createInputDialog("Введите данные", stringList, booleanList, getSelectItem());
            if (item != null) {
                if (!item.getName().isEmpty()) {
                    if (CommandsSQL.updateDataInTable(EXPENSE_CATEGORIES, item, fieldsTable, "id = " + item.getId())) {
                        searchOnField();
                        General.successfully("изменено");
                    }
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void deletion() {
        if (getSelectItem() != null) {
            CategoriesExpense item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" + "\n" + stringList.get(0) + ": " + item.getName())) {
                if (CommandsSQL.deleteDataFromTable(EXPENSE_CATEGORIES, "id = " + item.getId() + " AND name = '" + item.getName() + "';")) {
                    searchOnField();
                    General.successfully("удалено");
                }
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

    @FXML
    protected void createReport() {
        // Создание Map для хранения соответствий исходных и желаемых наименований столбцов
        Map<String, String> columnNames = new HashMap<>();
        columnNames.put("id", "ID");
        String parametrs = "";
        for (int i = 1; i < fieldsTable.size(); i++) columnNames.put(fieldsTable.get(i), stringList.get(i - 1));
        if (!field_search.getText().trim().isEmpty()) parametrs += "'" + field_search.getText().trim() + "'";
        ReportGenerator.selectPath("Категории расхода" ,tableView.getItems(), columnNames, fieldsTable, parametrs);
    }

    @FXML
    protected void clearAllFields() {
        field_search.clear();
        searchOnField();
    }
}
