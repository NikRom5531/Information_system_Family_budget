package com.example.kursproject.controllers;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.URLs;
import com.example.kursproject.classesTable.Expenses;
import com.example.kursproject.classesTable.FamilyMembers;
import com.example.kursproject.classesTable.Income;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class StatisticsController {
    private static final String INCOME = "income";
    private static final String EXPENSES = "expenses";
    @FXML
    private ComboBox<String> choice_expense_income;
    @FXML
    private ComboBox<String> choice_status;
    @FXML
    private DatePicker choice_date_from;
    @FXML
    private DatePicker choice_date_to;
    @FXML
    private ComboBox<String> choice_family_member;
    @FXML
    private CategoryAxis xAxis = new CategoryAxis();
    @FXML
    private NumberAxis yAxis = new NumberAxis();
    @FXML
    private LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    @FXML
    protected void initialize() {
        initComboBox();
        initDatePickers();
        initData();
    }

    private void initData() {
        ObservableList<Income> incomes = CommandsSQL.readDataFromTable(INCOME, Income.class);
        ObservableList<Expenses> expenses = CommandsSQL.readDataFromTable(EXPENSES, Expenses.class);
        setLineChart(incomes, expenses);
    }

    private void initComboBox() {
        ObservableList<String> bool = FXCollections.observableArrayList();
        bool.addAll("Выберите статус", "true", "false");
        choice_status.setItems(bool);
        ObservableList<String> expInc = FXCollections.observableArrayList();
        expInc.addAll("Доходы и расходы", "Только доходы", "Только расходы"); // 0 - все, 1 - Income, 2 - Expense
        choice_expense_income.setItems(expInc);
        ObservableList<FamilyMembers> familyMember = CommandsSQL.readDataFromTable("family_members", FamilyMembers.class);
        ObservableList<String> strFM = FXCollections.observableArrayList();
        for (FamilyMembers familyMembers : familyMember)
            strFM.add(familyMembers.getFirst_name() + " " + familyMembers.getLast_name());
        strFM.add(0, "Выберите члена семьи");
        choice_family_member.setItems(strFM);
    }

    private void initDatePickers() {
        ObservableList<Income> incomes = CommandsSQL.readDataFromTable(INCOME, Income.class);
        ObservableList<Expenses> expenses = CommandsSQL.readDataFromTable(EXPENSES, Expenses.class);
        LocalDate minDateInc = LocalDate.MAX;
        LocalDate maxDateInc = LocalDate.MIN;
        LocalDate minDateExp = LocalDate.MAX;
        LocalDate maxDateExp = LocalDate.MIN;
        for (Income item : incomes) {
            java.sql.Date sqlDate = (java.sql.Date) item.getDate();
            LocalDate localDate = sqlDate.toLocalDate();
            if (localDate.isBefore(minDateInc)) minDateInc = localDate;
            if (localDate.isAfter(maxDateInc)) maxDateInc = localDate;
        }
        for (Expenses item : expenses) {
            java.sql.Date sqlDate = (java.sql.Date) item.getDate();
            LocalDate localDate = sqlDate.toLocalDate();
            if (localDate.isBefore(minDateExp)) minDateExp = localDate;
            if (localDate.isAfter(maxDateExp)) maxDateExp = localDate;
        }
        LocalDate minDate;
        LocalDate maxDate;
        if (minDateExp.isBefore(minDateInc)) minDate = minDateExp;
        else minDate = minDateInc;
        if (maxDateExp.isAfter(maxDateInc)) maxDate = maxDateExp;
        else maxDate = maxDateInc;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String formattedMinDate = minDate.format(formatter);
        String formattedMaxDate = maxDate.format(formatter);
        choice_date_from.setValue(LocalDate.parse(formattedMinDate, formatter));
        choice_date_to.setValue(LocalDate.parse(formattedMaxDate, formatter));
    }

    @FXML
    protected void onGoBackButtonClick() throws IOException {
        ControlStages.changeScene(URLs.URL_MAIN);
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
    protected void choiceFamilyMember() {
        searchOnField();
    }

    @FXML
    protected void choiceStatus() {
        searchOnField();
    }


    @FXML
    protected void onRefreshButtonClick() {
        initData();
        searchOnField();
    }

    @FXML
    protected void choiceExpInc() {
        searchOnField();
    }

    @FXML
    protected void clearAllFields() {
        choice_status.getSelectionModel().select(0);
        choice_family_member.getSelectionModel().select(0);
        initDatePickers();
        initData();
    }
    private void searchOnField() {
        StringBuilder sqlQuery = new StringBuilder();
        if (choice_date_from != null && choice_date_to != null) { // date BETWEEN 'начальная_дата' AND 'конечная_дата'
            sqlQuery.append("date BETWEEN '").append(choice_date_from.getValue()).append("' AND '").append(choice_date_to.getValue()).append("'");
        } else {
            if (choice_date_from != null) sqlQuery.append("date >= '").append(choice_date_from.getValue()).append("'");
            if (choice_date_to != null) sqlQuery.append("date <= '").append(choice_date_to.getValue()).append("'");
        }
        if (choice_status.getSelectionModel().getSelectedIndex() > 0) {
            if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
            sqlQuery.append("status = '").append(choice_status.getSelectionModel().getSelectedItem()).append("'");
        }
        if (choice_family_member.getSelectionModel().getSelectedIndex() > 0) {
            if (!sqlQuery.toString().equals("")) sqlQuery.append(" AND ");
            ObservableList<FamilyMembers> familyMembers = CommandsSQL.readDataFromTable("family_members", FamilyMembers.class);
            ObservableList<String> strFM = FXCollections.observableArrayList();
            for (int i = 0; i < familyMembers.size(); i++) {
                FamilyMembers familyMember = familyMembers.get(i);
                strFM.add(familyMember.getFirst_name() + " " + familyMember.getLast_name());
                if (strFM.get(i).equals(choice_family_member.getSelectionModel().getSelectedItem())) {
                    sqlQuery.append("family_member_id = ").append(familyMember.getId());
                }
            }
        }
        if (!sqlQuery.toString().trim().isEmpty()) { // 0 - все, 1 - Income, 2 - Expense
            ObservableList<Income> incomes = FXCollections.observableArrayList();
            ObservableList<Expenses> expenses = FXCollections.observableArrayList();
            if (choice_expense_income.getSelectionModel().getSelectedIndex() != 2) incomes = CommandsSQL.filterData(INCOME, sqlQuery + " ORDER BY date", Income.class);
            if (choice_expense_income.getSelectionModel().getSelectedIndex() != 1) expenses = CommandsSQL.filterData(EXPENSES, sqlQuery + " ORDER BY date", Expenses.class);
            setLineChart(incomes, expenses);
        } else initData();
    }
    private void setLineChart(ObservableList<Income> incomes, ObservableList<Expenses> expenses){
        lineChart.getData().clear();
        lineChart.setAnimated(false);
        lineChart.setLegendVisible(true);
        lineChart.setBackground(Background.fill(Color.WHITE));
        yAxis.setLabel("Сумма (в рублях)");
        xAxis.setLabel("Дата");
        // Создание серий данных
        XYChart.Series<String, Number> expensesSeries = new XYChart.Series<>();
        expensesSeries.setName("Расходы");
        XYChart.Series<String, Number> incomesSeries = new XYChart.Series<>();
        incomesSeries.setName("Доходы");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        // Заполнение данных
        for (Income income : incomes) {
            Date date = income.getDate();
            String dateString = dateFormat.format(date);
            double amount = income.getAmount();
            // Проверяем, есть ли уже данные для данной даты в серии доходов
            XYChart.Data<String, Number> existingIncomeData = null;
            for (XYChart.Data<String, Number> data : incomesSeries.getData()) {
                if (data.getXValue().equals(dateString)) {
                    existingIncomeData = data;
                    break;
                }
            }
            // Если данные уже существуют, добавляем к существующему значению
            if (existingIncomeData != null) existingIncomeData.setYValue(existingIncomeData.getYValue().doubleValue() + amount);
            // В противном случае создаем новые данные для даты
            else incomesSeries.getData().add(new XYChart.Data<>(dateString, amount));
        }
        for (Expenses expense : expenses) {
            Date date = expense.getDate();
            String dateString = dateFormat.format(date);
            double amount = expense.getAmount();
            // Проверяем, есть ли уже данные для данной даты в серии расходов
            XYChart.Data<String, Number> existingExpenseData = null;
            for (XYChart.Data<String, Number> data : expensesSeries.getData()) {
                if (data.getXValue().equals(dateString)) {
                    existingExpenseData = data;
                    break;
                }
            }
            // Если данные уже существуют, добавляем к существующему значению
            if (existingExpenseData != null) existingExpenseData.setYValue(existingExpenseData.getYValue().doubleValue() + amount);
            // В противном случае создаем новые данные для даты
            else expensesSeries.getData().add(new XYChart.Data<>(dateString, amount));
        }
        lineChart.getData().addAll(expensesSeries, incomesSeries);
    }

}
