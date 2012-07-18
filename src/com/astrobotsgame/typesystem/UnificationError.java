package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Type;

public class UnificationError extends TypeError {
    public final Type type1;
    public final Type type2;

    UnificationError(Type type2, Type type1, String reason) {
        super(reason);
        this.type2 = type2;
        this.type1 = type1;
    }

    public UnificationError(Type type1, Type type2) {
        this(type1, type2, null);
    }
}
