package com.example.kursproject.classesTable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMembers {
    private int id;
    private String first_name;
    private String last_name;
    private String additional_info;
}
