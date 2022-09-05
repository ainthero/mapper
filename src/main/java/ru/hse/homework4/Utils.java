package ru.hse.homework4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Utils {

    static public boolean isPrimitiveWrapper(Class<?> clazz) {
        return (clazz == Double.class || clazz == Float.class || clazz == Long.class ||
                clazz == Integer.class || clazz == Short.class || clazz == Character.class ||
                clazz == Byte.class || clazz == Boolean.class);

    }

    static public boolean hasParameterlessPublicConstructor(Class<?> clazz) {
        for (var constructor : clazz.getConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }

    static public boolean isDateTime(Class<?> clazz) {
        return (clazz == LocalDateTime.class || clazz == LocalTime.class ||
                clazz == LocalDate.class);
    }
}
