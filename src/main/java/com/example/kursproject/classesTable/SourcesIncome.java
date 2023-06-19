package com.example.kursproject.classesTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SourcesIncome{
    private int id;
    private String name;
    private int income_category_id;
    private String description;
    private String category_name;
    private String subcategory_name;

    public SourcesIncome(int id, String name, int income_category_id, String description) {
        this.id = id;
        this.name = name;
        this.income_category_id = income_category_id;
        this.description = description;
    }

    @Override
    public String toString() {
        return "SourcesIncome{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", income_category_id=" + income_category_id +
                ", description='" + description + '\'' +
                ", category_name='" + category_name + '\'' +
                ", subcategory_name='" + subcategory_name + '\'' +
                '}';
    }

    public SourcesIncome(int id, String name, int income_category_id, String description, String category_name, String subcategory_name) {
        this.id = id;
        this.name = name;
        this.income_category_id = income_category_id;
        this.description = description;
        this.category_name = category_name;
        this.subcategory_name = subcategory_name;
    }

    public SourcesIncome(int id, String name, String category_name, String subcategory_name, String description) {
        this.id = id;
        this.name = name;
        this.category_name = category_name;
        this.subcategory_name = subcategory_name;
        this.description = description;
    }
}
