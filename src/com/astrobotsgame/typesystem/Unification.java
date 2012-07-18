package com.astrobotsgame.typesystem;

import com.astrobotsgame.syntax.Type;

import java.util.*;

public class Unification {

    private static Type.Factory types = Type.factory;

    public static class Result {
        public final Map<String, Type> substitution;
        public final List<Error> errors;
        public final static Result empty = new Result(Collections.<String, Type>emptyMap(), Collections.<Error>emptyList());

        private Result(Map<String, Type> substitution, List<Error> errors) {
            this.errors = errors;
            this.substitution = substitution;
        }

        public static Result bind(String variableName, Type type) {
            return new Result(Collections.singletonMap(variableName, type), Collections.<Error>emptyList());
        }

        public static Result error(Error error) {
            return new Result(Collections.<String, Type>emptyMap(), Collections.singletonList(error));
        }

        public static Result error(Type type1, Type type2) {
            return error(new Error(type1, type2));
        }

        public static Result error(Type type1, Type type2, String reason) {
            return error(new Error(type1, type2, reason));
        }

        /**
         * Concatenates the errors and compose the substitutions. Applying the
         * composed substitution corresponds to first applying substitution2
         * and then substitution1.
         */
        public static Result combine(Result result1, Result result2) {
            if (!intersection(result1.substitution.keySet(), result2.substitution.keySet()).isEmpty()) {
                throw new RuntimeException("Trying to combine substitutions with intersection domains.");
            }
            final Map<String, Type> substitution = new HashMap<String, Type>(result1.substitution);
            final List<Error> errors = new ArrayList<Error>(result1.errors);
            for(Map.Entry<String, Type> entry: result2.substitution.entrySet()) {
                substitution.put(entry.getKey(), applySubstitution(result1.substitution, entry.getValue()));
            }
            substitution.putAll(result1.substitution);
            errors.addAll(result2.errors);
            return new Result(substitution, errors);
        }

        public Result combine(Result result) {
            return combine(this, result);
        }
    }

    private static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        HashSet<T> intersection = new HashSet<T>(set1);
        intersection.removeAll(set2);
        return intersection;
    }

    public static class Error {
        public final Type type1;
        public final Type type2;
        public final String reason;

        private Error(Type type2, Type type1, String reason) {
            this.type2 = type2;
            this.type1 = type1;
            this.reason = reason;
        }

        public Error(Type type1, Type type2) {
            this(type1, type2, null);
        }
    }

    public static List<Type> applySubstitution(final Map<String, Type> substitution, final List<Type> types) {
        List<Type> newTypes = new ArrayList<Type>();
        for (Type type: types) {
            newTypes.add(applySubstitution(substitution, type));
        }
        return newTypes;
    }

    public static <A> Map<A, Type> applySubstitution(final Map<String, Type> substitution, final Map<A, Type> types) {
        HashMap<A, Type> newTypes = new HashMap<A, Type>();
        for (Map.Entry<A, Type> entry: types.entrySet()) {
            newTypes.put(entry.getKey(), applySubstitution(substitution, entry.getValue()));
        }
        return newTypes;
    }

    public static Type applySubstitution(final Map<String, Type> substitution, final Type type) {
        return type.accept(new Type.Visitor<Type>() {

            @Override
            public Type number() {
                return type;
            }

            @Override
            public Type variable(String name) {
                Type newType = substitution.get(name);
                if (newType != null) return newType;
                return type;
            }

            @Override
            public Type sumType(String name, List<Type> typeParameters) {
                return types.sumType(name, applySubstitution(substitution, typeParameters));
            }

            @Override
            public Type recordType(String name, List<Type> typeParameters) {
                return types.recordType(name, applySubstitution(substitution, typeParameters));
            }

            @Override
            public Type functionType(Map<String, Type> parameterTypes, Type returnType) {
                return types.functionType(applySubstitution(substitution, parameterTypes), applySubstitution(substitution, returnType));
            }
        });
    }


    /**
    * Unify the given two types by returning a substitution.
    */
    public static Result unify(final Type type1, final Type type2) {
        return type1.accept(new Type.Visitor<Result>() {

            @Override
            public Result variable(final String name) {
                return Result.bind(name, type2);
            }

            @Override
            public Result number() {
                return type2.accept(new Type.Visitor<Result>() {
                    @Override
                    public Result number() {
                        return Result.empty;
                    }

                    @Override
                    public Result variable(String name) {
                        return Result.bind(name, type1);
                    }

                    @Override
                    public Result sumType(String name, List<Type> typeParameters) {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result recordType(String name, List<Type> typeParameters) {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result functionType(Map<String, Type> parameterTypes, Type returnType) {
                        return Result.error(type1, type2);
                    }
                });
            }

            @Override
            public Result sumType(final String name1, final List<Type> typeParameters1) {
                return type2.accept(new Type.Visitor<Result>() {
                    @Override
                    public Result number() {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result variable(String name) {
                        return Result.bind(name, type1);
                    }

                    @Override
                    public Result sumType(String name2, List<Type> typeParameters2) {
                        if (!name1.equals(name2))
                            return Result.error(type1, type2, "Sum types with different names");
                        if (typeParameters1.size() == typeParameters2.size())
                            return Result.error(type1, type2, "Sum types with a different number of type parameters");
                        return unify(typeParameters1, typeParameters2);
                    }

                    @Override
                    public Result recordType(String name, List<Type> typeParameters) {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result functionType(Map<String, Type> parameterTypes, Type returnType) {
                        return Result.error(type1, type2);
                    }
                });
            }

            @Override
            public Result recordType(final String name1, final List<Type> typeParameters1) {
                return type2.accept(new Type.Visitor<Result>() {
                    @Override
                    public Result number() {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result variable(String name) {
                        return Result.bind(name, type1);
                    }

                    @Override
                    public Result sumType(String name, List<Type> typeParameters) {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result recordType(String name2, List<Type> typeParameters2) {
                        if (!name1.equals(name2))
                            return Result.error(type1, type2, "Record types with different names");
                        if (typeParameters1.size() == typeParameters2.size())
                            return Result.error(type1, type2, "Record types with a different number of type parameters");
                        return unify(typeParameters1, typeParameters2);
                    }

                    @Override
                    public Result functionType(Map<String, Type> parameterTypes, Type returnType) {
                        return Result.error(type1, type2);
                    }
                });
            }

            @Override
            public Result functionType(final Map<String, Type> parameterTypes1, final Type returnType1) {
                return type2.accept(new Type.Visitor<Result>() {
                    @Override
                    public Result number() {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result variable(String name) {
                        return Result.bind(name, type1);
                    }

                    @Override
                    public Result sumType(String name, List<Type> typeParameters) {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result recordType(String name2, List<Type> typeParameters2) {
                        return Result.error(type1, type2);
                    }

                    @Override
                    public Result functionType(Map<String, Type> parameterTypes2, Type returnType2) {
                        if (parameterTypes1.size() != parameterTypes2.size())
                            return Result.error(type1, type2, "Function types with a different number of arguments");
                        if (intersection(parameterTypes1.keySet(), parameterTypes2.keySet()).size() != parameterTypes1.size())
                            return Result.error(type1, type2, "Function types with a different argument names");
                        Collection<Type> types1 = new TreeMap<String, Type>(parameterTypes1).values();
                        Collection<Type> types2 = new TreeMap<String, Type>(parameterTypes2).values();
                        types1.add(returnType1);
                        types1.add(returnType2);
                        return unify(types1, types2);
                    }
                });
            }
        });
    }

    public static Result unify(final Iterable<Type> types1, final Iterable<Type> types2) {
        Iterator<Type> iterator1 = types1.iterator();
        Iterator<Type> iterator2 = types2.iterator();
        Result result = Result.empty;
        while(iterator1.hasNext()) {
            Type typeParameter1 = applySubstitution(result.substitution, iterator1.next());
            Type typeParameter2 = applySubstitution(result.substitution, iterator2.next());
            result = result.combine(unify(typeParameter1, typeParameter2));
        }
        return result;
    }
}
