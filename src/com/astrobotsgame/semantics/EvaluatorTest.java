package com.astrobotsgame.semantics;

import com.astrobotsgame.syntax.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EvaluatorTest {

    private static Term.Factory terms = Term.factory;
    private static Type.Factory types = Type.factory;

    public static Term term() {
        Map<String, Term> arguments = new HashMap<String, Term>();
        arguments.put("a", terms.variable("x"));
        arguments.put("b", terms.number(3));
        return terms.let("x", terms.number(2), terms.apply("add", arguments));
    }

    public static Term term2() {
        Map<String, Term> arguments = new HashMap<String, Term>();
        arguments.put("a", terms.binary(Term.BinaryOperator.mult, terms.variable("x"), null));
        arguments.put("b", terms.constructor("Maybe", "Just", terms.number(2)));
        return terms.let("x", terms.number(2), terms.apply("add", arguments));
    }

    public static Actor actor() {
        Actor actor = new Actor();
        actor.functions.put("add",
                new Function(terms.binary(Term.BinaryOperator.add, terms.variable("a"), terms.variable("b")),
                        Arrays.asList("a", "b")));
        actor.sumTypes.put("Maybe",
                new SumType(Collections.singletonList("a"), Arrays.asList(
                        new SumType.Constructor("Just", types.variable("a"))
                )));
        return actor;
    }

    public static void main(String[] args) {
        Value value = Evaluator.apply(actor(), Collections.<String, Value>emptyMap(), term());
        System.out.println(value);
    }
}
