package com.astrobotsgame.semantics;

import com.astrobotsgame.syntax.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EvaluatorTest {

    private static Term.Factory terms = Term.factory;
    private static Type.Factory types = Type.factory;
    private static Value.Factory values = Value.factory;

    public static Type unitType() {
        return types.recordType("Unit", Collections.<Type>emptyList());
    }

    public static Record unitRecord() {
        return new Record(Collections.<String>emptyList(), Collections.<Record.Field>emptyList());
    }

    public static SumType maybeSumType() {
        return new SumType(Arrays.asList("a"), Arrays.asList(
                new SumType.Constructor("Nothing", unitType()),
                new SumType.Constructor("Just", types.variable("a"))
        ));
    }

    public static SumType treeSumType() {
        return new SumType(Arrays.asList("a"), Arrays.asList(
                new SumType.Constructor("Leaf", unitType()),
                new SumType.Constructor("Internal", types.recordType("Node", Arrays.asList(types.variable("a"))))
        ));
    }

    public static Record treeNodeType() {
        return new Record(Arrays.asList("a"), Arrays.asList(
                new Record.Field("value", types.variable("a")),
                new Record.Field("left", types.sumType("Tree", Arrays.asList(types.variable("a")))),
                new Record.Field("right", types.sumType("Tree", Arrays.asList(types.variable("a"))))
        ));
    }

    public static Function addFunction() {
        return new Function(
                terms.binary(Term.BinaryOperator.add,
                        terms.variable("a"),
                        terms.variable("b")),
                Arrays.asList("a", "b"));
    }

    public static Function idFunction() {
        return new Function(terms.variable("a"), Arrays.asList("a"));
    }

    public static State iterationCounterState() {
        return new State(
                terms.binary(
                    Term.BinaryOperator.add,
                    terms.state("iterationCount"),
                    terms.number(1)),
                values.number(0));
    }

    public static Function sumTreeFunction() {
        Map<String, Term.Case> cases = new HashMap<String, Term.Case>();
        cases.put("Leaf", new Term.Case("_", terms.number(0)));
        cases.put("Internal", new Term.Case("node",
                terms.binary(Term.BinaryOperator.add,
                        terms.binary(Term.BinaryOperator.add,
                                terms.apply("sumTree", Collections.singletonMap("tree", terms.label("Node", "left", terms.variable("node")))),
                                terms.apply("sumTree", Collections.singletonMap("tree", terms.label("Node", "right", terms.variable("node"))))),
                        terms.apply("sumTree", Collections.singletonMap("tree", terms.label("Node", "value", terms.variable("node"))))
                )));
        return new Function(terms.match("Tree", cases, terms.variable("tree")), Arrays.asList("tree"));
    }


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
        actor.functions.put("add", addFunction());
        actor.functions.put("id", idFunction());
        actor.functions.put("sumTree", sumTreeFunction());
        actor.sumTypes.put("Maybe", maybeSumType());
        actor.sumTypes.put("Tree", treeSumType());
        actor.records.put("Node", treeNodeType());
        actor.records.put("Unit", unitRecord());
        actor.states.put("iterationCount", iterationCounterState());
        return actor;
    }

    public static void main(String[] args) {
        Value value = Evaluator.apply(actor(), Collections.<String, Value>emptyMap(), term());
        System.out.println(value);
    }
}
