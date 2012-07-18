package com.astrobotsgame.syntax;

import java.util.List;
import java.util.Map;

public interface Type {
    public <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        public T number();
        public T variable(String name);
        public T sumType(String name, List<Type> typeParameters);
        public T recordType(String name, List<Type> typeParameters);
        public T functionType(Map<String, Type> parameterTypes, Type returnType);
    }

    public static final Factory factory = new Factory();

    // Boilerplate
    public static class Factory implements Visitor<Type> {
        private int uniqueCounter = 1;

        @Override
        public Type number() {
            return new ShowType() { public <T> T accept(Visitor<T> visitor) { return visitor.number(); } };
        }

        @Override
        public Type variable(final String name) {
            return new ShowType() { public <T> T accept(Visitor<T> visitor) { return visitor.variable(name); } };
        }

        @Override
        public Type sumType(final String name, final List<Type> typeParameters) {
            return new ShowType() { public <T> T accept(Visitor<T> visitor) { return visitor.sumType(name, typeParameters); } };
        }

        @Override
        public Type recordType(final String name, final List<Type> typeParameters) {
            return new ShowType() { public <T> T accept(Visitor<T> visitor) { return visitor.recordType(name, typeParameters); } };
        }

        @Override
        public Type functionType(final Map<String, Type> parameterTypes, final Type returnType) {
            return new ShowType() { public <T> T accept(Visitor<T> visitor) { return visitor.functionType(parameterTypes, returnType); } };
        }

        public Type uniqueVariable() {
            return variable("alpha-" + uniqueCounter++);
        }
    }
}

abstract class ShowType implements Type {
    @Override
    public String toString() {
        return this.accept(new Visitor<String>() {
            @Override
            public String number() {
                return "Number";
            }

            @Override
            public String variable(String name) {
                return name;
            }

            @Override
            public String sumType(String name, List<Type> typeParameters) {
                StringBuilder builder = new StringBuilder();
                builder.append(name);
                for(Type typeParameter: typeParameters) {
                    builder.append(" ");
                    builder.append(typeParameter.accept(this));
                }
                return builder.toString();
            }

            @Override
            public String recordType(String name, List<Type> typeParameters) {
                StringBuilder builder = new StringBuilder();
                builder.append(name);
                for(Type typeParameter: typeParameters) {
                    builder.append(" ");
                    builder.append(typeParameter.accept(this));
                }
                return builder.toString();
            }

            @Override
            public String functionType(Map<String, Type> parameterTypes, Type returnType) {
                StringBuilder builder = new StringBuilder();
                builder.append("(");
                boolean first = true;
                for(Map.Entry<String, Type> entry: parameterTypes.entrySet()) {
                    if(!first) builder.append(", ");
                    builder.append(entry.getKey());
                    builder.append(": ");
                    builder.append(entry.getValue().accept(this));
                    first = false;
                }
                builder.append(") -> ");
                builder.append(returnType.accept(this));
                return builder.toString();
            }
        });
    }
}
