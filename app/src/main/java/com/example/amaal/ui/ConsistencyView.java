package com.example.amaal.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ConsistencyView extends View {

    private final Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<Float> dailyScores = new ArrayList<>(); 

    public ConsistencyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        barPaint.setColor(0xFF1976D2); 
        barPaint.setStyle(Paint.Style.FILL);
        
        axisPaint.setColor(0xFFE0E0E0);
        axisPaint.setStrokeWidth(2f);
    }

    public void setScores(List<Float> scores) {
        this.dailyScores = scores;
        invalidate(); 
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dailyScores.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();
        float barWidth = (width / dailyScores.size()) * 0.6f; 
        float spacing = (width / dailyScores.size()) * 0.4f;

        canvas.drawLine(0, height, width, height, axisPaint);

        float currentX = spacing / 2;

        for (Float score : dailyScores) {
            float barHeight = score * height;
            canvas.drawRect(currentX, height - barHeight, currentX + barWidth, height, barPaint);
            currentX += barWidth + spacing;
        }
    }
}