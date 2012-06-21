package com.astrobotsgame.syntax;

import java.util.List;

public interface Type {
    public <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        public T number();
        public T variable(String name);
        public T sumType(String name, List<Type> typeParameters);
        public T recordType(String name, List<Type> typeParameters);
    }

    public static final Factory factory = new Factory();

    // Boilerplate
    public static class Factory implements Visitor<Type> {
        private int uniqueCounter = 1;

        @Override
        public Type number() {
            return new Type() { public <T> T accept(Visitor<T> visitor) { return visitor.number(); } };
        }

        @Override
        public Type variable(final String name) {
            return new Type() { public <T> T accept(Visitor<T> visitor) { return visitor.variable(name); } };
        }

        @Override
        public Type sumType(final String name, final List<Type> typeParameters) {
            return new Type() { public <T> T accept(Visitor<T> visitor) { return visitor.sumType(name, typeParameters); } };
        }

        @Override
        public Type recordType(final String name, final List<Type> typeParameters) {
            return new Type() { public <T> T accept(Visitor<T> visitor) { return visitor.recordType(name, typeParameters); } };
        }

        public Type uniqueVariable() {
            return variable("alpha-" + uniqueCounter++);
        }
    }
}
