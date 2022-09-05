package ru.hse.homework4;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static ru.hse.homework4.Utils.*;

public class Stringer {

    public static String objectToString(Object obj, Field field) throws IllegalAccessException, UnsupportedClassException {
        if (obj.getClass() == String.class) {
            return quoted(obj.toString());
        }
        if (obj.getClass() == LocalDate.class || obj.getClass() == LocalTime.class ||
                obj.getClass() == LocalDateTime.class) {
            return dateTimeToString((TemporalAccessor) obj, field);
        }
        if (obj.getClass().isPrimitive() || isPrimitiveWrapper(obj.getClass()) ||
                obj.getClass().isEnum()) {
            return primitiveToString(obj);
        }
        if (obj instanceof List || obj instanceof Set) {
            return collectionToString((Collection<?>) obj);
        }
        if (obj.getClass().getSuperclass() == Object.class &&
                hasParameterlessPublicConstructor(obj.getClass())) {
            return customClassToString(obj);
        }
        return null;
    }

    private static String quoted(String str) {
        return '"' + str + '"';
    }

    private static String customClassToString(Object obj) throws IllegalAccessException, UnsupportedClassException {
        if (obj.getClass().getAnnotation(Exported.class) == null) {
            throw new UnsupportedClassException("Class should be marked Exported");
        }
        Field[] fields = obj.getClass().getDeclaredFields();
        StringBuilder res = new StringBuilder("{");
        for (var field : fields) {
            if (Modifier.isStatic(field.getModifiers()) ||
                    field.isSynthetic() ||
                    field.getAnnotation(Ignored.class) != null) {
                continue;
            }
            field.setAccessible(true);
            var value = field.get(obj);
            String stringedObject;
            if (value == null) {
                if (obj.getClass().getAnnotation(Exported.class).nullHandling() == NullHandling.EXCLUDE) {
                    continue;
                } else {
                    stringedObject = "null";
                }
            } else {
                stringedObject = objectToString(value, field);
                if (stringedObject == null) {
                    throw new UnsupportedClassException(String.format("%s class is unsupported",
                            value.getClass()));
                }
            }
            String fieldName;
            if (field.getAnnotation(PropertyName.class) == null) {
                fieldName = quoted(field.getName());
            } else {
                fieldName = quoted(field.getAnnotation(PropertyName.class).value());
            }
            res.append(fieldName).append(":").append(stringedObject).append(",");
        }
        res.deleteCharAt(res.length() - 1);
        res.append("}");
        return res.toString();
    }

    private static String primitiveToString(Object obj) {
        return quoted(obj.toString());
    }

    private static String collectionToString(Collection<?> collection) throws IllegalAccessException, UnsupportedClassException {
        StringBuilder res = new StringBuilder("{");
        int idx = 0;
        for (var obj : collection) {
            res.append(quoted(Integer.toString(idx))).append(":");
            res.append(objectToString(obj, null)).append(",");
            ++idx;
        }
        res.deleteCharAt(res.length() - 1);
        res.append("}");
        return res.toString();
    }

    private static String dateTimeToString(TemporalAccessor obj, Field field) {
        if (field == null) {
            return quoted(obj.toString());
        }
        var annotation = field.getAnnotation(DateFormat.class);
        if (annotation == null) {
            return quoted(obj.toString());
        } else {
            return quoted(DateTimeFormatter.ofPattern(annotation.value()).format(obj));
        }
    }
}
