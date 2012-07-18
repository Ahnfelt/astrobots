package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Type;

import java.util.*;

public class Unification {

    private static Type.Factory types = Type.factory;
    private final Substitution substitution;
    private final List<TypeError> typeErrors;

    public Unification(Substitution substitution, List<TypeError> typeErrors) {
        this.substitution = substitution;
        this.typeErrors = typeErrors;
    }
    
    private void error(final Type type1, final Type type2) {
        typeErrors.add(new UnificationError(type1, type2));
    }

    private void error(final Type type1, final Type type2, final String reason) {
        typeErrors.add(new UnificationError(type1, type2, reason));
    }

    /**
    * Unify the given two types by returning a substitution.
    */
    public void unify(final Type type1, final Type type2) {
        type1.accept(new Type.Visitor<Void>() {

            @Override
            public Void variable(final String name) {
                substitution.addBinding(name, type2);
                return null;
            }

            @Override
            public Void number() {
                return type2.accept(new Type.Visitor<Void>() {
                    @Override
                    public Void number() {
                        return null;
                    }

                    @Override
                    public Void variable(String name) {
                        substitution.addBinding(name, type1);
                        return null;
                    }

                    @Override
                    public Void sumType(String name, List<Type> typeParameters) {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void recordType(String name, List<Type> typeParameters) {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void functionType(Map<String, Type> parameterTypes, Type returnType) {
                        error(type1, type2);
                        return null;
                    }
                });
            }

            @Override
            public Void sumType(final String name1, final List<Type> typeParameters1) {
                return type2.accept(new Type.Visitor<Void>() {
                    @Override
                    public Void number() {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void variable(String name) {
                        substitution.addBinding(name, type1);
                        return null;
                    }

                    @Override
                    public Void sumType(String name2, List<Type> typeParameters2) {
                        if (!name1.equals(name2)) {
                            error(type1, type2, "Sum types with different names");
                            return null;
                        }
                        if (typeParameters1.size() == typeParameters2.size()) {
                            error(type1, type2, "Sum types with a different number of type parameters");
                            return null;
                        }
                        unify(typeParameters1, typeParameters2);
                        return null;
                    }

                    @Override
                    public Void recordType(String name, List<Type> typeParameters) {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void functionType(Map<String, Type> parameterTypes, Type returnType) {
                        error(type1, type2);
                        return null;
                    }
                });
            }

            @Override
            public Void recordType(final String name1, final List<Type> typeParameters1) {
                return type2.accept(new Type.Visitor<Void>() {
                    @Override
                    public Void number() {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void variable(String name) {
                        substitution.addBinding(name, type1);
                        return null;
                    }

                    @Override
                    public Void sumType(String name, List<Type> typeParameters) {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void recordType(String name2, List<Type> typeParameters2) {
                        if (!name1.equals(name2)) {
                            error(type1, type2, "Record types with different names");
                            return null;
                        }
                        if (typeParameters1.size() == typeParameters2.size()) {
                            error(type1, type2, "Record types with a different number of type parameters");
                            return null;
                        }
                        unify(typeParameters1, typeParameters2);
                        return null;
                    }

                    @Override
                    public Void functionType(Map<String, Type> parameterTypes, Type returnType) {
                        error(type1, type2);
                        return null;
                    }
                });
            }

            @Override
            public Void functionType(final Map<String, Type> parameterTypes1, final Type returnType1) {
                return type2.accept(new Type.Visitor<Void>() {
                    @Override
                    public Void number() {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void variable(String name) {
                        substitution.addBinding(name, type1);
                        return null;
                    }

                    @Override
                    public Void sumType(String name, List<Type> typeParameters) {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void recordType(String name2, List<Type> typeParameters2) {
                        error(type1, type2);
                        return null;
                    }

                    @Override
                    public Void functionType(Map<String, Type> parameterTypes2, Type returnType2) {
                        if (parameterTypes1.size() != parameterTypes2.size()) {
                            error(type1, type2, "Function types with a different number of arguments");
                            return null;
                        }
                        if (intersection(parameterTypes1.keySet(), parameterTypes2.keySet()).size() != parameterTypes1.size()) {
                            error(type1, type2, "Function types with a different argument names");
                            return null;
                        }
                        Collection<Type> types1 = new TreeMap<String, Type>(parameterTypes1).values();
                        Collection<Type> types2 = new TreeMap<String, Type>(parameterTypes2).values();
                        types1.add(returnType1);
                        types1.add(returnType2);
                        unify(types1, types2);
                        return null;
                    }
                });
            }
        });
    }

    public void unify(final Iterable<Type> types1, final Iterable<Type> types2) {
        Iterator<Type> iterator1 = types1.iterator();
        Iterator<Type> iterator2 = types2.iterator();
        while(iterator1.hasNext()) {
            Type typeParameter1 = substitution.apply(iterator1.next());
            Type typeParameter2 = substitution.apply(iterator2.next());
            unify(typeParameter1, typeParameter2);
        }
    }

    private static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        HashSet<T> intersection = new HashSet<T>(set1);
        intersection.removeAll(set2);
        return intersection;
    }
}
