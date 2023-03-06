package ru.shumchenia.parser;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.*;

public class Parser {

    public Object parse(String json, Class<?> clas) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        Constructor<?> constructor = clas.getConstructor();
        Object obj = constructor.newInstance();
        Map<String, String> values = getValues(json);
        Field[] fields = getAllFields(obj);

        for (Field field : fields) {
            field.setAccessible(true);
            field.set(obj, getValue(field, values.get(field.getName())));
            field.setAccessible(false);
        }
        return obj;
    }

    private Object getValue(Field field, String s) throws NoSuchFieldException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class<?> type = field.getType();
        if ("null".equals(s)) {
            return null;
        } else if (s.startsWith("{")) {
            return parse(s, field.getType());
        } else if ("java.lang.String".equals(type.getName())) {
            return s.substring(1,s.length()-1);
        } else if ("java.lang.Character".equals(type.getName()) || "char".equals(type.getName())) {
            return s.charAt(1);
        } else if ("java.lang.Boolean".equals(type.getName()) || "boolean".equals(type.getName())) {
            return Boolean.valueOf(s);
        } else if (type.isEnum()) {
            return (Enum.valueOf((Class<? extends Enum>) type, s.substring(1, s.length() - 1)));
        } else if (type.isPrimitive() ||
                type.getSuperclass() != null && "java.lang.Number".equals(type.getSuperclass().getName())) {
            return parseNumber(type, s);
        } else if (type.isArray()) {
            return parseArray(type, s);
        } else {
            return parseCollection(field, s);
        }
    }

    private Collection<Object> parseCollection(Field field, String s) {
        ParameterizedType type1= (ParameterizedType) field.getGenericType();
        return s.lines().map(x -> x.replace("[", ""))
                .map(x -> x.replace("]", ""))
                .map(x -> x.split(","))
                .flatMap(Arrays::stream)
                .map(x -> parseNumber((Class<?>) type1.getActualTypeArguments()[0], x))
                .toList();
    }


    private Object parseArray(Class<?> type, String s) {
        Object[] objects = s.lines()
                .map(x -> x.replace("[", ""))
                .map(x -> x.replace("]", ""))
                .map(x -> x.split(","))
                .flatMap(Arrays::stream)
                .toArray();
        if (type.getSimpleName().startsWith("double")) {
            return Arrays.stream(objects)
                    .mapToDouble(x -> Double.parseDouble((String) s))
                    .toArray();
        } else if (type.getSimpleName().startsWith("short")) {
            return Arrays.stream(objects)
                    .map(x -> Short.parseShort((String) x))
                    .toArray();
        } else if (type.getSimpleName().startsWith("long")) {
            return Arrays.stream(objects)
                    .mapToLong(x -> Long.parseLong((String) s))
                    .toArray();
        } else if (type.getSimpleName().startsWith("byte")) {
            return Arrays.stream(objects)
                    .map(x -> Byte.parseByte((String) x))
                    .toArray();
        } else if (type.getSimpleName().startsWith("int")) {
            return Arrays.stream(objects)
                    .mapToInt(x -> Integer.parseInt((String) x))
                    .toArray();
        }
        return objects;
    }

    private Object parseNumber(Class<?> type, String s) {
        return switch (type.getSimpleName()) {
            case "Short", "short" -> Short.parseShort(s);
            case "Byte", "byte" -> Byte.parseByte(s);
            case "Integer", "int" -> Integer.parseInt(s);
            case "Long", "long" -> Long.parseLong(s);
            case "BigDecimal" -> new BigDecimal(s);
            case "Double", "double" -> Double.parseDouble(s);
            default -> Float.parseFloat(s);
        };
    }

    private Map<String, String> getValues(String json) {
        Map<String, String> values = new HashMap<>();
        char[] chars = json.toCharArray();
        int count = 0;
        int pos = 1;
        for (int i = 1; i < chars.length; i++) {
            if ('{' == chars[i] || '[' == chars[i]) {
                count++;
            }
            if ('}' == chars[i] || ']' == chars[i]) {
                count--;
            }
            if ((',' == chars[i] || i == chars.length - 1) && count < 1) {
                String substring = json.substring(pos, i);
                values.put(substring.substring(1, substring.indexOf("\":")),
                        substring.substring(substring.indexOf(':') + 1));
                pos = i + 1;
            }
        }
        return values;
    }

    public String parse(Object obj) throws IllegalAccessException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        Field[] fields = getAllFields(obj);

        for (Field field : fields) {
            field.setAccessible(true);

            stringBuilder.append(String.format("\"%s\":", field.getName()));

            if (field.get(obj) instanceof Number || field.get(obj) == null || field.get(obj) instanceof Boolean) {
                stringBuilder.append(String.format("%s", field.get(obj)));
            } else if (field.getType().isArray()) {
                stringBuilder.append(String.format("%s", fromArrayToJson(field.get(obj))));
            } else if (field.get(obj) instanceof Collection<?>) {
                stringBuilder.append(String.format("[%s]", fromCollectionToJson((Collection<?>) field.get(obj))));
            } else if (field.get(obj) instanceof String || field.get(obj) instanceof Character
                    || field.getType().isEnum()) {
                stringBuilder.append(String.format("\"%s\"", field.get(obj)));
            } else {
                stringBuilder.append(parse(field.get(obj)));
            }
            stringBuilder.append(",");

            field.setAccessible(false);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    private String fromArrayToJson(Object obj) {
        StringBuilder string = new StringBuilder();
        int len = Array.getLength(obj);
        if (!"[I".equals(obj.getClass().getName())) {
            string.append("[");
            for (int i = 0; i < len - 1; i++) {
                string.append(fromArrayToJson(Array.get(obj, i)));
                string.append(",");
            }
            string.append(fromArrayToJson(Array.get(obj, len - 1)));
            string.append("]");
        } else {
            string.append("[");
            for (int i = 0; i < len - 1; i++) {
                string.append(Array.get(obj, i)).append(",");
            }
            string.append(Array.get(obj, len - 1)).append("]");
        }
        return string.toString();
    }

    private String fromCollectionToJson(Collection<?> obj) {
        return obj.stream()
                .map(x -> {
                            if (x instanceof String || x instanceof Character)
                                return "\"" + x + "\"";
                            return "" + x + "";
                        }
                )
                .reduce((x, x1) -> x + "," + x1)
                .orElseThrow();
    }

    private Field[] getAllFields(Object obj) {
        Class<?> clazz = obj.getClass();
        Field[] fields = obj.getClass().getDeclaredFields();
        while (!clazz.getSimpleName().equals("Object")) {
            clazz = clazz.getSuperclass();
            Field[] fields1 = clazz.getDeclaredFields();
            fields = ArrayUtils.addAll(fields1, fields);
        }
        return fields;
    }
}
