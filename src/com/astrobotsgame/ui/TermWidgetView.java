package com.astrobotsgame.ui;

import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.widget.ScrollView;

public class TermWidgetView extends ScrollView {
    private TermWidgets.Widget widget = null;
    private final View view;
    private static final int indentation = 4;

    public TermWidgetView(Context context) {
        super(context);
        setFillViewport(true);
        view = new View(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                if(widget != null) drawWidget(canvas, -this.getScrollY(), 0, widget);
            }
        };
        addView(view);
    }

    public void setWidget(TermWidgets.Widget widget) {
        this.widget = widget;
        view.invalidate();
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);
        view.setPadding(0, top, 0, 0);
        view.invalidate();
    }

    private void drawWidget(Canvas g, int top, int level, TermWidgets.Widget widget) {
        if(top + widget.height < 0 || top >= g.getHeight()) {
            return;
        }

        int bottom =  top + widget.height - 1 - TermWidgets.spacingHeight;

        LinearGradient gradient = new LinearGradient(0, top, 0, bottom,
                Color.rgb(224, 224, 224),
                Color.rgb(192, 192, 192),
                Shader.TileMode.CLAMP);

        RectF rectangle = new RectF(
                level * indentation,
                top,
                g.getWidth() - 1 - level * indentation,
                bottom);
        Paint fill = new Paint();
        fill.setStyle(Paint.Style.FILL);
        fill.setShader(gradient);
        g.drawRoundRect(rectangle, 7, 7, fill);
        Paint stroke = new Paint();
        stroke.setStyle(Paint.Style.STROKE);
        stroke.setAntiAlias(true);
        stroke.setStrokeWidth(1);
        stroke.setColor(Color.rgb(50, 50, 50));
        g.drawRoundRect(rectangle, 7, 7, stroke);

        Typeface typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD);
        Paint fontPaint = new Paint();
        fontPaint.setAntiAlias(true);
        fontPaint.setColor(widget.color);
        fontPaint.setTypeface(Typeface.DEFAULT_BOLD);
        fontPaint.setTextSize(16);
        float prefixWidth = widget.prefix != null ? fontPaint.measureText(widget.prefix) : 0;
        float nameWidth = widget.name != null ? fontPaint.measureText(widget.name) : 0;
        float textStart =  (g.getWidth() - prefixWidth - nameWidth) / 2;
        if(widget.name != null) g.drawText(widget.name, textStart + prefixWidth, top + 22, fontPaint);
        fontPaint.setColor(Color.rgb(100, 100, 100));
        if(widget.prefix != null) g.drawText(widget.prefix, textStart, top + 22, fontPaint);

        int parameterTop = top + TermWidgets.headerHeight;
        int parameterLevel = level + 1;
        for(TermWidgets.Parameter parameter: widget.parameters) {
            drawWidget(g, parameterTop, parameterLevel, parameter.widget);

            LinearGradient parameterGradient = new LinearGradient(
                    0, parameterTop + 5, 0, parameterTop + TermWidgets.headerHeight - 5,
                    Color.rgb(200, 200, 200),
                    Color.rgb(220, 220, 220),
                    Shader.TileMode.CLAMP);

            fontPaint.setColor(Color.rgb(120, 120, 120));
            float labelWidth = parameter.label != null ? fontPaint.measureText(parameter.label) : 0;

            RectF parameterRectangle = new RectF(
                    parameterLevel * indentation + 5,
                    parameterTop + 5,
                    parameterLevel * indentation + 5 + 5 + labelWidth + 5,
                    parameterTop + TermWidgets.headerHeight - 5);
            Paint parameterFill = new Paint();
            parameterFill.setStyle(Paint.Style.FILL);
            parameterFill.setShader(parameterGradient);
            g.drawRoundRect(parameterRectangle, 5, 5, parameterFill);
            Paint parameterStroke = new Paint();
            parameterStroke.setStyle(Paint.Style.STROKE);
            parameterStroke.setAntiAlias(true);
            parameterStroke.setStrokeWidth(1);
            parameterStroke.setColor(Color.rgb(180, 180, 180));
            g.drawRoundRect(parameterRectangle, 5, 5, parameterStroke);
            if(parameter.label != null) g.drawText(parameter.label, parameterLevel * indentation + 5 + 5, parameterTop + 22, fontPaint);

            parameterTop += parameter.widget.height;
        }
    }
}
