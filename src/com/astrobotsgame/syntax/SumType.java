package com.astrobotsgame.syntax;

import java.util.List;

public class SumType {
    public class Constructor {
        public final String name;
        public final Type type;

        public Constructor(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }

    public List<String> typeVariables;
    public List<Constructor> constructors;
}
