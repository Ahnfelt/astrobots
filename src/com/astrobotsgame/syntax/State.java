package com.astrobotsgame.syntax;

public class State {
    public final Value initialValue;
    public final Term step;

    public State(Term step, Value initialValue) {
        this.step = step;
        this.initialValue = initialValue;
    }
}
