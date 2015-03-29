package com.example.guoziwen.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * Created by guoziwen on 15/3/29.
 */
public class MyViewGroup extends FrameLayout{
    private final int screenH;
    private Paint paint;
    public MyViewGroup(Context context) {
        super(context);
        screenH = this.getContext().getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();

    }

    public MyViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        screenH = this.getContext().getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();

    }

    public MyViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        screenH = this.getContext().getResources().getDisplayMetrics().heightPixels;
        paint = new Paint();

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int count = getChildCount();
        double initAngel = -Math.PI/6;
        double step = 2*Math.PI/(3*(count+1));
        double circleR = 480;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();
                int locationX = (int)Math.round(circleR * Math.cos(initAngel) - 90.0);
                int locationY =screenH - (int)Math.round(circleR * Math.sin(initAngel) + 580.0);
                child.layout(locationX,locationY,locationX+childWidth,locationY+childHeight);
                android.util.Log.e("gzw","location_x=" + locationX + " location_y=" + locationY + " angel=" + initAngel * 180 / Math.PI);
                initAngel +=step;
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
//        canvas.drawCircle(-90.0f, screenH - 580, 480, paint);
    }
}
