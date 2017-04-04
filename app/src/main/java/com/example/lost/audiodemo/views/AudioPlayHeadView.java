package com.example.lost.audiodemo.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.example.lost.audiodemo.utils.DensityUtil;
import com.example.lost.audiodemo.utils.ScreenUtils;

import java.util.List;

/**
 * 音乐播放头部显示时间刻度view
 * Created by wuchanghe on 2017/3/27 17:41.
 */

public class AudioPlayHeadView extends View {

    Paint paint;
    int width;
    String TAG = this.getClass().getSimpleName();
    private List<String> timeList;// 传进来的时间集合
    Paint textPaint;
    private int screenWidth;
    private int screenHeight;
    private float TEXT_SIZE;


    public AudioPlayHeadView(Context context) {
        super(context);
    }

    public AudioPlayHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioPlayHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public AudioPlayHeadView(Context context, int width, List<String> timeList) {
        super(context);

        screenHeight = ScreenUtils.getScreenHeight(context);
        screenWidth = ScreenUtils.getScreenWidth(context);


        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2.0f);
        this.width = width;
        this.timeList = timeList;
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setStrokeWidth(2.0f);
        float ratioWidth = (float) screenWidth / 480;
        float ratioHeight = (float) screenHeight / 800;
        float RATIO = Math.min(ratioWidth, ratioHeight);
        TEXT_SIZE = Math.round(RATIO * 10);
        textPaint.setTextSize(TEXT_SIZE);
        Log.i(TAG, "时间字体大小：" + TEXT_SIZE);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int padding = DensityUtil.dip2px(getContext(), 25);
        canvas.drawLine(padding, 0, width - padding, 0, paint);
        int offset = (width - (2 * padding)) / 6;
        Log.i(TAG, "一份的offset:" + offset);
        int height = 80;
        int marginTop = 2;
        int timeTextOffset = (int) (TEXT_SIZE * 2);
        int timeTextHeightMargin = 30;

        float[] pts = {padding, marginTop, padding, height,
                padding + offset, marginTop, padding + offset, height,
                padding + 2 * offset, marginTop, padding + 2 * offset, height,
                padding + 3 * offset, marginTop, padding + 3 * offset, height,
                padding + 4 * offset, marginTop, padding + 4 * offset, height,
                padding + 5 * offset, marginTop, padding + 5 * offset, height,
                width - padding, marginTop, width - padding, height};
        canvas.drawLines(pts, paint);

        canvas.drawText(timeList.get(0), padding - timeTextOffset, height + timeTextHeightMargin, textPaint);
        canvas.drawText(timeList.get(1), padding + offset - timeTextOffset, height + timeTextHeightMargin, textPaint);
        canvas.drawText(timeList.get(2), padding + 2 * offset - timeTextOffset, height + timeTextHeightMargin, textPaint);
        canvas.drawText(timeList.get(3), padding + 3 * offset - timeTextOffset, height + timeTextHeightMargin, textPaint);
        canvas.drawText(timeList.get(4), padding + 4 * offset - timeTextOffset, height + timeTextHeightMargin, textPaint);
        canvas.drawText(timeList.get(5), padding + 5 * offset - timeTextOffset, height + timeTextHeightMargin, textPaint);
        canvas.drawText(timeList.get(6), padding + 6 * offset - timeTextOffset, height + timeTextHeightMargin, textPaint);


    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

}
