package com.astrobotsgame.syntax;

import java.util.List;

public class Record {
    public class Field {
        public final String name;
        public final Type type;

        public Field(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    public List<String> typeVariables;
    public List<Field> fields;
}
