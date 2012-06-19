package com.astrobotsgame.syntax;

import java.util.HashMap;
import java.util.Map;

public class Actor {
    public Map<String, SumType> sumTypes = new HashMap<String, SumType>();
    public Map<String, Record> records = new HashMap<String, Record>();
    public Map<String, State> states = new HashMap<String, State>();
    public Map<String, Function> functions = new HashMap<String, Function>();
    public Function mainFunction;
}
