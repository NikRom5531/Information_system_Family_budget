package com.example.kursproject.controllers.main;


import com.example.kursproject.*;
import com.example.kursproject.classesTable.SubcategoriesIncome;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubcategoriesIncomeController {
    private static final String INCOME_SUBCATEGORIES = "income_subcategories";
    private static final String INCOME_SUBCATEGORIES_V = "incomesubcategoriesview";
    private final List<String> fieldsTable = List.of("id", "name");
    private final List<String> stringList = List.of("Наименование");
    private final List<Boolean> booleanList = List.of(true);
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
        ControlStages.changeScene(URLs.URL_REFERENCE);
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
        if (tableView.getSelectionModel().getSelectedItem() == null) return null;
        return CommandsSQL.retrieveObjectById(INCOME_SUBCATEGORIES, tableView.getSelectionModel().getSelectedItem().getId(), SubcategoriesIncome.class);
    }

    private void searchOnField() {
        initData();
        tableView.setItems(General.filterTableData(tableView.getItems(), field_search.getText().trim(), tableView));
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

    private void addition() {
        SubcategoriesIncome item = General.createInputDialog("Введите данные", stringList, booleanList, new SubcategoriesIncome(0, ""));
        if (item != null) {
            if (!item.getName().isEmpty()) {
                if (CommandsSQL.insertDataIntoTable(INCOME_SUBCATEGORIES, new SubcategoriesIncome(CommandsSQL.getFreeID(INCOME_SUBCATEGORIES), item.getName()), fieldsTable)) {
                    searchOnField();
                    General.successfully("добавлено");
                }
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void edition() {
        if (getSelectItem() != null) {
            SubcategoriesIncome item = General.createInputDialog("Введите данные", stringList, booleanList, getSelectItem());
            if (item != null) {
                if (!item.getName().isEmpty()) {
                    if (CommandsSQL.updateDataInTable(INCOME_SUBCATEGORIES, new SubcategoriesIncome(item.getId(), item.getName()), fieldsTable, "id = " + item.getId())) {
                        searchOnField();
                        General.successfully("изменено");
                    }
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void deletion() {
        if (getSelectItem() != null) {
            SubcategoriesIncome item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" + "\nНаименование: " + item.getName())) {
                if (CommandsSQL.deleteDataFromTable(INCOME_SUBCATEGORIES, "id = " + item.getId() + " AND name = '" + item.getName() + "';")) {
                    searchOnField();
                    General.successfully("удалено");
                }
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

    @FXML
    protected void createReport() {
        // Создание Map для хранения соответствий исходных и желаемых наименований столбцов
        Map<String, String> columnNames = new HashMap<>();
        columnNames.put("id", "ID");
        String parametrs = "";
        for (int i = 1; i < fieldsTable.size(); i++) columnNames.put(fieldsTable.get(i), stringList.get(i - 1));
        if (!field_search.getText().trim().isEmpty()) parametrs += "'" + field_search.getText().trim() + "'";
        ReportGenerator.selectPath("Подкатегории дохода", tableView.getItems(), columnNames, fieldsTable, parametrs);
    }

    @FXML
    protected void clearAllFields() {
        field_search.clear();
        searchOnField();
    }
}
