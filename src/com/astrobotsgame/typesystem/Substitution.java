package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Type;

import java.util.*;

public class Substitution {

    private static Type.Factory types = Type.factory;

    private Map<String, Type> bindings;

    public Substitution() {
        this(new HashMap<String, Type>());
    }

    public Substitution(Map<String, Type> bindings) {
        this.bindings = bindings;
    }

    public Map<String, Type> getBindings() {
        return Collections.unmodifiableMap(bindings);
    }

    public void addBinding(String variableName, Type type) {
        addBindings(Collections.singletonMap(variableName, type));
    }

    public void addBindings(Map<String, Type> bindings2) {
        Substitution substitution2 = new Substitution(bindings2);
        Map<String, Type> newBindings = new HashMap<String, Type>(bindings2);
        for(Map.Entry<String, Type> entry: bindings.entrySet()) {
            if (bindings2.containsKey(entry.getKey())) {
                throw new RuntimeException("Trying to add biding on type variable '" + entry.getKey() + "' twice");
            }
            newBindings.put(entry.getKey(), substitution2.apply(entry.getValue()));
        }
        bindings = bindings2;
    }

    public Type apply(final Type type) {
        return type.accept(new Type.Visitor<Type>() {

            @Override
            public Type number() {
                return type;
            }

            @Override
            public Type variable(String name) {
                Type newType = bindings.get(name);
                if (newType != null) return newType;
                return type;
            }

            @Override
            public Type sumType(String name, List<Type> typeParameters) {
                return types.sumType(name, apply(typeParameters));
            }

            @Override
            public Type recordType(String name, List<Type> typeParameters) {
                return types.recordType(name, apply(typeParameters));
            }

            @Override
            public Type functionType(Map<String, Type> parameterTypes, Type returnType) {
                return types.functionType(apply(parameterTypes), apply(returnType));
            }
        });
    }

    public List<Type> apply(final List<Type> types) {
        List<Type> newTypes = new ArrayList<Type>();
        for (Type type: types) {
            newTypes.add(apply(type));
        }
        return newTypes;
    }

    public <A> Map<A, Type> apply(final Map<A, Type> types) {
        HashMap<A, Type> newTypes = new HashMap<A, Type>();
        for (Map.Entry<A, Type> entry: types.entrySet()) {
            newTypes.put(entry.getKey(), apply(entry.getValue()));
        }
        return newTypes;
    }
}
