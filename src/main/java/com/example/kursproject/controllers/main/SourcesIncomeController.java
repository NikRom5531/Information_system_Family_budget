package com.example.kursproject.controllers.main;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.General;
import com.example.kursproject.classesTable.SourcesIncome;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class SourcesIncomeController {
    private static final String INCOME_SOURCES = "income_sources";
    @FXML
    private TextField field_search;
    @FXML
    private TableView<SourcesIncome> tableView;
    @FXML
    private TableColumn<SourcesIncome, Integer> column0;// int id;
    @FXML
    private TableColumn<SourcesIncome, String> column1;//  String name;
    @FXML
    private TableColumn<SourcesIncome, String> column2;//  int income_category_id;
    @FXML
    private TableColumn<SourcesIncome, String> column3;//  String description;

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene("scenes/tables-window.fxml");
    }
    @FXML
    protected void initialize(){
        column0.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        column2.setCellValueFactory(new PropertyValueFactory<>("income_category_id"));
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
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

    private SourcesIncome getSelectItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void searchOnField() {
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, INCOME_SOURCES, SourcesIncome.class));
    }
    private void initData() {
        CommandsSQL.fillTable(tableView, INCOME_SOURCES, SourcesIncome.class);
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
        SourcesIncome item = General.createInputDialog(
                "Введите данные",
                List.of("Наименование:", "ID категории дохода:", "Описание:"),
                List.of(true, true, true),
                new SourcesIncome(-1, "", -1, ""));
        if (item != null) {
            if (!item.getName().trim().isEmpty()
                    && CommandsSQL.checkID(item.getIncome_category_id(),"income_categories")) {
                CommandsSQL.insertDataIntoTable(INCOME_SOURCES, new SourcesIncome(CommandsSQL.getFreeID(INCOME_SOURCES),
                        item.getName(),
                        item.getIncome_category_id(),
                        item.getDescription()));
                initData();
                General.successfully("добавлено");
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void interactionEdition() {
        if (getSelectItem() != null) {
            SourcesIncome item = General.createInputDialog(
                    "Введите данные",
                    List.of("Наименование:", "ID категории дохода:", "Описание:"),
                    List.of(true, true, true),
                    getSelectItem());
            if (item != null) {
                if (!item.getName().trim().isEmpty()
                        && CommandsSQL.checkID(item.getIncome_category_id(),"income_categories")
                        && !item.getDescription().trim().isEmpty()) {
                    CommandsSQL.updateDataInTable(INCOME_SOURCES, item, "id = " + item.getId());
                    initData();
                    General.successfully("изменено");
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void interactionDeletion() {
        if (getSelectItem() != null) {
            SourcesIncome item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" +
                    "\nНаименование: " + item.getName() +
                    "\nID категории дохода: " + item.getIncome_category_id()+
                    "\nОписание: " + item.getDescription())) {
                CommandsSQL.deleteDataFromTable(INCOME_SOURCES, "id = " + item.getId() + ";");
                initData();
                General.successfully("удалено");
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }
}
