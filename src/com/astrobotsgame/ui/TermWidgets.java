package com.astrobotsgame.ui;

import android.graphics.Color;
import com.astrobotsgame.syntax.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class TermWidgets {
    public static class Parameter {
        public final String label;
        public final Widget widget;

        public Parameter(String label, Widget widget) {
            this.label = label;
            this.widget = widget;
        }
    }

    public static class Widget {
        public final Term term;
        public final int height;
        public final String prefix;
        public final String name;
        public final int color;
        public final List<Parameter> parameters;

        public Widget(Term term, int height, String prefix, String name, int color, List<Parameter> parameters) {
            this.term = term;
            this.height = height;
            this.prefix = prefix;
            this.name = name;
            this.color = color;
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("{");
            builder.append(name);
            builder.append(":[");
            for(Parameter parameter: parameters) {
                builder.append(parameter.widget.toString());
            }
            builder.append("]}");
            return builder.toString();
        }
    }

    public static final int headerHeight = 33;
    public static final int spacingHeight = 5;

    public static Widget widget(final Actor actor, final Term term) {
        return term.accept(new Term.Visitor<Widget>() {
            private Widget widget(Term term) {
                if(term == null) return build(null, null, null, Color.TRANSPARENT);
                return TermWidgets.widget(actor, term);
            }

            @Override
            public Widget let(String name, Term value, Term body) {
                return build(term, null, "Define variable", Color.BLACK,
                        new Parameter("variable", build(null, null, name, Color.rgb(0, 100, 0))),
                        new Parameter("value", widget(value)),
                        new Parameter("in", widget(body)));
            }

            @Override
            public Widget number(double value) {
                return build(term, null, "" + value, Color.RED);
            }

            @Override
            public Widget binary(Term.BinaryOperator operator, Term left, Term right) {
                String op;
                switch(operator) {
                    case add: op = "+"; break;
                    case sub: op = "-"; break;
                    case mult: op = "*"; break;
                    case div: op = "/"; break;
                    default: throw new RuntimeException("Unknown operator: " + operator);
                }
                return build(term, null, "a " + op + " b", Color.BLACK,
                        new Parameter("a " + op, widget(left)),
                        new Parameter(op + " b", widget(right)));
            }

            @Override
            public Widget variable(String name) {
                return build(term, null, name, Color.rgb(0, 100, 0));
            }

            @Override
            public Widget state(String name) {
                return build(term, null, name, Color.rgb(0, 0, 255));
            }

            @Override
            public Widget apply(String functionName, Map<String, Term> arguments) {
                Function function = actor.functions.get(functionName);
                List<Parameter> parameters = new LinkedList<Parameter>();
                for(String parameter: function.parameters) {
                    parameters.add(new Parameter(parameter, widget(arguments.get(parameter))));
                }
                return build(term, null, functionName, Color.rgb(0, 50, 100), parameters);
            }

            @Override
            public Widget record(String typeName, Map<String, Term> fields) {
                Record record = actor.records.get(typeName);
                List<Parameter> parameters = new LinkedList<Parameter>();
                for(Record.Field field: record.fields) {
                    parameters.add(new Parameter(field.name, widget(fields.get(field.name))));
                }
                return build(term, null, typeName, Color.rgb(100, 0, 100), parameters);
            }

            @Override
            public Widget label(String typeName, String fieldName, Term term) {
                Record type = actor.records.get(typeName);
                return build(term, typeName + ".", fieldName, Color.rgb(100, 0, 100),
                        new Parameter("record", widget(term)));
            }

            @Override
            public Widget match(String typeName, Map<String, Term.Case> cases, Term term) {
                return build(null, null, "<match>", Color.BLACK);
            }

            @Override
            public Widget constructor(String typeName, String constructorName, Term term) {
                SumType type = actor.sumTypes.get(typeName);
                return build(term, typeName + ".", constructorName, Color.rgb(0, 100, 100),
                        new Parameter("value", widget(term)));
            }

            private Widget build(Term term, String prefix, String name, int color, Parameter... parameters) {
                return build(term, prefix, name, color, Arrays.asList(parameters));
            }

            private Widget build(Term term, String prefix, String name, int color, List<Parameter> parameters) {
                int height = headerHeight;
                for(Parameter parameter: parameters) {
                    height += parameter.widget.height;
                }
                height += spacingHeight;
                return new Widget(term, height, prefix, name, color, parameters);
            }
        });
    }
}
