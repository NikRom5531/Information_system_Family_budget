package com.example.kursproject.controllers.second;

import com.example.kursproject.CommandsSQL;
import com.example.kursproject.ControlStages;
import com.example.kursproject.classesTable.FamilyMembers;
import com.example.kursproject.General;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class InteractionEditFamilyMembersController {
    private static final String FAMILY_MEMBERS = "family_members";
    public Button button_cancel;
    @FXML
    private TextField field_first_name;
    @FXML
    private TextField field_last_name;
    @FXML
    private TextArea field_additional_info;
    private int id = -1;
    public InteractionEditFamilyMembersController() {
    }
    @FXML
    protected void initialize() {
        clearAll();
    }
    @FXML
    protected void clickConfirmFamilyMember() {
        General.setColorTFA(field_first_name, "00FF00");
        General.setColorTFA(field_last_name, "00FF00");
        if (field_first_name.getText().trim().isEmpty() || field_last_name.getText().trim().isEmpty()) {
            if (field_first_name.getText().trim().isEmpty()) General.setColorTFA(field_first_name, "FF0000");
            if (field_last_name.getText().trim().isEmpty()) General.setColorTFA(field_last_name, "FF0000");
            System.out.println("Error!");
        } else {
            String first_name = field_first_name.getText().trim(),
                    last_name = field_last_name.getText().trim(),
                    addition_info = field_additional_info.getText().trim();
            FamilyMembers familyMember = new FamilyMembers(id, first_name, last_name, addition_info);
            CommandsSQL.updateDataInTable(FAMILY_MEMBERS, familyMember, "id = " + id);
            General.selectedObject = familyMember;
            initFields();
            General.successfully("изменено");
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
    private void initFields(){
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