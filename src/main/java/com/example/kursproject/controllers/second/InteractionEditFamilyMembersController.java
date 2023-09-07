package com.example.kursproject.controllers.second;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.classesTable.FamilyMembers;
import com.example.kursproject.General;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.List;

public class InteractionEditFamilyMembersController {
    private static final String FAMILY_MEMBERS = "family_members";
    private final List<String> fieldsTable = List.of("id", "first_name", "last_name", "additional_info");
    public Button button_cancel;
    @FXML
    private TextField field_first_name;
    @FXML
    private TextField field_last_name;
    @FXML
    private TextArea field_additional_info;
    private int id = 0;

    public InteractionEditFamilyMembersController() {
    }

    @FXML
    protected void initialize() {
        clearAll();
    }

    @FXML
    protected void clickConfirmFamilyMember() {
        boolean check = true;
        if (General.isEmptyTFA(field_first_name)) check = false;
        if (General.isEmptyTFA(field_last_name)) check = false;
        if (check) {
            String first_name = field_first_name.getText().trim(),
                    last_name = field_last_name.getText().trim(),
                    addition_info = field_additional_info.getText().trim();
            FamilyMembers familyMember = new FamilyMembers(id, first_name, last_name, addition_info);
            if (CommandsSQL.updateDataInTable(FAMILY_MEMBERS, familyMember, fieldsTable, "id = " + id)) {
                General.selectedObject = familyMember;
                initFields();
                General.successfully("изменено");
            }
        }
    }

    @FXML
    protected void clickCancel() {
        ControlStages.closeSecondStage();
    }

    private void clearAll() {
        General.clearTFA(field_first_name);
        General.clearTFA(field_last_name);
        General.clearTFA(field_additional_info);
        initFields();
    }

    private void initFields() {
        FamilyMembers object = (FamilyMembers) General.selectedObject;
        id = object.getId();
        field_first_name.setText(object.getFirst_name());
        field_last_name.setText(object.getLast_name());
        field_additional_info.setText(object.getAdditional_info());
    }

    @FXML
    protected void clickClearFields() {
        clearAll();
    }
}
