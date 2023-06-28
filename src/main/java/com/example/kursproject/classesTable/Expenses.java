package com.example.kursproject.classesTable;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Expenses {
    private int id;
    private Date date;
    private int family_member_id;
    private int expense_category_id;
    private double amount;
    private String description;
    private boolean status;
    private String comment;
    /////
    private String first_name;
    private String last_name;
    private String category_name;
    /////

    public Expenses(int id, Date date, int family_member_id, int expense_category_id, double amount, String description, boolean status, String comment) {
        this.id = id;
        this.date = date;
        this.family_member_id = family_member_id;
        this.expense_category_id = expense_category_id;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.comment = comment;
    }

    public Expenses(int id, Date date, String first_name, String last_name, String category_name, double amount, String description, boolean status, String comment) {
        this.id = id;
        this.date = date;
        this.first_name = first_name;
        this.last_name = last_name;
        this.category_name = category_name;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.comment = comment;
    }
}
