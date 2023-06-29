package com.example.kursproject.controllers.main;

import com.example.kursproject.*;
import com.example.kursproject.classesTable.CategoriesIncome;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesIncomeController {
    private static final String INCOME_CATEGORIES = "income_categories";
    private static final String INCOME_CATEGORIES_V = "incomecategoriesview";
    private final List<String> fieldsTable = List.of("id", "name", "subcategory_id");
    private final List<String> fieldsView = List.of("id", "name", "subcategory_name");
    private final List<String> stringList_T = List.of("Наименование", "ID покатегории дохода");
    private final List<String> stringList_V = List.of("Наименование", "Наименование подкатегории дохода");
    private final List<Boolean> booleanList = List.of(true, true);

    @FXML
    private TextField field_search;
    @FXML
    private TableView<CategoriesIncome> tableView;
    @FXML
    private TableColumn<CategoriesIncome, ?> column0;//    private int id;
    @FXML
    private TableColumn<CategoriesIncome, ?> column1;//    private String name;
    @FXML
    private TableColumn<CategoriesIncome, ?> column2;//    private int subcategory_id; private String subcategory_name
    @FXML
    private ComboBox<String> choice_subcategory;
    private String NAME_TABLE;

    private General.Mode mode;


    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_REFERENCE);
    }

    @FXML
    protected void initialize() {
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (ControlStages.getSceneURL().equals(URLs.URL_INCOME_CATEGORIES_T)) {
            NAME_TABLE = INCOME_CATEGORIES;
            mode = General.Mode.TABLE;
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            column2.setCellValueFactory(new PropertyValueFactory<>("subcategory_id"));
        } else {
            NAME_TABLE = INCOME_CATEGORIES_V;
            mode = General.Mode.VIEW;
            column2.setCellValueFactory(new PropertyValueFactory<>("subcategory_name"));
        }
        initComboBox();
        initData();
    }

    @FXML
    protected void onRefreshButtonClick() {
        initData();
    }

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    @FXML
    protected void onInputMethodTextChanged() {
        searchOnField();
    }

    private CategoriesIncome getSelectItem() {
        if (tableView.getSelectionModel().getSelectedItem() == null) return null;
        return CommandsSQL.retrieveObjectById(INCOME_CATEGORIES, tableView.getSelectionModel().getSelectedItem().getId(), CategoriesIncome.class);
    }

    private void initData() {
        CommandsSQL.fillTable(tableView, NAME_TABLE, CategoriesIncome.class);
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
        CategoriesIncome item = General.createInputDialog("Введите данные", stringList_T, booleanList,
                new CategoriesIncome(0, "", 0));
        if (item != null) {
            if (!item.getName().trim().isEmpty() && CommandsSQL.checkID(item.getSubcategory_id(), "income_subcategories")) {
                if (CommandsSQL.insertDataIntoTable(INCOME_CATEGORIES,
                        new CategoriesIncome(CommandsSQL.getFreeID(INCOME_CATEGORIES),
                                item.getName(), item.getSubcategory_id()), fieldsTable)) {
                    searchOnField();
                    General.successfully("добавлено");
                }
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void edition() {
        if (getSelectItem() != null) {
            CategoriesIncome item = General.createInputDialog("Введите данные", stringList_T, booleanList, getSelectItem());
            if (item != null) {
                if (!item.getName().trim().isEmpty() && CommandsSQL.checkID(item.getSubcategory_id(), "income_subcategories")) {
                    if (CommandsSQL.updateDataInTable(INCOME_CATEGORIES, item, fieldsTable, "id = " + item.getId())) {
                        searchOnField();
                        General.successfully("изменено");
                    }
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void deletion() {
        if (getSelectItem() != null) {
            CategoriesIncome item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?"
                    + "\n" + stringList_T.get(0) + ": " + item.getName()
                    + "\n" + stringList_T.get(1) + ": " + item.getSubcategory_id())) {
                if (CommandsSQL.deleteDataFromTable(INCOME_CATEGORIES, "id = " + item.getId() + ";")) {
                    searchOnField();
                    General.successfully("удалено");
                }
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }


    private void initComboBox() {
        switch (mode) {
            case TABLE -> {
                ObservableList<Integer> categoriesInt = CommandsSQL.getColumnValues(INCOME_CATEGORIES, "subcategory_id", Integer.class, true);
                ObservableList<String> categories = FXCollections.observableArrayList();
                for (Integer integer : categoriesInt) categories.add(String.valueOf(integer));
                categories.add(0, "Выберите ID подкатегории дохода...");
                choice_subcategory.setItems(categories);
            }
            case VIEW -> {
                ObservableList<String> subcategories = CommandsSQL.getColumnValues(INCOME_CATEGORIES_V, "subcategory_name", String.class, true);
                subcategories.add(0, "Выберите подкатегорию дохода...");
                choice_subcategory.setItems(subcategories);
            }
        }
    }

    @FXML
    protected void createReport() {
        // Создание Map для хранения соответствий исходных и желаемых наименований столбцов
        Map<String, String> columnNames = new HashMap<>();
        List<String> onlyColumns = null;
        columnNames.put("id", "ID");
        String parametrs = "";
        switch (mode) {
            case TABLE -> {
                for (int i = 1; i < fieldsTable.size(); i++)
                    columnNames.put(fieldsTable.get(i), stringList_T.get(i - 1));
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0)
                    parametrs += "ID подкатегория дохода = '" + choice_subcategory.getSelectionModel().getSelectedItem() + "'";
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0 && !field_search.getText().trim().isEmpty())
                    parametrs += ", ";
                if (!field_search.getText().trim().isEmpty()) parametrs += "'" + field_search.getText().trim() + "'";
                onlyColumns = fieldsTable;
            }
            case VIEW -> {
                for (int i = 1; i < fieldsView.size(); i++) columnNames.put(fieldsView.get(i), stringList_V.get(i - 1));
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0)
                    parametrs += "подкатегория дохода = '" + choice_subcategory.getSelectionModel().getSelectedItem() + "'";
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0 && !field_search.getText().trim().isEmpty())
                    parametrs += ", ";
                if (!field_search.getText().trim().isEmpty()) parametrs += "'" + field_search.getText().trim() + "'";
                onlyColumns = fieldsView;
            }
        }
        ReportGenerator.selectPath("Категории дохода", tableView.getItems(), columnNames, onlyColumns, parametrs);
    }

    private void searchOnField() {
        switch (mode) {
            case TABLE -> {
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0)
                    tableView.setItems(CommandsSQL.filterData(INCOME_CATEGORIES, "subcategory_id = " + choice_subcategory.getSelectionModel().getSelectedItem() + " ORDER BY id", CategoriesIncome.class));
                else initData();
            }
            case VIEW -> {
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0)
                    tableView.setItems(CommandsSQL.filterData(INCOME_CATEGORIES_V, "subcategory_name = '" + choice_subcategory.getSelectionModel().getSelectedItem() + "'" + " ORDER BY id", CategoriesIncome.class));
                else initData();
            }
        }
        tableView.setItems(General.filterTableData(tableView.getItems(), field_search.getText().trim(), tableView));
    }

    @FXML
    protected void choiceSubcategory() {
        searchOnField();
    }

    @FXML
    protected void clearAllFields() {
        field_search.clear();
        choice_subcategory.getSelectionModel().select(0);
        searchOnField();
    }

    @FXML
    protected void gotoTable() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_CATEGORIES_T);
    }

    @FXML
    protected void gotoView() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_CATEGORIES_V);
    }
}
