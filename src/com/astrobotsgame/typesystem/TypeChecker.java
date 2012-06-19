package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Term;
import com.astrobotsgame.syntax.Type;
import com.astrobotsgame.syntax.TypeError;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeChecker implements Term.Visitor<Type>{

    private Type.Factory types = Type.factory;
    private TypeError.Factory typeErrors = TypeError.factory;

    private final Map<String, Type> environment = new HashMap<String, Type>();
    private final Map<Term, Type> termTypes = new HashMap<Term, Type>();
    private final Map<Term, TypeError> termTypeErrors = new HashMap<Term, TypeError>();
    private final Map<String, Type> substitutions = new HashMap<String, Type>();

    @Override
    public Type let(String name, Term value, Term body) {
        Type valueType = value.accept(this);
        Type oldType = environment.get(name);
        environment.put(name, valueType);
        Type bodyType = body.accept(this);
        environment.put(name, oldType);
        return bodyType;
    }

    @Override
    public Type number(double value) {
        return types.number();
    }

    @Override
    public Type binary(Term.BinaryOperator operator, Term left, Term right) {
        expect(left, types.number());
        expect(right, types.number());
        return types.number();
    }

    @Override
    public Type variable(String name) {
        Type type = environment.get(name);
        if (type == null) {
            // TODO
            throw new TypeErrorException(null, typeErrors.unknownIdentifier());
        } else {
            return type;
        }
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

    private void expect(Term term, Type expectedType) {
        Type actualType = term.accept(this);
        TypeError typeError = unify(actualType, expectedType);
        if (typeError != null) {
            throw new TypeErrorException(term, typeError);
        }
    }

    // TODO
    private void substitute(final String typeVariableName, final Type newType) {
        final Map<String, Type> substitutions = new HashMap<String, Type>();
        for (String variableName: environment.keySet()) {
            final Type type = environment.get(variableName);
            type.accept(new Type.Visitor<Void>() {
                @Override
                public Void number() {
                    return null;
                }

                @Override
                public Void variable(String name) {
                    if (name.equals(typeVariableName)) {
                        substitutions.put(name, newType);
                    }
                    return null;
                }

                @Override
                public Void sumType(String name, List<Type> typeParameters) {
                    return null;
                }

                @Override
                public Void recordType(String name, List<Type> typeParameters) {
                    return null;
                }
            });
        }
        environment.putAll(substitutions);
    }


    private TypeError unify(final Type actualType, final Type expectedType) {
        return actualType.accept(new Type.Visitor<TypeError>() {
            @Override
            public TypeError number() {
                return expectedType.accept(new Type.Visitor<TypeError>() {
                    @Override
                    public TypeError number() {
                        return null;
                    }

                    @Override
                    public TypeError variable(String name) {
                        substitute(name, actualType);
                        return null;
                    }

                    @Override
                    public TypeError sumType(String name, List<Type> typeParameters) {
                        return typeErrors.expectedType(types.number());
                    }

                    @Override
                    public TypeError recordType(String name, List<Type> typeParameters) {
                        return typeErrors.expectedType(types.number());
                    }
                });
            }

            @Override
            public TypeError variable(String name) {
                return null;
            }

            @Override
            public TypeError sumType(String name, List<Type> typeParameters) {
                return null;
            }

            @Override
            public TypeError recordType(String name, List<Type> typeParameters) {
                return null;
            }
        });
    }
}
