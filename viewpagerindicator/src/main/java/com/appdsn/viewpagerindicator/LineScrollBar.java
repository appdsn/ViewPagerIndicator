package com.appdsn.viewpagerindicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by wbz360 on 2017/4/10.
 */

public class LineScrollBar extends View implements IScrollBar {
    private View curTabView;
    private View nextTabView;
    private float positionOffset;
    private Paint paint;
    private int mBarHeight;
    private int radius;
    private int mBarWidth;
    private boolean isFixWidth;
    private int mGravity = Gravity.BOTTOM;

    public LineScrollBar(Context context) {
        super(context);
        paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.FILL);
    }


    public void setGravity(int gravity) {
        mGravity = gravity;
    }

    public void setHeight(int barHeight) {
        this.mBarHeight = barHeight;
    }

    /*默认宽度是tabview的宽度，会随着手指滑动变化，设置固定值后，不会变化*/
    public void setWidth(int barWidth) {
        this.mBarWidth = barWidth;
        isFixWidth = true;
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    @Override
    public void changScrollBar(View curTabView, View nextTabView, float positionOffset) {
        this.curTabView = curTabView;
        this.nextTabView = nextTabView;
        this.positionOffset = positionOffset;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (curTabView == null || nextTabView == null) {
            return;
        }
        int curtabWidth = curTabView.getWidth();
        int curtabHeight = curTabView.getHeight();
        int curbarWidth;
        if (mBarWidth == 0) {
            curbarWidth = curtabWidth;
        } else {
            curbarWidth = mBarWidth;
        }
        int curbarHeight;
        if (mBarHeight == 0) {
            curbarHeight = curtabHeight;
        } else {
            curbarHeight = mBarHeight;
        }

        int curCenterX = curTabView.getLeft() + curtabWidth / 2;
        int nexttabWidth = nextTabView.getWidth();
        int nextbarWidth = nexttabWidth;
        int nextCenterX = nextTabView.getLeft() + nexttabWidth / 2;
        float barCenterX = curCenterX + (nextCenterX - curCenterX)
                * positionOffset;
        float barWidth;
        if (isFixWidth) {
            barWidth = curbarWidth;
        } else {
            barWidth = curbarWidth + positionOffset
                    * (nextbarWidth - curbarWidth);
        }
        float offsetX = barCenterX - barWidth / 2;
        int offsetY = 0;
        switch (mGravity) {
            case Gravity.CENTER:
                // offsetY = (curtabHeight - curbarHeight) / 2;不考虑padingTop
                offsetY = curTabView.getTop() + (curtabHeight - curbarHeight) / 2;
                break;
            case Gravity.TOP:
                offsetY = curTabView.getTop();// offsetY =0,不考虑pading
                break;
            case Gravity.BOTTOM:
            default:
                // offsetY = curtabHeight - curbarHeight;//不考虑pading
                offsetY = curTabView.getBottom() - curbarHeight;
                break;
        }

        canvas.drawRoundRect(new RectF(offsetX, offsetY, offsetX + barWidth, offsetY + curbarHeight), radius, radius, paint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
