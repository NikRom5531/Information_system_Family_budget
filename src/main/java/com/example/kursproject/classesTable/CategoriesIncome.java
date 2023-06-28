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
    private String subcategory_name;

    public CategoriesIncome(int id, String name, int subcategory_id) {
        this.id = id;
        this.name = name;
        this.subcategory_id = subcategory_id;
    }

    public CategoriesIncome(int id, String name, String subcategory_name) {
        this.id = id;
        this.name = name;
        this.subcategory_name = subcategory_name;
    }

    @Override
    public String toString() {
        return "CategoriesIncome{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subcategory_id=" + subcategory_id +
                ", subcategory_name='" + subcategory_name + '\'' +
                '}';
    }
}
