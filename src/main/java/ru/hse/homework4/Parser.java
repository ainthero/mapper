package ru.hse.homework4;

import java.util.*;

public class Parser {

    private int idx = 0;

    enum State {
        START,
        KEY,
        VALUE,
    }

    public Map<String, Object> parse(String input) throws InvalidInputException {
        idx = 0;
        return process(input);
    }

    public Map<String, Object> process(String input) throws InvalidInputException {
        Map<String, Object> res = new HashMap<>();
        State state = State.START;
        String lastKey = null;
        while (idx < input.length()) {
            if (state == State.START) {
                if (input.charAt(idx) == '{') {
                    state = State.KEY;
                } else {
                    throw new InvalidInputException(String.format("At position %d expected {", idx));
                }
            } else if (state == State.KEY) {
                if (input.charAt(idx) == '\"') {
                    int jdx = idx;
                    ++idx;
                    while (input.charAt(idx) != '\"') ++idx;
                    lastKey = input.substring(jdx + 1, idx);
                    ++idx;
                    if (input.charAt(idx) == ':') {
                        state = State.VALUE;
                    } else {
                        throw new InvalidInputException(String.format("At position %d expected : ", idx));
                    }
                } else {
                    throw new InvalidInputException(String.format("At position %d expected \" ", idx));
                }
            } else {
                if (input.charAt(idx) == '{') {
                    var value = process(input);
                    res.put(lastKey, value);
                    ++idx;
                } else if (input.charAt(idx) == '\"') {
                    int jdx = idx;
                    ++idx;
                    while (input.charAt(idx) != '\"') ++idx;
                    res.put(lastKey, input.substring(jdx + 1, idx));
                    ++idx;
                } else {
                    throw new InvalidInputException(String.format("At position %d expected { or \" ", idx));
                }
                if (input.charAt(idx) == '}') {
                    break;
                } else if (input.charAt(idx) == ',') {
                    state = State.KEY;
                } else {
                    throw new InvalidInputException(String.format("At position %d expected } or ' ", idx));
                }
            }
            ++idx;
        }
        return res;
    }
}