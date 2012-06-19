package com.astrobotsgame.semantics;

import com.astrobotsgame.syntax.Actor;
import com.astrobotsgame.syntax.Function;
import com.astrobotsgame.syntax.Term;
import com.astrobotsgame.syntax.Value;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EvaluatorTest {

    private static Term.Factory terms = Term.factory;

    public static void main(String[] args) {
        Actor actor = new Actor();
        actor.functions.put("add",
                new Function(terms.binary(Term.BinaryOperator.add, terms.variable("a"), terms.variable("b")),
                Arrays.asList("a", "b")));

        Map<String, Term> arguments = new HashMap<String, Term>();
        arguments.put("a", terms.variable("x"));
        arguments.put("b", terms.number(3));
        Term let = terms.let("x", terms.number(2), terms.apply("add", arguments));

        Value value = Evaluator.apply(actor, Collections.<String, Value>emptyMap(), let);
        System.out.println(value);
    }
}
