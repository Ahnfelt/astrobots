package com.astrobotsgame;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.astrobotsgame.semantics.EvaluatorTest;
import com.astrobotsgame.syntax.Actor;
import com.astrobotsgame.syntax.Function;
import com.astrobotsgame.syntax.Term;

import java.util.Map;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(termView(EvaluatorTest.actor(), EvaluatorTest.term()));
    }

    private View termView(final Actor actor, Term term) {
        return term.accept(new Term.Visitor<View>() {
            private View termView(Term term) {
                return MainActivity.this.termView(actor, term);
            }

            @Override
            public View let(String name, Term value, Term body) {
                LinearLayout layout = componentLayout();
                layout.addView(textView("Let " + name + " ="));
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
                LinearLayout layout = componentLayout();
                layout.addView(inputView(termView(left), "Left operand"));
                switch(operator) {
                    case add: layout.addView(textView("+")); break;
                    case sub: layout.addView(textView("-")); break;
                    case mult: layout.addView(textView("*")); break;
                    case div: layout.addView(textView("/")); break;
                    default: throw new RuntimeException("Unknown operator: " + operator);
                }
                layout.addView(inputView(termView(right), "Right operand"));
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
                return textView("<record>");
            }

            @Override
            public View label(String typeName, String fieldName, Term term) {
                return textView("<label>");
            }

            @Override
            public View match(String typeName, Map<String, Term.Case> cases, Term term) {
                return textView("<match>");
            }

            @Override
            public View constructor(String typeName, String constructorName, Term term) {
                return textView("<constructor>");
            }
        });
    }

    private LinearLayout componentLayout() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundResource(R.drawable.construct);
        layout.setPadding(5, 5, 5, 5);
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
        } else {
            TextView textView = new TextView(this);
            textView.setText(name);
            textView.setTextColor(Color.GRAY);
            textView.setGravity(Gravity.CENTER);
            textView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            layout.addView(textView);
        }
        return layout;
    }
}
