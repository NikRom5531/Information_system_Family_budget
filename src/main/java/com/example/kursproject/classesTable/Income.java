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
public class Income {
    private int id;
    private Date date;
    private int family_member_id;
    private int income_source_id;
    private double amount;
    private String description;
    private boolean status;
    private String comment;
    /////
    private String first_name;
    private String last_name;
    private String source_name;
    private String category_name;
    private String subcategory_name;
    /////

    public Income(int id, Date date, int family_member_id, int income_source_id, double amount, String description, boolean status, String comment) {
        this.id = id;
        this.date = date;
        this.family_member_id = family_member_id;
        this.income_source_id = income_source_id;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.comment = comment;
    }

    public Income(int id, Date date, String first_name, String last_name, String source_name, String category_name, String subcategory_name, double amount, String description, boolean status, String comment) {
        this.id = id;
        this.date = date;
        this.first_name = first_name;
        this.last_name = last_name;
        this.source_name = source_name;
        this.category_name = category_name;
        this.subcategory_name = subcategory_name;
        this.amount = amount;
        this.description = description;
        this.status = status;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Income{" +
                "id=" + id +
                ", date=" + date +
                ", family_member_id=" + family_member_id +
                ", income_source_id=" + income_source_id +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", comment='" + comment + '\'' +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", source_name='" + source_name + '\'' +
                ", category_name='" + category_name + '\'' +
                ", subcategory_name='" + subcategory_name + '\'' +
                '}';
    }
}
