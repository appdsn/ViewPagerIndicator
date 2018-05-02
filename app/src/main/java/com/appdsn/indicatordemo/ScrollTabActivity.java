package com.appdsn.indicatordemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.appdsn.viewpagerindicator.IScrollBar;
import com.appdsn.viewpagerindicator.IndicatorAdapter;
import com.appdsn.viewpagerindicator.LineScrollBar;
import com.appdsn.viewpagerindicator.ViewPagerIndicator;

import java.util.ArrayList;


public class ScrollTabActivity extends FragmentActivity {

    private ViewPager viewPager;
    private ViewPagerIndicator indicator;
    private String[] tabNames = {"全部", "前端开发", "后端开发", "设计", "移动开发", "其他类干货", "正在热映", "即将上映"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        indicator = (ViewPagerIndicator) findViewById(R.id.indicator);
        TestFragment testFragment1 = new TestFragment();
        TestFragment testFragment2 = new TestFragment();
        TestFragment testFragment3 = new TestFragment();
        TestFragment testFragment4 = new TestFragment();
        TestFragment testFragment5 = new TestFragment();
        TestFragment testFragment6 = new TestFragment();
        TestFragment testFragment7 = new TestFragment();
        TestFragment testFragment8 = new TestFragment();
        final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(testFragment1);
        fragments.add(testFragment2);
        fragments.add(testFragment3);
        fragments.add(testFragment4);
        fragments.add(testFragment5);
        fragments.add(testFragment6);
        fragments.add(testFragment7);
        fragments.add(testFragment8);

        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return fragments.size();
            }

            @Override
            public Fragment getItem(int arg0) {
                // TODO Auto-generated method stub
                return fragments.get(arg0);
            }
        });


        indicator.bindViewPager(viewPager);
        indicator.setScrollBarFront(true);
        indicator.setIndicatorAdapter(new IndicatorAdapter() {
            @Override
            public View getTabView(Context context, int position) {
                TextView tabView = new TextView(ScrollTabActivity.this);
                tabView.setTextColor(Color.BLACK);
                tabView.setText(tabNames[position]);
                tabView.setGravity(Gravity.CENTER);
                tabView.setTextColor(Color.WHITE);
                tabView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tabView.setPadding(30, 0, 30, 0);
                return tabView;
            }

            @Override
            public IScrollBar getScrollBar(Context context) {
                LineScrollBar scrollBar = new LineScrollBar(context);
                scrollBar.setColor(Color.WHITE);//滚动块颜色
                scrollBar.setHeight(dip2px(context, 2));//滚动块高度，不设置默认和每个tabview高度一致
                scrollBar.setRadius(dip2px(context, 1));//滚动块圆角半径
                scrollBar.setGravity(Gravity.BOTTOM);//可设置上中下三种
                scrollBar.setWidth(0);//滚动块宽度，不设置默认和每个tabview宽度一致
                return scrollBar;
            }

            @Override
            public int getTabCount() {
                return tabNames.length;
            }

            @Override
            public void onTabChange(View view, int position,
                                    float selectPercent) {
                TextView tabView= (TextView) view;
                tabView.setScaleX(1+0.2F*selectPercent);
                tabView.setScaleY(1+0.2F*selectPercent);
            }
        });


        indicator.setOnTabViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }
}
