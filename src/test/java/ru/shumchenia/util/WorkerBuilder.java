package ru.shumchenia.util;

import ru.shumchenia.model.Company;
import ru.shumchenia.model.Gender;
import ru.shumchenia.model.Person;
import ru.shumchenia.model.Worker;

import java.util.List;

public class WorkerBuilder {
    private Company company = new Company("Clever", "Minsk");

    public Worker build(){
        return new Worker(Gender.MAN, 19, "Matvei", "Shumchenia", 500.0, company, 9, 'c',
                new int[]{3, 1, 1}, List.of(1, 2, 3, 4), null, true, false);
    }
}
