package com.astrobotsgame.typesystem;

import com.astrobotsgame.semantics.EvaluatorTest;

public class TypeCheckerTest {

    public static void main(String[] args) {
        System.out.println("add = " + Infer.type(EvaluatorTest.addFunction()));
        System.out.println("id = " + Infer.type(EvaluatorTest.idFunction()));
        System.out.println("sumTree = " + Infer.type(EvaluatorTest.sumTreeFunction()));
    }
}
