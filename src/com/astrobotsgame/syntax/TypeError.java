package com.astrobotsgame.syntax;

public interface TypeError {
    public <T> T accept(Visitor<T> visitor);

    public interface Visitor<T> {
        public T expectedType(Type type);
        public T unknownIdentifier();
    }

    public static final Factory factory = new Factory();

    // Boilerplate
    public static class Factory implements Visitor<TypeError> {
        @Override
        public TypeError expectedType(final Type type) {
            return new TypeError() {public <T> T accept(Visitor<T> visitor) {return visitor.expectedType(type); } };
        }

        @Override
        public TypeError unknownIdentifier() {
            return new TypeError() {public <T> T accept(Visitor<T> visitor) {return visitor.unknownIdentifier(); } };
        }
    }
}
