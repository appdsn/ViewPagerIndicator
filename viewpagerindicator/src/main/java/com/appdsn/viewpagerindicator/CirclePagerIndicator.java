package com.appdsn.viewpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wbz360 on 2016/01/11.
 */
public class CirclePagerIndicator extends ViewPagerIndicator {
    private float mRadius;
    private Paint mPaint;
    private int mCount;
    private int frontColor = Color.WHITE;

    public CirclePagerIndicator(Context context) {
        this(context, null);
    }

    public CirclePagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFixEnable(true);
        setScrollBarFront(true);
        mRadius = dip2px(context, 3);
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
    }


    @Override
    public void onAttachedToWindow() {
        setIndicatorAdapter(new IndicatorAdapter() {
            @Override
            public View getTabView(Context context, int position) {
                return new CircleView(context);
            }

            @Override
            public IScrollBar getScrollBar(Context context) {
                LineScrollBar scrollBar = new LineScrollBar(context);
                scrollBar.setHeight((int) (mRadius * 2));
                scrollBar.setWidth((int) (mRadius * 2));
                scrollBar.setColor(frontColor);
                scrollBar.setRadius((int) mRadius);
                scrollBar.setGravity(Gravity.CENTER);
                return scrollBar;
            }

            @Override
            public int getTabCount() {
                if (mViewPager != null && mViewPager.getAdapter() != null) {
                    mCount = mViewPager.getAdapter().getCount();
                }

                ViewGroup.LayoutParams params = getLayoutParams();
                params.width = (int) (mCount * mRadius * 3);
                params.height = (int) (mRadius * 4);
                setLayoutParams(params);
                setFixEnable(true);
                return mCount;
            }

            @Override
            public void onTabChange(View view, int position,
                                    float selectPercent) {

            }
        });
        super.onAttachedToWindow();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setCircleCount(int count) {
        mCount = count;
    }

    public void setCircleRadius(float radius) {
        mRadius = radius;
    }

    public void setBackCircleColor(int color) {
        mPaint.setColor(color);
    }

    public void setFrontCircleColor(int color) {
        frontColor = color;
    }

    private class CircleView extends View {


        public CircleView(Context context) {
            super(context);

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRadius, mPaint);
        }
    }

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

}
