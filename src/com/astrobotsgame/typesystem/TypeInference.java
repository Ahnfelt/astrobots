package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeInference {

    private static Type.Factory types = Type.factory;

    private final Substitution substitutions;
    private final List<TypeError> typeErrors = new ArrayList<TypeError>();
    private final Map<String, SumType> sumTypes;
    private final Map<String, Record> records;
    private final Map<String, State> states;
    private final Map<String, Function> functions;
    private final Unification unifier;

    public TypeInference(Substitution substitutions, Actor actor) {
        this.substitutions = substitutions;
        this.records = actor.records;
        this.sumTypes = actor.sumTypes;
        this.states = actor.states;
        this.functions = actor.functions;
        this.unifier = new Unification(substitutions, typeErrors);
    }

    public Map<String, Type> infer(Map<String, State> states) {
        Map<String, Type> stateEnvironment = new HashMap<String, Type>();
        for (String name: states.keySet()) {
            Type typeVariable = types.uniqueVariable(name);
            stateEnvironment.put(name, typeVariable);
        }

        for (Map.Entry<String, State> entry: states.entrySet()) {
            State state = entry.getValue();
            Type typeVariable = stateEnvironment.get(entry.getKey());
            Type initialType = infer(state.initialValue);
            Type stepType = infer(stateEnvironment, state.step);
            unifier.unify(typeVariable, initialType);
            unifier.unify(initialType, stepType);
            stateEnvironment = substitutions.apply(stateEnvironment);
        }
        return stateEnvironment;
    }

    private Type infer(Value value) {
        return value.accept(new Value.Visitor<Type>() {
            @Override
            public Type number(double value) {
                return types.number();
            }

            @Override
            public Type record(String typeName, Map<String, Value> fields) {
                Record record = records.get(typeName);

                // Instantiate i.e. generate fresh type parameters
                List<Type> freshTypeParameters = new ArrayList<Type>();
                Map<String, Type> instantiateBindings = new HashMap<String, Type>();
                for (String typeVariableName: record.typeVariables) {
                    Type freshTypeVariable = types.uniqueVariable(typeVariableName);
                    instantiateBindings.put(typeVariableName, freshTypeVariable);
                    freshTypeParameters.add(freshTypeVariable);
                }
                Substitution instantiateSubstitution = new Substitution(instantiateBindings);

                List<Type> valueFieldTypes = new ArrayList<Type>();
                List<Type> recordFieldTypes = new ArrayList<Type>();
                for (Map.Entry<String, Value> entry: fields.entrySet()) {
                    Type valueFieldType = instantiateSubstitution.apply(infer(entry.getValue()));
                    Type recordFieldType = instantiateSubstitution.apply(record.fieldTypes.get(entry.getKey()));
                    valueFieldTypes.add(valueFieldType);
                    recordFieldTypes.add(recordFieldType);
                }
                unifier.unify(valueFieldTypes, recordFieldTypes);
                return types.recordType(typeName, freshTypeParameters);
            }

            @Override
            public Type constructor(String typeName, String constructorName, Value value) {
                // TODO
                return null;
            }
        });
    }


    private Type infer(final Map<String, Type> environment, final Term term) {
        return term.accept(new Term.Visitor<Type>() {
            @Override
            public Type let(String name, Term value, Term body) {
                // TODO generalize for let polymorphism.
                Type type1 = infer(environment, value);
                Map<String, Type> environment1 = addBinding(substitutions.apply(environment), name, type1);
                return infer(environment1, body);
            }

            @Override
            public Type number(double value) {
                return types.number();
            }

            @Override
            public Type binary(Term.BinaryOperator operator, Term left, Term right) {
                Type type1 = infer(environment, left);
                unifier.unify(type1, types.number());
                Type type2 = infer(substitutions.apply(environment), right);
                unifier.unify(type2, types.number());
                return types.number();
            }

            @Override
            public Type variable(String name) {
                // TODO instantiate for polymorphism.
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Type state(String name) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Type apply(String functionName, Map<String, Term> arguments) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Type record(String typeName, Map<String, Term> fields) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Type label(String typeName, String fieldName, Term term) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Type match(String typeName, Map<String, Term.Case> cases, Term term) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public Type constructor(String typeName, String constructorName, Term term) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        });
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
}