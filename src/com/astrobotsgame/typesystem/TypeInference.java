package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.*;

import java.util.HashMap;
import java.util.Map;
import static com.astrobotsgame.typesystem.Unification.applySubstitution;

public class TypeInference {

    private static Type.Factory types = Type.factory;
    private static TypeError.Factory typeErrors = TypeError.factory;

    private final Map<String, SumType> sumTypes = new HashMap<String, SumType>();
    private final Map<String, Record> records = new HashMap<String, Record>();
    private final Map<String, State> states = new HashMap<String, State>();
    private final Map<String, Function> functions = new HashMap<String, Function>();

    private final Map<String, Type> environment;
    private final Map<Term, Type> termTypes = new HashMap<Term, Type>();
    private final Map<Term, TypeError> termTypeErrors = new HashMap<Term, TypeError>();
    private final Map<String, Type> substitutions = new HashMap<String, Type>();

    public TypeInference(Map<String, Type> environment) {
        this.environment = environment;
    }

    public Type typeInference(Term term) {
        Bundle bundle = infer(environment, term);
        return applySubstitution(bundle.result.substitution, bundle.type);
    }


    private static class Bundle {
        public final Type type;
        public final Unification.Result result;

        private Bundle(Unification.Result result, Type type) {
            this.result = result;
            this.type = type;
        }
    }

    private static Map<String, Type> addBinding(Map<String, Type> map, String name, Type type) {
        HashMap<String, Type> newMap = new HashMap<String, Type>(map);
        newMap.put(name, type);
        return newMap;
    }

    private static Map<String, Type> removeBinding(Map<String, Type> map, String name) {
        HashMap<String, Type> newMap = new HashMap<String, Type>(map);
        newMap.remove(name);
        return newMap;
    }

    private Bundle infer(final Map<String, Type> environment, final Term term) {
        return term.accept(new Term.Visitor<Bundle>() {
            @Override
            public Bundle let(String name, Term value, Term body) {
                // TODO generalize for let polymorphism.
                Bundle bundle1 = infer(environment, value);
                Map<String, Type> environment1 = addBinding(applySubstitution(bundle1.result.substitution, environment), name, bundle1.type);
                Bundle bundle2 = infer(environment1, body);
                return new Bundle(bundle1.result.combine(bundle2.result), bundle2.type);
            }

            @Override
            public Bundle number(double value) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle binary(Term.BinaryOperator operator, Term left, Term right) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle variable(String name) {
                // TODO instantiate for polymorphism.
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle state(String name) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle apply(String functionName, Map<String, Term> arguments) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle record(String typeName, Map<String, Term> fields) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle label(String typeName, String fieldName, Term term) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle match(String typeName, Map<String, Term.Case> cases, Term term) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Bundle constructor(String typeName, String constructorName, Term term) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }
}