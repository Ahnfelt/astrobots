package com.astrobotsgame.syntax;

import java.util.List;

public class SumType {
    public static class Constructor {
        public final String name;
        public final Type type;

        public Constructor(String name, Type type) {
            this.name = name;
            this.type = type;
        }
    }


    public SumType(List<String> typeVariables, List<Constructor> constructors) {
        this.typeVariables = typeVariables;
        this.constructors = constructors;
    }

    public final List<String> typeVariables;
    public final List<Constructor> constructors;
}
