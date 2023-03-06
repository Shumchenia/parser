package ru.shumchenia.parser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.shumchenia.model.Worker;
import ru.shumchenia.util.WorkerBuilder;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    public static ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    public static Parser parser = new Parser();

    WorkerBuilder workerBuilder=new WorkerBuilder();

    @Test
    void checkParseToJson() throws JsonProcessingException, IllegalAccessException {
        Worker worker=workerBuilder.build();

        String expected =objectMapper.writeValueAsString(worker);
        String actual =parser.parse(worker);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void checkParseToObject() throws JsonProcessingException, NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Worker worker=workerBuilder.build();


        String str=objectMapper.writeValueAsString(worker);
        Worker actual = (Worker) parser.parse(str,Worker.class);

        assertThat(actual).isEqualTo(worker);
    }
}