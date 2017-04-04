package com.example.lost.audiodemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.lost.audiodemo.R;
import com.example.lost.audiodemo.utils.DensityUtil;
import com.example.lost.audiodemo.utils.ScreenUtils;


/**
 * 自定义view实现滚动view
 * Created by wuchanghe on 2017/3/21 13:52.
 */

public class FingerView extends View {

    private float currentX = 20;
    private int height = 800;
    Paint p = new Paint();
    Paint circlePaint = new Paint();

    Paint rectPaint = new Paint();

    private OnFingerTouchListener touchListener;

    private int padding = 0;


    public FingerView(Context context) {
        super(context);
        padding = DensityUtil.dip2px(context, 25);
    }

    public FingerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FingerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTouchListener(OnFingerTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = DensityUtil.dip2px(getContext(), 220);
        int reactHeight = DensityUtil.dip2px(getContext(), 200);
        p.setColor(ContextCompat.getColor(getContext(), R.color.circle_color));
        p.setAntiAlias(true);
        p.setStrokeWidth(3.0f);
        circlePaint.setColor(ContextCompat.getColor(getContext(), R.color.circle_color));
        circlePaint.setAntiAlias(true);
        rectPaint.setColor(ContextCompat.getColor(getContext(), R.color.half_transparent));
        rectPaint.setAntiAlias(true);
        canvas.drawLine(currentX, 0, currentX, height + 50, p);
        canvas.drawCircle(currentX, height + 50, 30, circlePaint);
        canvas.drawRect(currentX, 0, ScreenUtils.getScreenWidth(getContext()), reactHeight, rectPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (touchListener != null) {
                    touchListener.onFingerDown();
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchListener != null) {
                    touchListener.onMove();
                }
                break;

            case MotionEvent.ACTION_UP:
                if (touchListener != null) {
                    float percent = (event.getX() - padding) / (ScreenUtils.getScreenWidth(getContext()) - 2 * padding);
                    touchListener.onFingerUp(percent);
                }
                break;
        }

        if (event.getX() > ScreenUtils.getScreenWidth(getContext()) - padding || event.getX() < padding) {
            return true;
        }
        currentX = event.getX();
        postInvalidate();
        return true;
    }


    public void onPlay(int x) {
        currentX = x;
        postInvalidateOnAnimation();
    }


    public interface OnFingerTouchListener {
        void onMove();

        void onFingerUp(float percent);

        void onFingerDown();
    }


}
