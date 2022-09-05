package ru.hse.homework4;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


public class ObjectMapper implements Mapper {

    @Override
    public <T> T readFromString(Class<T> clazz, String input) throws UnsupportedClassException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidInputException {
        var parser = new Parser();
        var mapped = parser.parse(input);
        return clazz.cast(Objecter.mapToObject(mapped, clazz));
    }

    @Override
    public <T> T read(Class<T> clazz, InputStream inputStream) throws IOException, UnsupportedClassException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidInputException {
        String input = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        inputStream.close();
        return readFromString(clazz, input);
    }

    @Override
    public <T> T read(Class<T> clazz, File file) throws IOException, UnsupportedClassException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvalidInputException {
        var inputStream = new FileInputStream(file);
        return read(clazz, inputStream);
    }

    @Override
    public String writeToString(Object object) throws IllegalAccessException, UnsupportedClassException {
        return Stringer.objectToString(object, null);
    }

    @Override
    public void write(Object object, OutputStream outputStream) throws IOException, UnsupportedClassException, IllegalAccessException {
        outputStream.write(writeToString(object).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void write(Object object, File file) throws IOException, UnsupportedClassException, IllegalAccessException {
        PrintWriter out = new PrintWriter(file);
        out.print(writeToString(object));
    }
}
