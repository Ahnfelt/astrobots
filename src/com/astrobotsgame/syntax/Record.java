package com.astrobotsgame.syntax;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Record {
    public static class Field {
        public final String name;
        public final Type type;

        public Field(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    public Record(List<String> typeVariables, List<Field> fields) {
        this.typeVariables = typeVariables;
        this.fields = fields;
        fieldTypes = new HashMap<String, Type>();
        for (Field field: fields) {
            fieldTypes.put(field.name, field.type);
        }
    }

    public List<String> typeVariables;
    public List<Field> fields;
    public final Map<String, Type> fieldTypes;
}
