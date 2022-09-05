package ru.hse.homework4;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class ObjectMapperTest {

    @Test
    void readFromString() throws UnsupportedClassException, IllegalAccessException, InvalidInputException, InvocationTargetException, InstantiationException, NoSuchMethodException {
        var mp = new ObjectMapper();
        var testClass = new TestClass();
        var parser = new Parser();
        testClass.fill();
        String jsoned = mp.writeToString(testClass);
        var recycled = mp.readFromString(TestClass.class, jsoned);
        assertEquals(recycled.clr, testClass.clr);
        assertEquals(recycled.str, testClass.str);
        assertEquals(recycled.wrappedInt, testClass.wrappedInt);
        assertEquals(recycled.ignored, 0);
        assertEquals(recycled.lt, testClass.lt);
        assertEquals(recycled.ld, testClass.ld);
        assertEquals(recycled.ldt, testClass.ldt);
    }

    @Test
    void writeToString() throws UnsupportedClassException, IllegalAccessException, InvalidInputException {
        var mp = new ObjectMapper();
        var testClass = new TestClass();
        var parser = new Parser();
        testClass.fill();
        String jsoned = mp.writeToString(testClass);
        var mapped = parser.parse(jsoned);
        assertEquals(Integer.toString(testClass.integer), mapped.get("integer"));
        assertNull(mapped.get("ignored"));
        //assertNull(mapped.getOrDefault("nulled", null));
        assertEquals(testClass.wrappedInt.toString(), mapped.get("wrapped"));
        assertEquals("{0=D, 1=E, 2=A, 3=D}", mapped.get("characters").toString());
        assertEquals("02++02--02", mapped.get("lt"));
        assertEquals("2020-03-03", mapped.get("ld"));
        assertEquals("2020-03-03T02:02:02", mapped.get("ldt"));
        assertEquals(testClass.str, mapped.get("str"));
        assertEquals(testClass.clr.toString(), mapped.get("clr"));
    }
}