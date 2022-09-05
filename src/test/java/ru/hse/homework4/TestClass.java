package ru.hse.homework4;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

enum Color {
    R,
    G,
    B
}

@Exported(nullHandling = NullHandling.EXCLUDE)
public class TestClass {
    //Double nulled = null;
    int integer;

    @Ignored
    int ignored;

    @PropertyName("wrapped")
    Integer wrappedInt;

    List<Character> characters;

    @DateFormat("HH++mm--ss")
    LocalTime lt;
    LocalDate ld;
    LocalDateTime ldt;

    String str;

    Color clr;

    public TestClass() {
    }

    void fill() {
        integer = 34;
        ignored = 10;
        wrappedInt = 100;
        characters = new LinkedList<Character>();
        characters.add('D');
        characters.add('E');
        characters.add('A');
        characters.add('D');
        lt = LocalTime.of(2, 2, 2);
        ld = LocalDate.of(2020, 3, 3);
        ldt = LocalDateTime.of(ld, lt);
        str = "CLOAKED";
        clr = Color.G;
    }

}
