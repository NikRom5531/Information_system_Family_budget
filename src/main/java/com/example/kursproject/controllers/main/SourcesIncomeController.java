package com.example.kursproject.controllers.main;

import com.example.kursproject.*;
import com.example.kursproject.classesTable.SourcesIncome;
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

public class SourcesIncomeController {
    private static final String INCOME_SOURCES = "income_sources";
    private static final String INCOME_SOURCES_V = "incomesourcesview";
    private final List<String> fieldsTable = List.of("id", "name", "income_category_id", "description");
    private final List<String> fieldsView = List.of("id", "name", "category_name", "subcategory_name", "description"); //
    private final List<String> stringList_T = List.of("Наименование", "ID категории дохода", "Описание");
    private final List<String> stringList_V = List.of("Наименование", "Наименование категории дохода", "Наименование подкатегории дохода", "Описание"); //
    private final List<Boolean> booleanList = List.of(true, true, true);

    @FXML
    private TextField field_search;
    @FXML
    private TableView<SourcesIncome> tableView;
    @FXML
    private TableColumn<SourcesIncome, ?> column0;// int id;
    @FXML
    private TableColumn<SourcesIncome, ?> column1;//  String name;
    @FXML
    private TableColumn<SourcesIncome, ?> column2;//  int income_category_id;
    @FXML
    private TableColumn<SourcesIncome, ?> column2_1;// String category_name;
    @FXML
    private TableColumn<SourcesIncome, ?> column2_2;//  String subcategory_name;
    @FXML
    private TableColumn<SourcesIncome, ?> column3;//  String description;
    @FXML
    private ComboBox<String> choice_subcategory;
    @FXML
    private ComboBox<String> choice_category;
    private String NAME_TABLE;
    private General.Mode mode;

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_REFERENCE);
    }

    @FXML
    protected void initialize() {
        if (ControlStages.getSceneURL().equals(URLs.URL_INCOME_SOURCES_T)) {
            NAME_TABLE = INCOME_SOURCES;
            mode = General.Mode.TABLE;
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            column2.setCellValueFactory(new PropertyValueFactory<>("income_category_id"));
        } else {
            NAME_TABLE = INCOME_SOURCES_V;
            mode = General.Mode.VIEW;
            column2_1.setCellValueFactory(new PropertyValueFactory<>("category_name"));
            column2_2.setCellValueFactory(new PropertyValueFactory<>("subcategory_name"));
        }
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
        initComboBox();
        initData();
    }

    private void initComboBox() {
        switch (mode) {
            case TABLE -> {
                ObservableList<Integer> categoriesInt = CommandsSQL.getColumnValues(INCOME_SOURCES, "income_category_id", Integer.class, true);
                ObservableList<String> categories = FXCollections.observableArrayList();
                for (Integer integer : categoriesInt) categories.add(String.valueOf(integer));
                categories.add(0, "Выберите ID категории дохода...");
                choice_category.setItems(categories);
            }
            case VIEW -> {
                ObservableList<String> categories = CommandsSQL.getColumnValues(INCOME_SOURCES_V, "category_name", String.class, true);
                categories.add(0, "Выберите категорию дохода...");
                choice_category.setItems(categories);
                ObservableList<String> subcategories = CommandsSQL.getColumnValues(INCOME_SOURCES_V, "subcategory_name", String.class, true);
                subcategories.add(0, "Выберите подкатегорию дохода...");
                choice_subcategory.setItems(subcategories);
            }
        }
    }

    private void initData() {
        CommandsSQL.fillTable(tableView, NAME_TABLE, SourcesIncome.class);
    }

    @FXML
    protected void onRefreshButtonClick() {
        initData();
    }

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    private SourcesIncome getSelectItem() { //tableView.getSelectionModel().getSelectedItem();
        if (tableView.getSelectionModel().getSelectedItem() == null) return null;
        return CommandsSQL.retrieveObjectById(INCOME_SOURCES, tableView.getSelectionModel().getSelectedItem().getId(), SourcesIncome.class);
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
        SourcesIncome item = General.createInputDialog("Введите данные", stringList_T, booleanList,
                new SourcesIncome(0, "", 0, "")); //new SourcesIncome(-1, "", -1, "")
        if (item != null) {
            if (!item.getName().trim().isEmpty()
                    && CommandsSQL.checkID(item.getIncome_category_id(), "income_categories")) {
                if (CommandsSQL.insertDataIntoTable(INCOME_SOURCES, new SourcesIncome(CommandsSQL.getFreeID(INCOME_SOURCES),
                        item.getName(), item.getIncome_category_id(), item.getDescription()), fieldsTable)) {
                    searchOnField();
                    General.successfully("добавлено");
                }
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void edition() {
        if (getSelectItem() != null) {
            SourcesIncome item = General.createInputDialog("Введите данные", stringList_T, booleanList, getSelectItem());
            if (item != null) {
                if (!item.getName().trim().isEmpty()
                        && CommandsSQL.checkID(item.getIncome_category_id(), "income_categories")
                        && !item.getDescription().trim().isEmpty()) {
                    if (CommandsSQL.updateDataInTable(INCOME_SOURCES, item, fieldsTable, "id = " + item.getId())) {
                        searchOnField();
                        General.successfully("изменено");
                    }
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void deletion() {
        if (getSelectItem() != null) {
            SourcesIncome item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" +
                    "\n" + stringList_T.get(0) + ": " + item.getName() +
                    "\n" + stringList_T.get(1) + ": " + item.getIncome_category_id() +
                    "\n" + stringList_T.get(2) + ": " + item.getDescription())) {
                if (CommandsSQL.deleteDataFromTable(INCOME_SOURCES, "id = " + item.getId() + ";")) {
                    searchOnField();
                    General.successfully("удалено");
                }
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }

    @FXML
    protected void gotoTable() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SOURCES_T);
    }

    @FXML
    protected void gotoView() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_SOURCES_V);
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
                for (int i = 1; i < fieldsTable.size(); i++) columnNames.put(fieldsTable.get(i), stringList_T.get(i - 1));
                if (choice_category.getSelectionModel().getSelectedIndex() > 0) parametrs += "ID категории дохода = '" + choice_category.getSelectionModel().getSelectedItem() + "'";
                if (choice_category.getSelectionModel().getSelectedIndex() > 0 && !field_search.getText().trim().isEmpty()) parametrs += ", ";
                if (!field_search.getText().trim().isEmpty()) parametrs += "'" + field_search.getText().trim() + "'";
                onlyColumns = fieldsTable;
            }
            case VIEW -> {
                for (int i = 1; i < fieldsView.size(); i++) columnNames.put(fieldsView.get(i), stringList_V.get(i - 1));
                if (choice_category.getSelectionModel().getSelectedIndex() > 0) parametrs += "категория дохода = '" + choice_category.getSelectionModel().getSelectedItem() + "'";
                if (choice_category.getSelectionModel().getSelectedIndex() > 0 && choice_subcategory.getSelectionModel().getSelectedIndex() > 0) parametrs += ", ";
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0) parametrs += "подкатегория дохода = '" + choice_subcategory.getSelectionModel().getSelectedItem() + "'";
                if ((choice_subcategory.getSelectionModel().getSelectedIndex() > 0 || choice_category.getSelectionModel().getSelectedIndex() > 0) && !field_search.getText().trim().isEmpty()) parametrs += ", ";
                if (!field_search.getText().trim().isEmpty()) parametrs += "'" + field_search.getText().trim() + "'";
                onlyColumns = fieldsView;
            }
        }
        ReportGenerator.selectPath("Источники дохода", tableView.getItems(), columnNames, onlyColumns, parametrs);
    }

    @FXML
    protected void onInputMethodTextChanged() {
        searchOnField();
    }

    private void searchOnField() {
        switch (mode) {
            case TABLE -> {
                if (choice_category.getSelectionModel().getSelectedIndex() > 0) tableView.setItems(CommandsSQL.filterData(INCOME_SOURCES, "income_category_id = " + choice_category.getSelectionModel().getSelectedItem() + " ORDER BY id", SourcesIncome.class));
                else initData();
            }
            case VIEW -> {
                if (choice_category.getSelectionModel().getSelectedIndex() > 0 || choice_subcategory.getSelectionModel().getSelectedIndex() > 0) {
                    StringBuilder sqlQuery = new StringBuilder();
                    if (choice_category.getSelectionModel().getSelectedIndex() > 0) sqlQuery.append("category_name = '").append(choice_category.getSelectionModel().getSelectedItem()).append("'");
                    if (choice_category.getSelectionModel().getSelectedIndex() > 0 && choice_subcategory.getSelectionModel().getSelectedIndex() > 0) sqlQuery.append(" AND ");
                    if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0) sqlQuery.append("subcategory_name = '").append(choice_subcategory.getSelectionModel().getSelectedItem()).append("'");
                    tableView.setItems(CommandsSQL.filterData(INCOME_SOURCES_V, sqlQuery + " ORDER BY id", SourcesIncome.class));
                } else initData();
            }
        }
        tableView.setItems(General.filterTableData(tableView.getItems(), field_search.getText().trim(), tableView));
    }

    @FXML
    protected void choiceCategory() {
        searchOnField();
    }

    @FXML
    protected void choiceSubcategory() {
        searchOnField();
    }

    @FXML
    protected void clearAllFields() {
        field_search.clear();
        choice_category.getSelectionModel().select(0);
        if (mode == General.Mode.VIEW) choice_subcategory.getSelectionModel().select(0);
        searchOnField();
    }
}