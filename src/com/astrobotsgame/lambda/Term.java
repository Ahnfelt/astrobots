package com.astrobotsgame.lambda;

import java.util.Map;

public interface Term {
    public static class Case {
        public final String variableName;
        public final Term body;

        public Case(String variableName, Term body) {
            this.variableName = variableName;
            this.body = body;
        }
    }

    public <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        public T number(double value);
        public T variable(String name);
        public T apply(String functionName, Map<String, Term> arguments);
        public T let(String name, Term value, Term body);
        public T remember(String name, Term initial, Term step);
        public T record(String typeName, Map<String, Term> fields);
        public T label(String typeName, String fieldName, Term term);
        public T match(String typeName, Map<String, Case> cases);
        public T constructor(String typeName, String constructorName, Term term);
    }

    public static final Factory factory = new Factory();

    // Boilerplate
    public static class Factory implements Visitor<Term> {
        @Override
        public Term number(final double value) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.number(value); } };
        }

        @Override
        public Term variable(final String name) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.variable(name); } };
        }

        @Override
        public Term let(final String name, final Term value, final Term body) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.let(name, value, body); } };
        }

        @Override
        public Term remember(final String name, final Term initial, final Term step) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.remember(name, initial, step); } };
        }

        @Override
        public Term apply(final String functionName, final Map<String, Term> arguments) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.apply(functionName, arguments); } };
        }

        @Override
        public Term record(final String typeName, final Map<String, Term> fields) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.record(typeName, fields); } };
        }

        @Override
        public Term label(final String typeName, final String fieldName, final Term term) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.label(typeName, fieldName, term); } };
        }

        @Override
        public Term match(final String typeName, final Map<String, Case> cases) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.match(typeName, cases); } };
        }

        @Override
        public Term constructor(final String typeName, final String constructorName, final Term term) {
            return new Term() { public <T> T accept(Visitor<T> visitor) { return visitor.constructor(typeName, constructorName, term); } };
        }
    }
}
