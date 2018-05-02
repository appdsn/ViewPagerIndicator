package com.appdsn.indicatordemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.appdsn.viewpagerindicator.CirclePagerIndicator;


import java.util.ArrayList;


public class CircleActivity extends FragmentActivity {

    private ViewPager viewPager;
    private CirclePagerIndicator indicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circle);
        setTitle("CircleActivity");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        indicator = (CirclePagerIndicator) findViewById(R.id.indicator);

        TestFragment testFragment1 = new TestFragment();
        TestFragment testFragment2 = new TestFragment();
        TestFragment testFragment3 = new TestFragment();
        TestFragment testFragment4 = new TestFragment();
        TestFragment testFragment5 = new TestFragment();
        TestFragment testFragment6 = new TestFragment();
        final ArrayList<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(testFragment1);
        fragments.add(testFragment2);
        fragments.add(testFragment3);
        fragments.add(testFragment4);
        fragments.add(testFragment5);
        fragments.add(testFragment6);
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
        indicator.setCircleCount(fragments.size());
        indicator.setFrontCircleColor(Color.RED);
        indicator.setBackCircleColor(Color.LTGRAY);
    }


}
