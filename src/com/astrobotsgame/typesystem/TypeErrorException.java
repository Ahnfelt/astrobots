package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Term;
import com.astrobotsgame.syntax.TypeError;

public class TypeErrorException extends RuntimeException {
    public TypeErrorException(Term term, TypeError typeError) {
    }
}
