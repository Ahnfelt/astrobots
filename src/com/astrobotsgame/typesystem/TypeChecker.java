package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Term;
import com.astrobotsgame.syntax.Type;
import com.astrobotsgame.syntax.TypeError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeChecker implements Term.Visitor<Type>{

    private static Type.Factory types = Type.factory;
    private TypeError.Factory typeErrors = TypeError.factory;

    private Map<String, Type> environment;
    private final Map<Term, Type> termTypes = new HashMap<Term, Type>();
    private final Map<Term, TypeError> termTypeErrors = new HashMap<Term, TypeError>();
    private final Map<String, Type> substitutions = new HashMap<String, Type>();

    public TypeChecker(Map<String, Type> environment) {
        this.environment = environment;
    }

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
        UnificationError error = unify(actualType, expectedType);
        if (error != null) {
            throw new TypeErrorException(term, typeErrors.expectedType(expectedType));
        }
    }

    public Map<String, Type> getEnvironment() {
        return environment;
    }

    private class UnificationError {
        public final Type type1;
        public final Type type2;

        private UnificationError(Type type2, Type type1) {
            this.type2 = type2;
            this.type1 = type1;
        }
    }

    /**
     * Unify the given two types by substituting in the environment
     */
    private UnificationError unify(final Type type1, final Type type2) {
        return type1.accept(new Type.Visitor<UnificationError>() {

            @Override
            public UnificationError variable(final String name) {
                environment = substitute(name, type2, environment);
                return null;
            }

            @Override
            public UnificationError number() {
                return type2.accept(new Type.Visitor<UnificationError>() {
                    @Override
                    public UnificationError number() {
                        return null;
                    }

                    @Override
                    public UnificationError variable(String name) {
                        environment = substitute(name, type1, environment);
                        return null;
                    }

                    @Override
                    public UnificationError sumType(String name, List<Type> typeParameters) {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError recordType(String name, List<Type> typeParameters) {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError functionType(Map<String, Type> parameterTypes, Type returnType) {
                        return new UnificationError(type1, type2);
                    }
                });
            }

            @Override
            public UnificationError sumType(final String name1, final List<Type> typeParameters1) {
                return type2.accept(new Type.Visitor<UnificationError>() {
                    @Override
                    public UnificationError number() {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError variable(String name) {
                        environment = substitute(name, type1, environment);
                        return null;
                    }

                    @Override
                    public UnificationError sumType(String name2, List<Type> typeParameters2) {
                        if (name1.equals(name2) && typeParameters1.size() == typeParameters2.size()) {
                            for(int i = 0; i < typeParameters1.size(); i++) {
                                UnificationError error = unify(typeParameters1.get(i), typeParameters2.get(i));
                                if (error != null) return error;
                            }
                            return null;
                        } else {
                            return new UnificationError(type1, type2);
                        }
                    }

                    @Override
                    public UnificationError recordType(String name, List<Type> typeParameters) {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError functionType(Map<String, Type> parameterTypes, Type returnType) {
                        return new UnificationError(type1, type2);
                    }
                });
            }

            @Override
            public UnificationError recordType(final String name1, final List<Type> typeParameters1) {
                return type2.accept(new Type.Visitor<UnificationError>() {
                    @Override
                    public UnificationError number() {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError variable(String name) {
                        environment = substitute(name, type1, environment);
                        return null;
                    }

                    @Override
                    public UnificationError sumType(String name, List<Type> typeParameters) {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError recordType(String name2, List<Type> typeParameters2) {
                        if (name1.equals(name2) && typeParameters1.size() == typeParameters2.size()) {
                            for(int i = 0; i < typeParameters1.size(); i++) {
                                UnificationError error = unify(typeParameters1.get(i), typeParameters2.get(i));
                                if (error != null) return error;
                            }
                            return null;
                        } else {
                            return new UnificationError(type1, type2);
                        }
                    }

                    @Override
                    public UnificationError functionType(Map<String, Type> parameterTypes, Type returnType) {
                        return new UnificationError(type1, type2);
                    }
                });
            }

            @Override
            public UnificationError functionType(final Map<String, Type> parameterTypes, final Type returnType) {
                return type2.accept(new Type.Visitor<UnificationError>() {
                    @Override
                    public UnificationError number() {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError variable(String name) {
                        environment = substitute(name, type1, environment);
                        return null;
                    }

                    @Override
                    public UnificationError sumType(String name, List<Type> typeParameters) {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError recordType(String name2, List<Type> typeParameters2) {
                        return new UnificationError(type1, type2);
                    }

                    @Override
                    public UnificationError functionType(Map<String, Type> parameterTypes2, Type returnType2) {
                        if (parameterTypes.size() != parameterTypes2.size()) return new UnificationError(type1, type2);
                        for(String parameter: parameterTypes.keySet()) {
                            Type type1 = parameterTypes.get(parameter);
                            Type type2 = parameterTypes2.get(parameter);
                            if (type2 == null) return new UnificationError(type1, type2);
                            UnificationError error = unify(type1, type2);
                            if (error != null) return error;
                        }
                        // TODO apply substitution on return types before unification
                        return unify(returnType, returnType2);
                    }
                });
            }
        });
    }

    /**
     * Replace all type variables with the given name by replaceType in type.
     */
    private static Type substitute(final String searchTypeVariableName, final Type replaceType, final Type type) {
        return type.accept(new Type.Visitor<Type>() {
            @Override
            public Type number() {
                return type;
            }

            @Override
            public Type variable(String name) {
                if (name.equals(searchTypeVariableName)) return replaceType;
                else return type;
            }

            @Override
            public Type sumType(String name, List<Type> typeParameters) {
                List<Type> newTypeParameters = new ArrayList<Type>();
                for (Type typeParameter: typeParameters) {
                    Type newTypeParameter = substitute(searchTypeVariableName, replaceType, typeParameter);
                    newTypeParameters.add(newTypeParameter);
                }
                return types.sumType(name, newTypeParameters);
            }

            @Override
            public Type recordType(String name, List<Type> typeParameters) {
                List<Type> newTypeParameters = new ArrayList<Type>();
                for (Type typeParameter: typeParameters) {
                    Type newTypeParameter = substitute(searchTypeVariableName, replaceType, typeParameter);
                    newTypeParameters.add(newTypeParameter);
                }
                return types.recordType(name, newTypeParameters);
            }

            @Override
            public Type functionType(Map<String, Type> parameterTypes, Type returnType) {
                Map<String, Type> newParameterTypes = new HashMap<String, Type>();
                for (String parameter: parameterTypes.keySet()) {
                    Type newParameterType = substitute(searchTypeVariableName, replaceType, parameterTypes.get(parameter));
                    newParameterTypes.put(parameter, newParameterType);
                }
                Type newReturnType = substitute(searchTypeVariableName, replaceType, returnType);
                return types.functionType(parameterTypes, returnType);
            }
        });
    }

    /**
     * Replace all type variables with the given name by replaceType in environment.
     */
    private static Map<String, Type> substitute(final String searchTypeVariableName, final Type replaceType, final Map<String, Type> environment) {
        final Map<String, Type> newEnvironment = new HashMap<String, Type>();
        for (String name: environment.keySet()) {
            Type newType = substitute(searchTypeVariableName, replaceType, environment.get(name));
            newEnvironment.put(name, newType);
        }
        return newEnvironment;
    }
}
