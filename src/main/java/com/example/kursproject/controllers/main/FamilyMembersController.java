package com.example.kursproject.controllers.main;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.URLs;
import com.example.kursproject.classesTable.FamilyMembers;
import com.example.kursproject.General;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;

public class FamilyMembersController {
    private static final String FAMILY_MEMBERS = "family_members";
    private static final String FAMILY_MEMBERS_V = "familymembersview";
    @FXML
    private TableView<FamilyMembers> tableView;
    @FXML
    private TableColumn<FamilyMembers, Integer> column0;
    @FXML
    private TableColumn<FamilyMembers, String> column1;
    @FXML
    private TableColumn<FamilyMembers, String> column2;
    @FXML
    private TableColumn<FamilyMembers, String> column3;
    @FXML
    private TextField field_search;
    private String NAME_TABLE;

    public void initData() {
        CommandsSQL.fillTable(tableView, NAME_TABLE, FamilyMembers.class);
    }

    @FXML
    protected void initialize() {
        if (ControlStages.getSceneURL().equals(URLs.URL_FAMILY_MEMBER_T)) {
            column0.setCellValueFactory(new PropertyValueFactory<>("id"));
            NAME_TABLE = FAMILY_MEMBERS;
        } else NAME_TABLE = FAMILY_MEMBERS_V;
        column1.setCellValueFactory(new PropertyValueFactory<>("first_name"));
        column2.setCellValueFactory(new PropertyValueFactory<>("last_name"));
        column3.setCellValueFactory(new PropertyValueFactory<>("additional_info"));
        initData();
    }

    @FXML
    protected void clickOpenAdditionWin() {
        ControlStages.newSecondStage("Добавление данных члена семьи", URLs.URL_ADD_FAMILY_MEMBER);
    }

    @FXML
    protected void clickOpenEditWin() {
        if (getSelectItem() != null)
            ControlStages.newSecondStage("Изменение данных члена семьи", URLs.URL_EDIT_FAMILY_MEMBER);
        else General.ErrorWindow("Не была выбрана строка для изменения!");
    }

    @FXML
    protected void clickOpenDeleteWin() {
        if (getSelectItem() != null) {
            FamilyMembers item = getSelectItem();
            String condition = "";
            if (!item.getAdditional_info().trim().isEmpty())
                condition = "\nДоп.информация: " + item.getAdditional_info();
            if (General.getConfirmation("Удалить члена семьи?" +
                    "\nИмя: " + item.getFirst_name() + "\nФамилия: " + item.getLast_name() + condition)) {
                CommandsSQL.deleteDataFromTable(FAMILY_MEMBERS, "id = " + item.getId() +
                        " AND first_name = '" + item.getFirst_name() + "'" +
                        " AND last_name = '" + item.getLast_name() + "'" +
                        " AND additional_info = '" + item.getAdditional_info() + "';");
                initData();
                General.successfully("удалено");
            }
        } else General.ErrorWindow("Не была выбрана строка для удаления!");
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
        tableView.setItems(General.filterTableData(field_search.getText().trim(), tableView, NAME_TABLE, FamilyMembers.class));
    }

    @FXML
    protected void onMouseClicked() {
        General.setSelectedObject(getSelectItem());
    }

    private FamilyMembers getSelectItem() {
        return tableView.getSelectionModel().getSelectedItem();
    }

    @FXML
    protected void gotoTable() throws IOException {
        ControlStages.changeScene(URLs.URL_FAMILY_MEMBER_T);
    }

    @FXML
    protected void gotoView() throws IOException {
        ControlStages.changeScene(URLs.URL_FAMILY_MEMBER_V);
    }
}