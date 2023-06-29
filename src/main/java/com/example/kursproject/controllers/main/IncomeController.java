package com.example.kursproject.controllers.main;

import com.example.kursproject.*;
import com.example.kursproject.classesTable.FamilyMembers;
import com.example.kursproject.classesTable.Income;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IncomeController {
    private static final String INCOME = "income";
    private static final String INCOME_V = "incomeview";
    private final List<String> fieldsTable = List.of("id", "date", "family_member_id", "income_source_id", "amount", "description", "status", "comment");
    private final List<String> fieldsView = List.of("id", "date", "first_name", "last_name", "source_name", "category_name", "subcategory_name", "amount", "description", "status", "comment"); //
    private final List<String> stringList_T = List.of("Дата", "ID члена семьи", "ID источника дохода", "Сумма", "Описание", "Статус", "Комментарий");
    private final List<String> stringList_V = List.of("Дата", "Имя", "Фамилия", "Источник дохода", "Категория дохода", "Подкатегория дохода", "Сумма", "Описание", "Статус", "Комментарий"); //
    private final List<Boolean> booleanList = List.of(true, true, true, true, true, true, false);

    @FXML
    private TableView<Income> tableView;
    @FXML
    private TableColumn<Income, Integer> column0; //private int id;
    @FXML
    private TableColumn<Income, Date> column1; //private Date date;
    @FXML
    private TableColumn<Income, Integer> column2; //private int family_member_id;
    @FXML
    private TableColumn<Income, String> column2_1; //    private String first_name;
    @FXML
    private TableColumn<Income, String> column2_2; //    private String last_name;
    @FXML
    private TableColumn<Income, Integer> column3; //     private int income_source_id;
    @FXML
    private TableColumn<Income, String> column3_1; //    private String source_name;
    @FXML
    private TableColumn<Income, String> column3_2; //    private String category_name;
    @FXML
    private TableColumn<Income, String> column3_3; //    private String subcategory_name;
    @FXML
    private TableColumn<Income, Double> column4; //private double amount;
    @FXML
    private TableColumn<Income, String> column5; //private String description;
    @FXML
    private TableColumn<Income, Boolean> column6; //private boolean status;
    @FXML
    private TableColumn<Income, String> column7; //private String comment;
    @FXML
    private ComboBox<String> choice_subcategory;
    @FXML
    private ComboBox<String> choice_category;
    @FXML
    private ComboBox<String> choice_family_member;
    @FXML
    private ComboBox<String> choice_source_income;
    @FXML
    private ComboBox<String> choice_status;
    @FXML
    private DatePicker choice_date_from;
    @FXML
    private DatePicker choice_date_to;
    @FXML
    private TextField choice_amount_from;
    @FXML
    private TextField choice_amount_to;
    @FXML
    private TextField field_search;
    private General.Mode mode;
    private String NAME_TABLE;

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene("scenes/tables-window.fxml");
    }

    @FXML
    protected void initialize() {
        if (ControlStages.getSceneURL().equals(URLs.URL_INCOME_T)) {
            NAME_TABLE = INCOME;
            mode = General.Mode.TABLE;
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            column2.setCellValueFactory(new PropertyValueFactory<>("family_member_id"));
            column3.setCellValueFactory(new PropertyValueFactory<>("income_source_id"));
        } else {
            NAME_TABLE = INCOME_V;
            mode = General.Mode.VIEW;
            column2_1.setCellValueFactory(new PropertyValueFactory<>("first_name"));
            column2_2.setCellValueFactory(new PropertyValueFactory<>("last_name"));
            column3_1.setCellValueFactory(new PropertyValueFactory<>("source_name"));
            column3_2.setCellValueFactory(new PropertyValueFactory<>("category_name"));
            column3_3.setCellValueFactory(new PropertyValueFactory<>("subcategory_name"));
        }
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
        column4.setCellValueFactory(new PropertyValueFactory<>("amount"));
        column5.setCellValueFactory(new PropertyValueFactory<>("description"));
        column6.setCellValueFactory(new PropertyValueFactory<>("status"));
        column7.setCellValueFactory(new PropertyValueFactory<>("comment"));
        initComboBox();
        initDatePickers();
        initData();
    }

    private void initComboBox() {
        ObservableList<String> bool = FXCollections.observableArrayList();
        bool.addAll("Статус", "true", "false");
        choice_status.setItems(bool);
        switch (mode) {
            case TABLE -> {
                ObservableList<Integer> familyMemberInt = CommandsSQL.getColumnValues(INCOME, "family_member_id", Integer.class, true);
                ObservableList<String> familyMember = FXCollections.observableArrayList();
                for (Integer integer : familyMemberInt) familyMember.add(String.valueOf(integer));
                familyMember.add(0, "Выберите ID члена семьи");
                choice_family_member.setItems(familyMember);
                ObservableList<Integer> sourceInt = CommandsSQL.getColumnValues(INCOME, "income_source_id", Integer.class, true);
                ObservableList<String> source = FXCollections.observableArrayList();
                for (Integer integer : sourceInt) source.add(String.valueOf(integer));
                source.add(0, "Выберите ID источника дохода");
                choice_source_income.setItems(source);
            }
            case VIEW -> {
                ObservableList<FamilyMembers> familyMember = CommandsSQL.readDataFromTable("family_members", FamilyMembers.class);
                ObservableList<String> strFM = FXCollections.observableArrayList();
                for (FamilyMembers familyMembers:familyMember) strFM.add(familyMembers.getFirst_name() + " " + familyMembers.getLast_name());
                strFM.add(0, "Выберите члена семьи");
                choice_family_member.setItems(strFM);
                ObservableList<String> sourceName = CommandsSQL.getColumnValues(INCOME_V, "source_name", String.class, true);
                sourceName.add(0, "Выберите источник дохода");
                choice_source_income.setItems(sourceName);
                ObservableList<String> categories = CommandsSQL.getColumnValues(INCOME_V, "category_name", String.class, true);
                categories.add(0, "Выберите категорию дохода");
                choice_category.setItems(categories);
                ObservableList<String> subcategories = CommandsSQL.getColumnValues(INCOME_V, "subcategory_name", String.class, true);
                subcategories.add(0, "Выберите подкатегорию дохода");
                choice_subcategory.setItems(subcategories);
            }
        }
    }

    private void initDatePickers() {
        ObservableList<Income> incomes = CommandsSQL.readDataFromTable(INCOME, Income.class);
        LocalDate minDate = LocalDate.MAX;
        LocalDate maxDate = LocalDate.MIN;
        for (Income item : incomes) {
            java.sql.Date sqlDate = (java.sql.Date) item.getDate();
            LocalDate localDate = sqlDate.toLocalDate();
            if (localDate.isBefore(minDate)) minDate = localDate;
            if (localDate.isAfter(maxDate)) maxDate = localDate;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedMinDate = minDate.format(formatter);
        String formattedMaxDate = maxDate.format(formatter);
        choice_date_from.setValue(LocalDate.parse(formattedMinDate, formatter));
        choice_date_to.setValue(LocalDate.parse(formattedMaxDate, formatter));
    }

    private void initData() {
        tableView.setItems(CommandsSQL.readDataFromTable(NAME_TABLE, Income.class));
    }

    private Income getSelectItem() {// return tableView.getSelectionModel().getSelectedItem();
        if (tableView.getSelectionModel().getSelectedItem() == null) return null;
        return CommandsSQL.retrieveObjectById(INCOME, tableView.getSelectionModel().getSelectedItem().getId(), Income.class);
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
        Income item = General.createInputDialog("Введите данные", stringList_T, booleanList,
                new Income(0, new Date(), 0, 0, 0.0, "", false, ""));
        if (item != null) {
            if (item.getDate() != null
                    && CommandsSQL.checkID(item.getFamily_member_id(), "family_members")
                    && CommandsSQL.checkID(item.getIncome_source_id(), "income_sources")
                    && item.getAmount() > 0 && !item.getDescription().trim().isEmpty()) {
                if (item.getComment().isEmpty()) item.setComment("");
                if (CommandsSQL.insertDataIntoTable(INCOME, new Income(CommandsSQL.getFreeID(INCOME),
                        item.getDate(), item.getFamily_member_id(), item.getIncome_source_id(), item.getAmount(),
                        item.getDescription(), item.isStatus(), item.getComment()), fieldsTable)) {
                    searchOnField();
                    General.successfully("добавлено");
                }
            } else General.ErrorWindow("Не выполнено условие ввода!");
        }
    }

    private void edition() {
        if (getSelectItem() != null) {
            double amount = getSelectItem().getAmount();
            Income item = General.createInputDialog("Введите данные", stringList_T, booleanList, getSelectItem());
            if (item != null) {
                if (item.getDate() != null
                        && CommandsSQL.checkID(item.getFamily_member_id(), "family_members")
                        && CommandsSQL.checkID(item.getIncome_source_id(), "income_sources")
                        && item.getAmount() > 0
                        && (!item.isStatus() || (item.isStatus() && CommandsSQL.calculateTotalBalance() + (item.getAmount() - amount) >= 0))
                        && !item.getDescription().trim().isEmpty()) {
                    if (item.getComment().isEmpty()) item.setComment("");
                    if (CommandsSQL.updateDataInTable(INCOME, item, fieldsTable, "id = " + item.getId())) {
                        searchOnField();
                        General.successfully("изменено");
                    }
                } else General.ErrorWindow("Не выполнено условие ввода!");
            }
        } else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    private void deletion() {
        if (getSelectItem() != null) {
            Income item = getSelectItem();
            String comment = "";
            if (item.getComment() != null && !item.getComment().trim().isEmpty())
                comment = "\n" + stringList_T.get(6) + ": " + item.getComment();
            if (General.getConfirmation("Удалить запись о доходе?" + "\nНаименование:" +
                    "\n" + stringList_T.get(0) + ": " + item.getDate() +
                    "\n" + stringList_T.get(1) + ": " + item.getFamily_member_id() +
                    "\n" + stringList_T.get(2) + ": " + item.getIncome_source_id() +
                    "\n" + stringList_T.get(3) + ": " + item.getAmount() +
                    "\n" + stringList_T.get(4) + ": " + item.getDescription() +
                    "\n" + stringList_T.get(5) + ": " + item.isStatus() + comment)) {
                if(CommandsSQL.deleteDataFromTable(INCOME, "id = " + item.getId() + ";")) {
                    searchOnField();
                    General.successfully("удалено");
                }
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
    }

    @FXML
    protected void gotoTable() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_T);
    }

    @FXML
    protected void gotoView() throws IOException {
        ControlStages.changeScene(URLs.URL_INCOME_V);
    }

    @FXML
    protected void createReport() {
        // Создание Map для хранения соответствий исходных и желаемых наименований столбцов
        Map<String, String> columnNames = new HashMap<>();
        List<String> onlyColumns = null;
        columnNames.put("id", "ID");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        StringBuilder parametrs = new StringBuilder();
        if (choice_date_from != null && choice_date_to != null) {
            parametrs.append("дата: от '").append(choice_date_from.getValue().format(formatter))
                    .append("' до '").append(choice_date_to.getValue().format(formatter)).append("'");
        } else {
            if (choice_date_from != null)
                parametrs.append("дата: от '").append(choice_date_from.getValue().format(formatter)).append("'");
            if (choice_date_to != null)
                parametrs.append("дата: до '").append(choice_date_to.getValue().format(formatter)).append("'");
        }
        if (!choice_amount_from.getText().trim().isEmpty() || !choice_amount_to.getText().trim().isEmpty()) {
            double valueFrom = 0;
            double valueTo = 0;
            try {
                valueFrom = Double.parseDouble(choice_amount_from.getText().trim());
            } catch (NumberFormatException ignored) {}
            try {
                valueTo = Double.parseDouble(choice_amount_to.getText().trim());
            } catch (NumberFormatException ignored) {}
            if (valueFrom > 0 && valueTo > 0) {
                if (!parametrs.toString().equals("")) parametrs.append(", ");
                parametrs.append("сумма: от ").append(valueFrom).append("₽ до ").append(valueTo).append("₽");
            } else {
                if (valueFrom > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("сумма: от ").append(valueFrom).append("₽");
                }
                if (valueTo > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("сумма: до ").append(valueTo).append("₽");
                }
            }
        }
        if (choice_status.getSelectionModel().getSelectedIndex() > 0) {
            if (!parametrs.toString().equals("")) parametrs.append(", ");
            parametrs.append("статус: '").append(choice_status.getSelectionModel().getSelectedItem()).append("'");
        }
        switch (mode) {
            case TABLE -> {
                for (int i = 1; i < fieldsTable.size(); i++)
                    columnNames.put(fieldsTable.get(i), stringList_T.get(i - 1));
                if (choice_family_member.getSelectionModel().getSelectedIndex() > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("ID члена семьи: '").append(choice_family_member.getSelectionModel().getSelectedItem()).append("'");
                }
                if (choice_source_income.getSelectionModel().getSelectedIndex() > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("ID источника дохода: '").append(choice_source_income.getSelectionModel().getSelectedItem()).append("'");
                }
                onlyColumns = fieldsTable;
            }
            case VIEW -> {
                for (int i = 1; i < fieldsView.size(); i++) columnNames.put(fieldsView.get(i), stringList_V.get(i - 1));
                if (choice_family_member.getSelectionModel().getSelectedIndex() > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("член семьи: '").append(choice_family_member.getSelectionModel().getSelectedItem()).append("'");
                }
                if (choice_source_income.getSelectionModel().getSelectedIndex() > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("источник дохода: '").append(choice_source_income.getSelectionModel().getSelectedItem()).append("'");
                }
                if (choice_category.getSelectionModel().getSelectedIndex() > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("категория дохода: '").append(choice_category.getSelectionModel().getSelectedItem()).append("'");
                }
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0) {
                    if (!parametrs.toString().equals("")) parametrs.append(", ");
                    parametrs.append("подкатегория дохода: '").append(choice_subcategory.getSelectionModel().getSelectedItem()).append("'");
                }
                onlyColumns = fieldsView;
            }
        }
        if (!field_search.getText().trim().isEmpty()) {
            if (!parametrs.toString().equals("")) parametrs.append(", ");
            parametrs.append("'").append(field_search.getText().trim()).append("'");
        }
        ReportGenerator.selectPath("Доходы", tableView.getItems(), columnNames, onlyColumns, parametrs.toString());
    }

    private void searchOnField() {
        StringBuilder sqlQuery = new StringBuilder();
        if (choice_date_from != null && choice_date_to != null) { // date BETWEEN 'начальная_дата' AND 'конечная_дата'
            sqlQuery.append("date BETWEEN '").append(choice_date_from.getValue()).append("' AND '").append(choice_date_to.getValue()).append("'");
        } else {
            if (choice_date_from != null) sqlQuery.append("date >= '").append(choice_date_from.getValue()).append("'");
            if (choice_date_to != null) sqlQuery.append("date <= '").append(choice_date_to.getValue()).append("'");
        }
        if (!choice_amount_from.getText().trim().isEmpty() || !choice_amount_to.getText().trim().isEmpty()) {
            double valueFrom = 0;
            double valueTo = 0;
            try {
                valueFrom = Double.parseDouble(choice_amount_from.getText().trim());
            } catch (NumberFormatException ignored) {}
            try {
                valueTo = Double.parseDouble(choice_amount_to.getText().trim());
            } catch (NumberFormatException ignored) {}
            if (valueFrom > 0 && valueTo > 0) {
                if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                sqlQuery.append("amount BETWEEN '").append(valueFrom).append("' AND '").append(valueTo).append("'");
            } else {
                if (valueFrom > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("amount >= '").append(valueFrom).append("'");
                }
                if (valueTo > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("amount <= '").append(valueTo).append("'");
                }
            }
        }
        if (choice_status.getSelectionModel().getSelectedIndex() > 0) {
            if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
            sqlQuery.append("status = '").append(choice_status.getSelectionModel().getSelectedItem()).append("'");
        }
        switch (mode) {
            case TABLE -> {
                if (choice_family_member.getSelectionModel().getSelectedIndex() > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("family_member_id = ").append(choice_family_member.getSelectionModel().getSelectedItem());
                }
                if (choice_source_income.getSelectionModel().getSelectedIndex() > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("income_source_id = ").append(choice_source_income.getSelectionModel().getSelectedItem());
                }
            }
            case VIEW -> {
                if (choice_family_member.getSelectionModel().getSelectedIndex() > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    ObservableList<FamilyMembers> familyMembers = CommandsSQL.readDataFromTable("family_members", FamilyMembers.class);
                    ObservableList<String> strFM = FXCollections.observableArrayList();
//                    System.out.println(choice_family_member.getSelectionModel().getSelectedItem());
                    for (int i = 0; i < familyMembers.size(); i++) {
                        FamilyMembers familyMember = familyMembers.get(i);
                        strFM.add(familyMember.getFirst_name() + " " + familyMember.getLast_name());
                        if (strFM.get(i).equals(choice_family_member.getSelectionModel().getSelectedItem())){
                            sqlQuery.append("first_name = '").append(familyMember.getFirst_name()).append("'").append(" AND ");
                            sqlQuery.append("last_name = '").append(familyMember.getLast_name()).append("'");
                        }
                    }
                }
                if (choice_source_income.getSelectionModel().getSelectedIndex() > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("source_name = '").append(choice_source_income.getSelectionModel().getSelectedItem()).append("'");
                }
                if (choice_category.getSelectionModel().getSelectedIndex() > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("category_name = '").append(choice_category.getSelectionModel().getSelectedItem()).append("'");
                }
                if (choice_subcategory.getSelectionModel().getSelectedIndex() > 0) {
                    if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
                    sqlQuery.append("subcategory_name = '").append(choice_subcategory.getSelectionModel().getSelectedItem()).append("'");
                }
            }
        }
        if (!sqlQuery.toString().trim().isEmpty()){
            tableView.setItems(CommandsSQL.filterData(NAME_TABLE, sqlQuery + " ORDER BY id", Income.class));
            tableView.setItems(General.filterTableData(tableView.getItems(), field_search.getText().trim(), tableView));
        } else initData();
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
    protected void choiceDateFrom() {
        searchOnField();
    }

    @FXML
    protected void choiceDateTo() {
        searchOnField();
    }

    @FXML
    protected void choiceAmountFrom() {
        searchOnField();
    }

    @FXML
    protected void choiceAmountTo() {
        searchOnField();
    }

    @FXML
    protected void choiceSourceIncome() {
        searchOnField();
    }

    @FXML
    protected void choiceFamilyMember() {
        searchOnField();
    }
    @FXML
    protected void choiceStatus() {
        searchOnField();
    }


    @FXML
    protected void clearAllFields() {
        field_search.clear();
        choice_amount_from.clear();
        choice_amount_to.clear();
        choice_status.getSelectionModel().select(0);
        choice_family_member.getSelectionModel().select(0);
        choice_source_income.getSelectionModel().select(0);
        if (mode == General.Mode.VIEW) {
            choice_category.getSelectionModel().select(0);
            choice_subcategory.getSelectionModel().select(0);
        }
        initDatePickers();
        initData();
    }
}
