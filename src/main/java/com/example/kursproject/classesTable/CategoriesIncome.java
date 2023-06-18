package com.example.kursproject.classesTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriesIncome {
    private int id;
    private String name;
    private int subcategory_id;
}
