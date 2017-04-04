package com.example.lost.audiodemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.lost.audiodemo.utils.DensityUtil;


/**
 * 录音头部图案
 * Created by wuchanghe on 2017/3/22 11:42.
 */

public class RecordHeadView extends View {

    Paint paint;
    int width;
    String TAG = this.getClass().getSimpleName();

    public RecordHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecordHeadView(Context context) {
        super(context);
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
    }


    public RecordHeadView(Context context, int width) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        this.width = width;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int padding = DensityUtil.dip2px(getContext(), 25);
        int offset = (width - (2 * padding)) / 6;
        Log.i(TAG, "一份的offset:" + offset);
        int height = 80;
        int marginTop = 2;

        float[] pts = {padding, marginTop, padding, height,
                padding + offset, marginTop, padding + offset, height,
                padding + 2 * offset, marginTop, padding + 2 * offset, height,
                padding + 3 * offset, marginTop, padding + 3 * offset, height,
                padding + 4 * offset, marginTop, padding + 4 * offset, height,
                padding + 5 * offset, marginTop, padding + 5 * offset, height,
                width - padding, marginTop, width - padding, height};


        canvas.drawLine(padding, 0, width - padding, 0, paint);

        canvas.drawLines(pts, paint);


    }


    public void drawText(Canvas canvas) {
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


}
