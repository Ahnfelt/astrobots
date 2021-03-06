package com.astrobotsgame;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.astrobotsgame.semantics.EvaluatorTest;
import com.astrobotsgame.syntax.*;
import com.astrobotsgame.ui.TermWidgetView;
import com.astrobotsgame.ui.TermWidgets;

import java.util.Map;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //setContentView(inputView(termView(EvaluatorTest.actor(), EvaluatorTest.term2()), "Value"));
        //setContentView(overflow(15));
        TermWidgetView view = new TermWidgetView(this);
        TermWidgets.Widget widget = TermWidgets.widget(EvaluatorTest.actor(), EvaluatorTest.term2());
        view.setWidget(widget);
        setContentView(view);
    }

    public View overflow(final int count) {
        if(count <= 0) {
            TextView textView = new TextView(this);
            textView.setText("Bottom");
            return textView;
        } else {
            LinearLayout layout = new LinearLayout(this);
            layout.addView(overflow(count - 1));
            return layout;
        }
    }

    private View termView(final Actor actor, Term term) {
        if(term == null) return null;
        View view = term.accept(new Term.Visitor<View>() {
            private View termView(Term term) {
                return MainActivity.this.termView(actor, term);
            }

            @Override
            public View let(String name, Term value, Term body) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.addView(textView("Let variable"));
                layout.addView(inputView(textView(name, Color.rgb(0, 100, 0)), "Name"));
                layout.addView(textView("be equal to"));
                layout.addView(inputView(termView(value), "Value"));
                layout.addView(textView("within"));
                layout.addView(inputView(termView(body), "Body"));
                return layout;
            }

            @Override
            public View number(double value) {
                return textView("" + value, Color.RED);
            }

            @Override
            public View binary(Term.BinaryOperator operator, Term left, Term right) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                switch(operator) {
                    case add: layout.addView(textView("add")); break;
                    case sub: layout.addView(textView("subtract")); break;
                    case mult: layout.addView(textView("A * B")); break;
                    case div: layout.addView(textView("divide")); break;
                    default: throw new RuntimeException("Unknown operator: " + operator);
                }
                layout.addView(inputView(termView(left), "[A] * B"));
                layout.addView(inputView(termView(right), "A * [B]"));
                return layout;
            }

            @Override
            public View variable(String name) {
                return textView(name, Color.rgb(0, 100, 0));
            }

            @Override
            public View state(String name) {
                return textView(name);
            }

            @Override
            public View apply(String functionName, Map<String, Term> arguments) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                Function function = actor.functions.get(functionName);
                layout.addView(textView(functionName, Color.rgb(0, 50, 100)));
                for(String parameter: function.parameters) {
                    layout.addView(inputView(termView(arguments.get(parameter)), parameter));
                }
                return layout;
            }

            @Override
            public View record(String typeName, Map<String, Term> fields) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                Record type = actor.records.get(typeName);
                layout.addView(textView(typeName, Color.rgb(100, 0, 100)));
                for(Record.Field field: type.fields) {
                    layout.addView(inputView(termView(fields.get(field.name)), field.name));
                }
                return layout;
            }

            @Override
            public View label(String typeName, String fieldName, Term term) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                Record type = actor.records.get(typeName);
                LinearLayout texts = new LinearLayout(MainActivity.this);
                texts.setOrientation(LinearLayout.HORIZONTAL);
                texts.setGravity(Gravity.CENTER);
                texts.addView(textView(typeName + ".", Color.rgb(100, 100, 100)));
                texts.addView(textView(fieldName, Color.rgb(100, 0, 100)));
                layout.addView(texts);
                layout.addView(inputView(termView(term), "Record"));
                return layout;
            }

            @Override
            public View match(String typeName, Map<String, Term.Case> cases, Term term) {
                return textView("<match>");
            }

            @Override
            public View constructor(String typeName, String constructorName, Term term) {
                LinearLayout layout = new LinearLayout(MainActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                SumType type = actor.sumTypes.get(typeName);
                LinearLayout texts = new LinearLayout(MainActivity.this);
                texts.setOrientation(LinearLayout.HORIZONTAL);
                texts.setGravity(Gravity.CENTER);
                texts.addView(textView(typeName + ".", Color.rgb(100, 100, 100)));
                texts.addView(textView(constructorName, Color.rgb(0, 100, 100)));
                layout.addView(texts);
                layout.addView(inputView(termView(term), "Value"));
                return layout;
            }
        });
        view.setTag(term);
        return view;
    }

    private LinearLayout componentLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.construct);
        layout.setPadding(5, 5, 5, 5);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 2, 0, 2);
        layout.setLayoutParams(layoutParams);
        return layout;
    }

    private View textView(String text) {
        return textView(text, Color.BLACK);
    }

    private View textView(String text, int color) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextColor(color);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        return view;
    }

    private View inputView(View view, String name) {
        LinearLayout layout = componentLayout();
        if(view != null) {
            layout.addView(view);
            layout.setOnClickListener(editConstructListener);
            layout.setTag(view.getTag());
            view.setTag(null);
        } else {
            layout.setBackgroundResource(R.drawable.construct_empty);
            TextView textView = new TextView(this);
            textView.setText(name);
            textView.setTextColor(Color.GRAY);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            textView.setPadding(5, 5, 5, 5);
            layout.setOnClickListener(editConstructListener);
            layout.addView(textView);
        }
        return layout;
    }

    private static final View.OnClickListener editConstructListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Term term = (Term) view.getTag();
            if(term != null) {
                // edit
            } else {
                // create
            }
        }
    };
}
