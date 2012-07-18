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
        arguments.put("b", terms.constructor("Maybe", "Just",
                terms.label("Point", "x", null // TODO: Stackoverflow on rendering of "deep" nesting
                //terms.binary(Term.BinaryOperator.mult, terms.variable("x"), null)
                //terms.record("Point", Collections.<String, Term>emptyMap())
                )));
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
        actor.records.put("Point",
                new Record(Collections.<String>emptyList(), Arrays.asList(
                        new Record.Field("x", types.number()),
                        new Record.Field("y", types.number())
                )));
        return actor;
    }

    public static void main(String[] args) {
        Value value = Evaluator.apply(actor(), Collections.<String, Value>emptyMap(), term());
        System.out.println(value);
    }
}
