package com.appdsn.indicatordemo;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.appdsn.viewpagerindicator.FixPagerIndicator;
import com.appdsn.viewpagerindicator.IScrollBar;
import com.appdsn.viewpagerindicator.IndicatorAdapter;
import com.appdsn.viewpagerindicator.LineScrollBar;

import java.util.ArrayList;

public class BottomTabActivity extends FragmentActivity {

    private ViewPager viewPager;
    private FixPagerIndicator indicator;
    private String[] tabNames = {"首页", "关注","消息","我的"};
    private int[] tabDrawables = {R.drawable.bottom_tab_icon_home_selector,
            R.drawable.bottom_tab_icon_follow_selector, R.drawable.bottom_tab_icon_message_selector,
            R.drawable.bottom_tab_icon_mine_selector};
    ArrayList<Fragment> fragments = new ArrayList<Fragment>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_tab);
        setTitle("TopTabActivity");
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(3);
        indicator = (FixPagerIndicator) findViewById(R.id.indicator);

        TestFragment testFragment1 = new TestFragment();
        TestFragment testFragment2 = new TestFragment();
        TestFragment testFragment3 = new TestFragment();
        TestFragment testFragment4 = new TestFragment();
        fragments.add(testFragment1);
        fragments.add(testFragment2);
        fragments.add(testFragment3);
        fragments.add(testFragment4);

        initTab();
    }

    private void initTab() {
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
                LinearLayout linearLayout=new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setSelected(true);
                linearLayout.setGravity(Gravity.CENTER);

                ImageView imageView=new ImageView(context);
                imageView.setImageResource(tabDrawables[position]);

                TextView textView = new TextView(BottomTabActivity.this);
                textView.setGravity(Gravity.CENTER);
                textView.setText(tabNames[position]);
                textView.setTextColor(context.getResources().getColorStateList(R.color.bottom_tab_item_text_color));

                linearLayout.addView(imageView);
                linearLayout.addView(textView);

                return linearLayout;
            }

            @Override
            public IScrollBar getScrollBar(Context context) {
                LineScrollBar scrollBar = new LineScrollBar(context);
                scrollBar.setHeight(10);
                scrollBar.setColor(Color.RED);
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

//        indicator.setSmoothScrollEnable(false);

        indicator.setOnTabViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indicator.invalidate();
                Toast.makeText(v.getContext(),"onClick",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
