package com.example.moviebooking.customView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.example.moviebooking.R;

public class DiagonalView extends View {
    private Paint paintWhite;
    private Paint paintBlue;

    public DiagonalView(Context context) {
        super(context);
        init();
    }

    public DiagonalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DiagonalView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paintWhite = new Paint();
        paintWhite.setColor(getResources().getColor(R.color.white));
        paintWhite.setStyle(Paint.Style.FILL);

        paintBlue = new Paint();
        paintBlue.setColor(getResources().getColor(R.color.gradient_blue));
        paintBlue.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        canvas.drawRect(0, 0, width, height, paintWhite);

        Path path = new Path();
        path.moveTo(0, height);
        path.lineTo(width, height/2);

        path.lineTo(width, height);
        path.lineTo(0, height);
        path.close();

        canvas.drawPath(path, paintBlue);
    }
}