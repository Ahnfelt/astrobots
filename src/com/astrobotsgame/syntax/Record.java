package com.astrobotsgame.syntax;

import java.util.List;

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
    }

    public final List<String> typeVariables;
    public final List<Field> fields;
}
