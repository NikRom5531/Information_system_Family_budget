package com.example.kursproject.controllers.main;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.classesTable.Expenses;
import com.example.kursproject.General;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ExpensesController {
    private static final String EXPENSES = "expenses";
    @FXML
    private TextField field_search;
    @FXML
    private TableView<Expenses> tableView;
    @FXML
    private TableColumn<Expenses, Integer> column0;//    private int id;
    @FXML
    private TableColumn<Expenses, Date> column1;//    private Date date;
    @FXML
    private TableColumn<Expenses, Integer> column2;//    private int family_member_id;
    @FXML
    private TableColumn<Expenses, Integer> column3;//    private int expense_category_id;
    @FXML
    private TableColumn<Expenses, Double> column4;//    private double amount;
    @FXML
    private TableColumn<Expenses, String> column5;//    private String description;
    @FXML
    private TableColumn<Expenses, Boolean> column6;//    private boolean status;
    @FXML
    private TableColumn<Expenses, String> column7;//    private String comment;

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene("scenes/tables-window.fxml");
    }

    @FXML
    protected void initialize() {
        column0.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setCellValueFactory(new PropertyValueFactory<>("date"));
        column1.setCellFactory(column -> new TextFieldTableCell<>() {
            private final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");

            @Override
            public void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(format.format(item));
            }
        });
        column2.setCellValueFactory(new PropertyValueFactory<>("family_member_id"));
        column3.setCellValueFactory(new PropertyValueFactory<>("expense_category_id"));
        column4.setCellValueFactory(new PropertyValueFactory<>("amount"));
        column5.setCellValueFactory(new PropertyValueFactory<>("description"));
        column6.setCellValueFactory(new PropertyValueFactory<>("status"));
        column7.setCellValueFactory(new PropertyValueFactory<>("comment"));
        initData();
    }

    @FXML
    protected void onRefreshButtonClick() {
        initData();
    }

    @FXML
    protected void onInputMethodTextChanged() {
        searchOnField();
    }

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    private Expenses getSelectItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    private void searchOnField() {
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, EXPENSES, Expenses.class));
    }

    private void initData() {
        CommandsSQL.fillTable(tableView, EXPENSES, Expenses.class);
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
        Expenses item = General.createInputDialog(
                "Введите данные",
                List.of("Дата:", "ID члена семьи:", "ID категории расхода:", "Сумма:", "Описание:", "Статус:", "Комментарий:"),
                List.of(true, true, true, true, true, true, false),
                new Expenses(-1, new Date(), -1, -1, -1, "", false, ""));
        if (item != null) {
            if (item.getDate() != null
                    && CommandsSQL.checkID(item.getFamily_member_id(), "family_members")
                    && CommandsSQL.checkID(item.getExpense_category_id(), "expense_categories")
                    && 0 < item.getAmount()
                    && (!item.isStatus() || (item.isStatus() && (item.getAmount() <= CommandsSQL.calculateTotalBalance())))
                    && !item.getDescription().trim().isEmpty()) {
                CommandsSQL.insertDataIntoTable(EXPENSES, new Expenses(CommandsSQL.getFreeID(EXPENSES),
                        item.getDate(),
                        item.getFamily_member_id(),
                        item.getExpense_category_id(),
                        item.getAmount(),
                        item.getDescription(),
                        item.isStatus(),
                        item.getComment()));
                initData();
                General.successfully("добавлено");
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void interactionEdition() {
        if (getSelectItem() != null) {
            double amount = getSelectItem().getAmount();
            Expenses item = General.createInputDialog(
                    "Введите данные",
                    List.of("Дата:", "ID члена семьи:", "ID категории расхода:", "Сумма:", "Описание:", "Статус:", "Комментарий:"),
                    List.of(true, true, true, true, true, true, false),
                    getSelectItem());
            if (item != null) {
                if (item.getDate() != null
                        && CommandsSQL.checkID(item.getFamily_member_id(), "family_members")
                        && CommandsSQL.checkID(item.getExpense_category_id(), "expense_categories")
                        && 0 < item.getAmount()
                        && (!item.isStatus() || (item.isStatus() && ((item.getAmount() - amount) <= CommandsSQL.calculateTotalBalance())))
                        && !item.getDescription().trim().isEmpty()) {
                    CommandsSQL.updateDataInTable(EXPENSES, item, "id = " + item.getId());
                    initData();
                    General.successfully("изменено");
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void interactionDeletion() {
        if (getSelectItem() != null) {
            Expenses item = getSelectItem();
            if (General.getConfirmation("Удалить подкатегорию дохода?" +
                    "\nДата: " + item.getDate() +
                    "\nID члена семьи: " + item.getFamily_member_id() +
                    "\nID категории расхода: " + item.getExpense_category_id() +
                    "\nСумма: " + item.getAmount() +
                    "\nОписание: " + item.getDescription() +
                    "\nСтатус: " + item.isStatus() +
                    "\nКомментарий: " + item.getComment())) {
                CommandsSQL.deleteDataFromTable(EXPENSES, "id = " + item.getId() + ";");
                initData();
                General.successfully("удалено");
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }
}
