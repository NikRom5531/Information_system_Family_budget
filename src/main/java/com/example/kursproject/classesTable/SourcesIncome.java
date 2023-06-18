package com.example.kursproject.classesTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SourcesIncome {
    private int id;
    private String name;
    private int income_category_id;
    private String description;
}
