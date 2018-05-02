package com.appdsn.viewpagerindicator;

import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by wbz360 on 2017/4/10.
 */
public class ViewPagerIndicator extends HorizontalScrollView implements ViewPager.OnPageChangeListener {
    protected ViewPager mViewPager;
    protected int mCurPosition = 0;
    protected IndicatorAdapter mIndicatorAdapter;
    protected int mTabCount;
    protected LinearLayout mTabViewLayout;
    protected FrameLayout mScrollBarLayout;
    protected IScrollBar mScrollBar;
    protected boolean isClicked = true;
    protected boolean isSmoothScroll = true;
    protected boolean isScrollBarFront = false;
    protected OnClickListener mOnTabViewClickListener;
    protected boolean mIsFix;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (isInEditMode()) {
            return;
        }
        setHorizontalScrollBarEnabled(false);
        /*rootView:ScrollView的子view宽度只能是WRAP_CONTENT，设置其他宽度也会变成WRAP_CONTENT*/
        FrameLayout rootLayout = new FrameLayout(context);
        LayoutParams rootParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        addView(rootLayout, rootParams);
        /*scrollbar：跟随mTabViewLayout的宽度，所以设置为MATCH_PARENT*/
        mScrollBarLayout = new FrameLayout(context);
        LayoutParams barParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        rootLayout.addView(mScrollBarLayout, barParams);
        /*tabView：宽度可以自定义，默认WRAP_CONTENT*/
        mTabViewLayout = new LinearLayout(context);
        mTabViewLayout.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams tabParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        rootLayout.addView(mTabViewLayout, tabParams);
    }

    public void setOnTabViewClickListener(OnClickListener onClickListener) {
        this.mOnTabViewClickListener = onClickListener;
    }

    /*滚动条覆盖方式有两种：在tabView的上面，或者后面*/
    public void setScrollBarFront(boolean isFront) {
        isScrollBarFront = isFront;
        if (isScrollBarFront) {
            mScrollBarLayout.getParent().bringChildToFront(mScrollBarLayout);
        } else {
            mScrollBarLayout.getParent().bringChildToFront(mTabViewLayout);
        }
    }

    /*设置是否平滑滚动ScrollBar，以及ViewPager的平滑切换，默认是开启的*/
    public void setSmoothScrollEnable(boolean isSmoothScroll) {
        this.isSmoothScroll = isSmoothScroll;
    }

    /*设置指示器的tab是否可滚动，或者是固定宽度的*/
    public void setFixEnable(boolean isFix) {
        this.mIsFix = isFix;
        post(new Runnable() {
            @Override
            public void run() {
                if (mIsFix) {
                    int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
                    LayoutParams tabParams = new LayoutParams(width, LayoutParams.MATCH_PARENT);
                    mTabViewLayout.setLayoutParams(tabParams);
                }
            }
        });
    }

    public View getTabView(int position) {
        return mTabViewLayout.getChildAt(position);
    }

    public int getTabCount() {
        return mTabCount;
    }

    public IScrollBar getScrollBar() {
        return mScrollBar;
    }

    /*将指示器和ViewPager绑定，实现联动效果（当然也可以不绑定，单独使用指示器）*/
    public void bindViewPager(ViewPager viewPager) {
        bindViewPager(viewPager, 0);
    }

    public void bindViewPager(ViewPager viewPager, int position) {
        if (viewPager == null) {
            return;
        }
        if (viewPager.getAdapter() == null) {
            throw new RuntimeException("viewpager adapter can not be null");
        }
        isClicked = true;
        mViewPager = viewPager;
        if (position < 0 || position >= mViewPager.getAdapter().getCount()) {
            position = 0;
        }
        mCurPosition = position;
        mViewPager.addOnPageChangeListener(this);
        /*ViewPager数据改变时，要同步刷新指示器的数据*/
        mViewPager.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                mCurPosition = mViewPager.getCurrentItem();
                refreshIndicatorView();
            }
        });

        mViewPager.setCurrentItem(mCurPosition);//只要position不同，onPageSelected会立即调用
        /*同步指示器位置，和指定的position一致，这个时候也许还没有设置过IndicatorAdapter，所以判断一下*/
        refreshIndicatorView();
    }

    /*设置指示器数据适配器：包括，指示器的tabView数量，以及每个自定义的tabview，滑块样式，滑动过程的监听*/
    public void setIndicatorAdapter(IndicatorAdapter adapter) {
        mIndicatorAdapter = adapter;
        refreshIndicatorView();
    }

    /*刷新指示器的数据*/
    public void refreshIndicatorView() {
        if (mIndicatorAdapter == null) {
            return;
        }
        /*init TabView*/
        mTabViewLayout.removeAllViews();
        mTabCount = mIndicatorAdapter.getTabCount();
        //数据校验，如果绑定了ViewPager强制tab的数量和它保持一致
        if (mViewPager != null && mViewPager.getAdapter().getCount() != mTabCount) {
            mTabCount = mViewPager.getAdapter().getCount();
        } else {
            if (mCurPosition >= mTabCount) {
                mCurPosition = mTabCount - 1;
            }
        }
        if (mTabCount < 1) {
            return;
        }
        for (int i = 0; i < mTabCount; i++) {
            View tabView = mIndicatorAdapter.getTabView(getContext(), i);
            if (tabView == null) {//如果为空，给一个默认看不见的tab（防止空指针异常）
                tabView = new View(getContext());
                tabView.setVisibility(GONE);
            }
            tabView.setTag(i);//tab的索引位置
            tabView.setFocusable(true);
            tabView.setOnClickListener(mTabClickListener);
            LinearLayout.LayoutParams layoutParams;
            if (tabView.getLayoutParams() != null) {
                layoutParams = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
            } else {
                //tab高度和指示器高度一致，宽度：固定的是平分宽度，滚动的是wrap_content
                if (mIsFix) {
                    layoutParams = new LinearLayout.LayoutParams(0, MATCH_PARENT, 1);
                } else {
                    layoutParams = new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT);
                }
            }
            mTabViewLayout.addView(tabView, layoutParams);
        }
        /*init ScrollBar*/
        mScrollBarLayout.removeAllViews();
        mScrollBar = mIndicatorAdapter.getScrollBar(getContext());
        if (mScrollBar != null && mScrollBar instanceof View) {
            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mScrollBarLayout.addView((View) mScrollBar, lp);
            ((View) mScrollBar).setClickable(false);//禁用触摸事件，以防其在tabview上方时，tabview不能点击
        }
        /*初始化选中的tab及ScrollBar*/
        selectTabView(mCurPosition);
        changeTabView(mCurPosition, 0);
    }

    /*只有手指滚动ViewPager时候才会调用,isClicked判断是否为手指滚动，还是点击tabView带来的滚动
    * 如果是点击tab带来的滚动，则不用在onPageScrolled时再滚动tab了*/
    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {//只有手指滑动才会有该事件
            isClicked = false;
        }
    }

    /*只要每次setCurrentItem的position不同都会调用，setAdapter会调用一次*/
    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {
        if (!isClicked) {
            if (isSmoothScroll) {
                changeTabView(position, positionOffset);
            }
        }
    }

    /*setCurrentItem后会立即调用（当然新position和当前不相同），setAdapter不会调用*/
    @Override
    public void onPageSelected(int position) {
        if (!isClicked) {
            mCurPosition = position;
            selectTabView(position);
            if (!isSmoothScroll) {//和上面的onPageScrolled对应，两者选其一
                changeTabView(position, 0);
            }
        }
    }

    /*选中某个tab，可以直接调用这个方法由代码设置选中，或者手指点击选中*/
    public void setCurrentTab(int position) {
        if (position < 0 || position >= mTabCount || mCurPosition == position || mIndicatorAdapter == null) {
            return;
        }

        final int oldSelected = mCurPosition;
        final int newSelected = position;
        final View curView = mTabViewLayout.getChildAt(oldSelected);
        final View nextView = mTabViewLayout.getChildAt(newSelected);

        mCurPosition = position;
        isClicked = true;
        selectTabView(position);

        if (isSmoothScroll) {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(newSelected, true);
            }
            /*模仿平滑滚动改变TabView*/
            long duration = (Math.abs(newSelected - oldSelected) - 1) * 80 + 200;
            ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
            animator.setDuration(250);//默认滑动块的滑动时间
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animator) {
                    float positionOffset = (Float) animator.getAnimatedValue();
                    changeTabView(curView, nextView, positionOffset);
                }
            });
            animator.start();
        } else {
            if (mViewPager != null) {
                mViewPager.setCurrentItem(newSelected, false);
            }
            changeTabView(curView, nextView, 1);
        }


    }

    /*移动当前tab到中间，并设置当前tab为选中状态*/
    private void selectTabView(int position) {
        if (mIndicatorAdapter != null) {
            scrollTabToCenter(position);//将选中的tab移动到中间
            for (int i = 0; i < mTabCount; i++) {
                View child = mTabViewLayout.getChildAt(i);
                boolean isSelected = (i == position);//将选中的tab设置为选中状态
                child.setSelected(isSelected);
                child.setActivated(isSelected);//解决第一个默认Selected时不起效果，可用Activated属性代替
            }
        }
    }

    private void scrollTabToCenter(int position) {
        View tabView = mTabViewLayout.getChildAt(position);
        int scrollPos = tabView.getLeft()
                - (getWidth() - tabView.getWidth()) / 2;
        smoothScrollTo(scrollPos, 0);
    }

    /*两个tab之间切换时的联动过渡状态,以及滑块的移动*/
    private void changeTabView(int position, float positionOffset) {
        if (mIndicatorAdapter == null) {
            return;
        }

        View curView = mTabViewLayout.getChildAt(position);
        View nextView;
        int nextPosition;
        if (position >= mTabCount - 1) {//最后了（下一个tab不能为null，这里是自己本身）
//            nextPosition = 0;
            nextPosition = position;
        } else {
            nextPosition = position + 1;
        }
        nextView = mTabViewLayout.getChildAt(nextPosition);
        changeTabView(curView, nextView, positionOffset);

    }

    private void changeTabView(View curView, View nextView, float positionOffset) {
        mIndicatorAdapter.onTabChange(curView, (Integer) curView.getTag(),
                1 - positionOffset);
        if (curView != nextView) {
            mIndicatorAdapter.onTabChange(nextView, (Integer) nextView.getTag(),
                    positionOffset);
        }

        if (mScrollBar != null) {
            mScrollBar.changScrollBar(curView, nextView, positionOffset);
        }
    }

    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            if (mOnTabViewClickListener != null) {//外部可以设置监听，写自己的逻辑
                mOnTabViewClickListener.onClick(view);
            }
            setCurrentTab((Integer) view.getTag());
        }
    };


}
