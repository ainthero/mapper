package ru.hse.homework4;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static ru.hse.homework4.Utils.*;

public class Objecter {

    public static Object mapToObject(Map<String, Object> map, Class<?> clazz) throws InstantiationException,
            IllegalAccessException, NoSuchMethodException, InvocationTargetException, UnsupportedClassException {
        if (clazz.getAnnotation(Exported.class) == null) {
            throw new UnsupportedClassException("Class should be marked Exported");
        }
        var constructor = clazz.getConstructor();
        Object object = constructor.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) ||
                    field.isSynthetic() ||
                    field.getAnnotation(Ignored.class) != null) {
                continue;
            }
            String fieldName;
            if (field.getAnnotation(PropertyName.class) == null) {
                fieldName = field.getName();
            } else {
                fieldName = field.getAnnotation(PropertyName.class).value();
            }
            var key = map.get(fieldName);
            field.setAccessible(true);
            if (key == null) {
                field.set(object, null);
            } else {
                var value = generalToObject(field, key);
                if (value == null) {
                    if (clazz.getAnnotation(Exported.class).unknownPropertiesPolicy() ==
                            UnknownPropertiesPolicy.FAIL) {
                        throw new UnsupportedClassException("Unknown class");
                    }
                }
                field.set(object, value);
            }
        }
        return object;
    }

    //unchecked cast нужны для реализации правильной обработки словаря бесконечной вложенности
    @SuppressWarnings({"unchecked"})
    private static Object generalToObject(Field field, Object val) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, UnsupportedClassException {
        if (val instanceof Map) {
            if (List.class.isAssignableFrom(field.getType())) {
                return mapToList((ParameterizedType) field.getGenericType(), (Map<String, Object>) val);
            } else if (Set.class.isAssignableFrom(field.getType())) {
                return mapToSet((ParameterizedType) field.getGenericType(), (Map<String, Object>) val);
            } else {
                return mapToObject((Map<String, Object>) val, field.getType());
            }
        }
        if (isDateTime(field.getType())) {
            return dateTimeToObject((String) val, field);
        }
        if (val.getClass() == String.class) {
            return simpleToObject((String) val, field.getType());
        }
        return null;
    }

    //unchecked cast нужны для реализации правильной обработки словаря бесконечной вложенности
    @SuppressWarnings({"unchecked"})
    private static Object generalClassToObject(Class<?> clazz, Object val) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, UnsupportedClassException {
        if (val instanceof Map) {
            return mapToObject((Map<String, Object>) val, clazz);
        }
        if (isDateTime(clazz)) {
            return dateTimeToObject((String) val, clazz);
        }
        if (val.getClass() == String.class) {
            return simpleToObject((String) val, clazz);
        }
        return null;
    }

    private static List<?> mapToList(ParameterizedType type, Map<String, Object> map) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, UnsupportedClassException {
        List<Object> list = new LinkedList<>();
        for (int idx = 0; idx < map.size(); ++idx) {
            var parameterType = (Class<?>) type.getActualTypeArguments()[0];
            list.add(generalClassToObject(parameterType, map.get(Integer.toString(idx))));
        }
        return list;
    }

    private static Set<?> mapToSet(ParameterizedType type, Map<String, Object> map) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, UnsupportedClassException {
        Set<Object> set = new HashSet<>();
        for (int idx = 0; idx < map.size(); ++idx) {
            var parameterType = (Class<?>) type.getActualTypeArguments()[0];
            set.add(generalClassToObject(parameterType, map.get(Integer.toString(idx))));
        }
        return set;
    }

    private static Object primitiveToObject(Class<?> clazz, String str) {
        if (clazz == Boolean.TYPE || clazz == Boolean.class) return Boolean.parseBoolean(str);
        if (clazz == Byte.TYPE || clazz == Byte.class) return Byte.parseByte(str);
        if (clazz == Short.TYPE || clazz == Short.class) return Short.parseShort(str);
        if (clazz == Integer.TYPE || clazz == Integer.class) return Integer.parseInt(str);
        if (clazz == Long.TYPE || clazz == Long.class) return Long.parseLong(str);
        if (clazz == Float.TYPE || clazz == Float.class) return Float.parseFloat(str);
        if (clazz == Double.TYPE || clazz == Double.class) return Double.parseDouble(str);
        return null;
    }

    private static Object dateTimeToObject(String str, Field field) {
        if (field.getType() == LocalDate.class) {
            var annotation = field.getAnnotation(DateFormat.class);
            if (annotation == null) return LocalDate.parse(str);
            else return LocalDate.parse(str, DateTimeFormatter.ofPattern(annotation.value()));
        }
        if (field.getType() == LocalTime.class) {
            var annotation = field.getAnnotation(DateFormat.class);
            if (annotation == null) return LocalTime.parse(str);
            else return LocalTime.parse(str, DateTimeFormatter.ofPattern(annotation.value()));
        }
        if (field.getType() == LocalDateTime.class) {
            var annotation = field.getAnnotation(DateFormat.class);
            if (annotation == null) return LocalDateTime.parse(str);
            else return LocalDateTime.parse(str, DateTimeFormatter.ofPattern(annotation.value()));
        }
        return null;
    }

    private static Object dateTimeToObject(String str, Class<?> clazz) {
        if (clazz == LocalDate.class) {
            return LocalDate.parse(str);
        }
        if (clazz == LocalTime.class) {
            return LocalTime.parse(str);
        }
        if (clazz == LocalDateTime.class) {
            return LocalDateTime.parse(str);
        }
        return null;
    }

    //я не знаю почему ругается и как подругому
    @SuppressWarnings({"unchecked"})
    private static Object simpleToObject(String str, Class<?> clazz) {
        if (str.equals("null")) {
            return null;
        }
        if (clazz == String.class) {
            return str;
        }
        if (clazz.isPrimitive() || isPrimitiveWrapper(clazz)) {
            return primitiveToObject(clazz, str);
        }
        if (clazz.isEnum()) {
            return Enum.valueOf((Class<Enum>) clazz, str);
        }
        if (isDateTime(clazz)) {
            return dateTimeToObject(str, clazz);
        }
        return null;
    }
}
