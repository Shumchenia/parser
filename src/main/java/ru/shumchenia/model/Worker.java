package ru.shumchenia.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Data
@NoArgsConstructor
public class Worker extends Person {
    public Double salary;

    private Company company;

    private int i;
    private char c;

    private int[] array;
    //
    private List<Integer> integerList = new ArrayList<>();
    private Double nul;

    private Boolean boolean1;

    private boolean boolean2;

    public Worker(Double salary, Company company, int i, char c, int[] array, List<Integer> integerList, Double nul, Boolean boolean1, boolean boolean2) {
        this.salary = salary;
        this.company = company;
        this.i = i;
        this.c = c;
        this.array = array;
        this.integerList = integerList;
        this.nul = nul;
        this.boolean1 = boolean1;
        this.boolean2 = boolean2;
    }

    public Worker(Gender gender, Integer age, String name, String surName, Double salary, Company company, int i, char c, int[] array, List<Integer> integerList, Double nul, Boolean boolean1, boolean boolean2) {
        super(gender, age, name, surName);
        this.salary = salary;
        this.company = company;
        this.i = i;
        this.c = c;
        this.array = array;
        this.integerList = integerList;
        this.nul = nul;
        this.boolean1 = boolean1;
        this.boolean2 = boolean2;
    }
}
