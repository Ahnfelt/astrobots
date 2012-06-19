package com.astrobotsgame.syntax;

import java.util.Map;

public interface Value {
    public <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        public T number(double value);
        public T record(String typeName, Map<String, Value> fields);
        public T constructor(String typeName, String constructorName, Value value);
    }

    public static final Factory factory = new Factory();

    // Boilerplate
    public static class Factory implements Visitor<Value> {
        @Override
        public Value number(final double value) {
            return new AbstractValue() { public <T> T accept(Visitor<T> visitor) { return visitor.number(value); } };
        }

        @Override
        public Value record(final String typeName, final Map<String, Value> fields) {
            return new AbstractValue() { public <T> T accept(Visitor<T> visitor) { return visitor.record(typeName, fields); } };
        }

        @Override
        public Value constructor(final String typeName, final String constructorName, final Value value) {
            return new AbstractValue() { public <T> T accept(Visitor<T> visitor) { return visitor.constructor(typeName, constructorName, value); } };
        }
    }

}

abstract class AbstractValue implements Value {
    @Override
    public String toString() {
        return this.accept(new Visitor<String>() {
            @Override
            public String number(double value) {
                return "" + value;
            }

            @Override
            public String record(String typeName, Map<String, Value> fields) {
                StringBuilder builder = new StringBuilder();
                builder.append(typeName);
                builder.append(" {");
                boolean first = true;
                for(Map.Entry<String, Value> entry: fields.entrySet()) {
                    if(!first) builder.append(", ");
                    builder.append(entry.getKey());
                    builder.append(" = ");
                    builder.append(entry.getValue().accept(this));
                    first = false;
                }
                builder.append('}');
                return builder.toString();
            }

            @Override
            public String constructor(String typeName, String constructorName, Value value) {
                StringBuilder builder = new StringBuilder();
                builder.append(typeName);
                builder.append(" (");
                builder.append(value.accept(this));
                builder.append(')');
                return builder.toString();
            }
        });
    }
}
