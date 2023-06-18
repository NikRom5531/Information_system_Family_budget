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
}
