package com.astrobotsgame.semantics;

import com.astrobotsgame.syntax.*;

import java.util.HashMap;
import java.util.Map;

public class Evaluator implements Term.Visitor<Value> {
    private final Actor actor;
    private final Map<String, Value> environment = new HashMap<String, Value>();
    private final Map<String, Value> stateValues;
    private final Value.Factory values = Value.factory;

    public static Value apply(Actor actor, Map<String, Value> stateValues, Term term) {
        return term.accept(new Evaluator(actor, stateValues));
    }

    private Evaluator(Actor actor, Map<String, Value> stateValues) {
        this.actor = actor;
        this.stateValues = stateValues;
    }

    @Override
    public Value let(String name, Term value, Term body) {
        Value newValue = value.accept(this);
        Value oldValue = environment.get(name);
        environment.put(name, newValue);
        Value result = body.accept(this);
        environment.put(name, oldValue);
        return result;
    }

    @Override
    public Value number(double value) {
        return values.number(value);
    }

    @Override
    public Value binary(Term.BinaryOperator operator, Term left, Term right) {
        Value leftValue = left.accept(this);
        Value rightValue = right.accept(this);
        double l = leftValue.accept(extractNumber);
        double r = rightValue.accept(extractNumber);
        switch(operator) {
            case add: return values.number(l + r);
            case sub: return values.number(l - r);
            case mult: return values.number(l * r);
            case div: return values.number(l / r);
            default: throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    @Override
    public Value variable(String name) {
        Value value = environment.get(name);
        if(value == null) throw new RuntimeException("Unbound variable: " + name);
        return value;
    }

    @Override
    public Value state(String name) {
        Value value = stateValues.get(name);
        if(value == null) throw new RuntimeException("Unbound state variable: " + name);
        return value;
    }

    @Override
    public Value apply(String functionName, Map<String, Term> arguments) {
        Function function = actor.functions.get(functionName);
        if(function == null) throw new RuntimeException("Unbound function: " + functionName);
        Map<String, Value> oldValues = new HashMap<String, Value>();
        Map<String, Value> newValues = new HashMap<String, Value>();
        if(arguments.size() != function.parameters.size()) {
            throw new RuntimeException("Wrong number of arguments");
        }
        for(Map.Entry<String, Term> entry: arguments.entrySet()) {
            oldValues.put(entry.getKey(), environment.get(entry.getKey()));
            newValues.put(entry.getKey(), entry.getValue().accept(this));
            if(!function.parameters.contains(entry.getKey())) {
                throw new RuntimeException("Unknown parameter: " + entry.getKey());
            }
        }
        environment.putAll(newValues);
        Value result = function.body.accept(this);
        environment.putAll(oldValues);
        return result;
    }

    @Override
    public Value record(String typeName, Map<String, Term> fields) {
        Record record = actor.records.get(typeName);
        if(record == null) {
            throw new RuntimeException("Unknown record type: " + typeName);
        }
        if(fields.size() != record.fields.size()) {
            throw new RuntimeException("Wrong number of fields");
        }
        Map<String, Value> fieldValues = new HashMap<String, Value>();
        for(Map.Entry<String, Term> entry: fields.entrySet()) {
            fieldValues.put(entry.getKey(), entry.getValue().accept(this));
            boolean found = false;
            for(Record.Field field: record.fields) {
                found |= field.name.equals(entry.getKey());
            }
            if(!found) {
                throw new RuntimeException("Unknown label: " + entry.getKey());
            }
        }
        return values.record(typeName, fieldValues);
    }

    @Override
    public Value label(final String typeName, final String fieldName, Term term) {
        Record record = actor.records.get(typeName);
        if(record == null) {
            throw new RuntimeException("Unknown record type: " + typeName);
        }
        boolean found = false;
        for(Record.Field field: record.fields) {
            found |= field.name.equals(fieldName);
        }
        if(!found) {
            throw new RuntimeException("Unknown label: " + fieldName);
        }
        return term.accept(this).accept(new Value.Visitor<Value>() {
            @Override
            public Value number(double value) {
                throw new RuntimeException("Record expected");
            }

            @Override
            public Value record(String recordTypeName, Map<String, Value> fields) {
                Value value = fields.get(fieldName);
                if(value == null) {
                    throw new RuntimeException("No such label: " + fieldName);
                }
                if(!typeName.equals(recordTypeName)) {
                    throw new RuntimeException("Wrong record type (found " + recordTypeName + " but expected " + typeName + ")");
                }
                return value;
            }

            @Override
            public Value constructor(String typeName, String constructorName, Value value) {
                throw new RuntimeException("Record expected");
            }
        });
    }

    @Override
    public Value match(final String typeName, final Map<String, Term.Case> cases, Term term) {
        Value value = term.accept(this);
        return value.accept(new Value.Visitor<Value>() {
            @Override
            public Value number(double value) {
                throw new RuntimeException("Expected sum type");
            }

            @Override
            public Value record(String typeName, Map<String, Value> fields) {
                throw new RuntimeException("Expected sum type");
            }

            @Override
            public Value constructor(String sumTypeName, String constructorName, Value value) {
                if(!sumTypeName.equals(typeName)) {
                    throw new RuntimeException("Wrong sum type constructor (found " + sumTypeName + " but expected " + typeName + ")");
                }
                Term.Case target = cases.get(constructorName);
                if(target == null) {
                    throw new RuntimeException("No such constructor: " + constructorName);
                }
                if(target.variableName != null) {
                    Value oldValue = environment.get(target.variableName);
                    Value result = target.body.accept(Evaluator.this);
                    environment.put(target.variableName, oldValue);
                    return result;
                } else {
                    return target.body.accept(Evaluator.this);
                }
            }
        });
    }

    @Override
    public Value constructor(String typeName, String constructorName, Term term) {
        return values.constructor(typeName, constructorName, term.accept(this));
    }

    private Value.Visitor<Double> extractNumber = new Value.Visitor<Double>() {
        @Override
        public Double number(double value) {
            return value;
        }

        @Override
        public Double record(String typeName, Map<String, Value> fields) {
            throw new RuntimeException("Expected number");
        }

        @Override
        public Double constructor(String typeName, String constructorName, Value value) {
            throw new RuntimeException("Expected number");
        }
    };
}
