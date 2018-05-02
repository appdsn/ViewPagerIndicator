package com.appdsn.viewpagerindicator;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by wbz360 on 2017/04/10.
 */
public class FixPagerIndicator extends ViewPagerIndicator {

    public FixPagerIndicator(Context context) {
        this(context, null);
    }

    public FixPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFixEnable(true);
    }
}
