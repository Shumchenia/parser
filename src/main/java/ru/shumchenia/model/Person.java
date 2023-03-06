package ru.shumchenia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    private Gender gender;
    private Integer age;
    private String name;
    private String surName;

}
