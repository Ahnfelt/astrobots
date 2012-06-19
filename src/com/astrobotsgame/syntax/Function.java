package com.astrobotsgame.syntax;

import java.util.List;

/**
 * Top-level function declaration.
 */
public class Function {
    public final List<String> parameters;
    public final Term body;

    public Function(Term body, List<String> parameters) {
        this.body = body;
        this.parameters = parameters;
    }
}
