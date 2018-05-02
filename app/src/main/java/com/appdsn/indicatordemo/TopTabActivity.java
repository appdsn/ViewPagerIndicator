package com.appdsn.indicatordemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.appdsn.viewpagerindicator.FixPagerIndicator;
import com.appdsn.viewpagerindicator.IScrollBar;
import com.appdsn.viewpagerindicator.IndicatorAdapter;
import com.appdsn.viewpagerindicator.LineScrollBar;


import java.util.ArrayList;


public class TopTabActivity extends FragmentActivity {

    private ViewPager viewPager;
    private FixPagerIndicator indicator;
    private ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    private String[] tabNames = {"热映", "即将上映"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fix);
        setTitle("TopTabActivity");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        indicator = (FixPagerIndicator) findViewById(R.id.indicator);

        TestFragment testFragment1 = new TestFragment();
        TestFragment testFragment2 = new TestFragment();
        fragments.add(testFragment1);
        fragments.add(testFragment2);

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
        indicator.bindViewPager(viewPager, 0);
        indicator.setIndicatorAdapter(new IndicatorAdapter() {
            @Override
            public View getTabView(Context context, int position) {
                TextView textView = new TextView(TopTabActivity.this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(tabNames[position]);
                return textView;
            }

            @Override
            public IScrollBar getScrollBar(Context context) {
                LineScrollBar scrollBar = new LineScrollBar(context);
                scrollBar.setHeight(0);
                scrollBar.setColor(0xffffffff);
                scrollBar.setRadius(dip2px(context,5));
                return scrollBar;
            }

            @Override
            public int getTabCount() {
                return tabNames.length;
            }

            @Override
            public void onTabChange(View view, int position,
                                    float selectPercent) {

            }
        });

    }
    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

}
