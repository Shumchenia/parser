package ru.shumchenia;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.shumchenia.model.Company;
import ru.shumchenia.model.Gender;
import ru.shumchenia.model.Person;
import ru.shumchenia.model.Worker;
import ru.shumchenia.parser.Parser;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static Company company = new Company("Clever", "Minsk");
    public static Person person = new Worker(Gender.MAN, 19, "Matvei", "Shumchenia", 500.0, company, 9, 'c', new int[]{3, 1, 1}, List.of(1, 2, 3, 4), null, true, false);
    public static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
    public static Parser parser = new Parser();


    public static void main(String[] args) throws JsonProcessingException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        System.out.println(objectMapper.writeValueAsString(person));
        System.out.println(parser.parse(person));

        System.out.println(objectMapper.readValue(objectMapper.writeValueAsString(person), Worker.class));
        //System.out.println(objectMapper.readValue(parser.parse(person), Worker.class));
        Worker parse = (Worker) parser.parse(parser.parse(person), Worker.class);
        System.out.println(parse);

    }
}