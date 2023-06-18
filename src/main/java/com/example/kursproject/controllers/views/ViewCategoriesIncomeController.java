package com.example.kursproject.controllers.views;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.General;
import com.example.kursproject.classesTable.CategoriesIncome;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.util.List;

public class ViewCategoriesIncomeController {
    private static final String INCOME_CATEGORIES = "income_categories";
    @FXML
    private TextField field_search;
    @FXML
    private TableView<CategoriesIncome> tableView;
    @FXML
    private TableColumn<CategoriesIncome, Integer> column0;//    private int id;
    @FXML
    private TableColumn<CategoriesIncome, String> column1;//    private String name;
    @FXML
    private TableColumn<CategoriesIncome, Integer> column2;//    private int subcategory_id;

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene("scenes/tables-window.fxml");
    }
    @FXML
    protected void initialize(){
        column0.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setCellValueFactory(new PropertyValueFactory<>("name"));
        column2.setCellValueFactory(new PropertyValueFactory<>("subcategory_id"));
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
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void searchOnField() {
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, INCOME_CATEGORIES, CategoriesIncome.class));
    }
    private void initData() {
        CommandsSQL.fillTable(tableView, INCOME_CATEGORIES, CategoriesIncome.class);
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
        CategoriesIncome item = General.createInputDialog(
                "Введите данные",
                List.of("Наименование:", "ID подкатегории дохода:"),
                List.of(true, true),
                new CategoriesIncome(-1, "", -1));
        if (item != null) {
            if (!item.getName().trim().isEmpty() && CommandsSQL.checkID(item.getSubcategory_id(), "income_subcategories")) {
                CommandsSQL.insertDataIntoTable(INCOME_CATEGORIES,
                        new CategoriesIncome(CommandsSQL.getFreeID(INCOME_CATEGORIES), item.getName(), item.getSubcategory_id()));
                initData();
                General.successfully("добавлено");
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void interactionEdition() {
        if (getSelectItem() != null) {
            CategoriesIncome item = General.createInputDialog(
                    "Введите данные",
                    List.of("Наименование:", "ID подкатегории дохода:"),
                    List.of(true, true),
                    getSelectItem());
            if (item != null) {
                if (!item.getName().trim().isEmpty() && CommandsSQL.checkID(item.getSubcategory_id(), "income_subcategories")) {
                    CommandsSQL.updateDataInTable(INCOME_CATEGORIES, item, "id = " + item.getId());
                    initData();
                    General.successfully("изменено");
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void interactionDeletion() {
        if (getSelectItem() != null) {
            CategoriesIncome item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" + "\nНаименование: " + item.getName())) {
                CommandsSQL.deleteDataFromTable(INCOME_CATEGORIES, "id = " + item.getId() + " AND name = '" + item.getName() + "';");
                initData();
                General.successfully("удалено");
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }
}
