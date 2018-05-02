package com.appdsn.indicatordemo;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LoopViewPager extends ViewPager {
    private PagerAdapterWrapper mAdapter;
    private PageTransformer mPageTransformer;
    private long autoTurningTime = 3000;// 自动翻页时间
    private boolean isTurning = false;// 是否正在翻页
    private boolean canLoop = true;//是否可以循环，默认是可以的
    private ViewPagerScroller scroller;
    private Runnable loopTask = new Runnable() {
        @Override
        public void run() {
            postDelayed(this, autoTurningTime);
            if (mAdapter == null) {
                return;
            }

            int page = getInnerCurrentItem() + 1;
            if (page >= mAdapter.getCount()) {
                page = 0;
            }
            setInnerCurrentItem(page, true);

        }
    };

    public LoopViewPager(Context context) {
        super(context);
        init();
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        super.addOnPageChangeListener(onPageChangeListener);
        initViewPagerScroller();
//        setScrollDuration(2000);
    }

    /**
     * 设置ViewPager的滑动速度
     */
    private void initViewPagerScroller() {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(getContext());
            mScroller.set(this, scroller);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // 触碰控件的时候，翻页应该停止，离开的时候如果之前是开启了翻页的话则重新启动翻页
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP
                || action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_OUTSIDE) {
            // 开始翻页
            if (isTurning) {
                startTurning(autoTurningTime);
            }
        } else if (action == MotionEvent.ACTION_DOWN) {
            // 停止翻页
            if (isTurning) {
                stopTurning();
                isTurning = true;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /***
     * 开始翻页
     *
     * @param autoTurningTime 自动翻页时间
     * @return
     */
    public void startTurning(long autoTurningTime) {
        // 设置可以翻页
        this.autoTurningTime = autoTurningTime;
        // 如果是正在翻页的话先停掉
        if (isTurning) {
            stopTurning();
        }
        // 开启翻页
        isTurning = true;
        postDelayed(loopTask, autoTurningTime);
    }

    public void stopTurning() {
        isTurning = false;
        removeCallbacks(loopTask);
    }

    /**
     * 设置ViewPager的滚动速度
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    private OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            customPageSelected(mAdapter.toRealPosition(position));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            int realPos = mAdapter.toRealPosition(position);
            if (realPos == mAdapter.getRealCount() - 1) {
                if (positionOffset > .5) {
                    realPos = 0;
                    positionOffset = 0;
                    positionOffsetPixels = 0;
                } else {
                    positionOffset = 0;
                    positionOffsetPixels = 0;
                }
            }

            customPageScrolled(realPos, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            int position = getInnerCurrentItem();
            if (canLoop && state == ViewPager.SCROLL_STATE_IDLE) {
                int startPos = 1;//头部位置开始跳转的位置（正数第2个）
                if (startPos >= mAdapter.getExtraCount()) {
                    startPos = 0;
                }

                int endPos = mAdapter.getCount() - 2;//尾部位置开始跳转的位置（倒数第2个）
                if (endPos <= mAdapter.getRealLastPosition()) {
                    endPos = mAdapter.getRealLastPosition() + mAdapter.getExtraCount();
                }

                int realPos = mAdapter.toRealPosition(position);
                if (position <= startPos || position >= endPos) {
                    position = mAdapter.toInnerPosition(realPos);
                    setInnerCurrentItem(position, false);
                }

            }
            customScrollStateChanged(state);
        }
    };


    /*因为循环，头尾各添加了一个相同的View，所以不能同时加到布局中*/
    public void setOffscreenPageLimit(int limit) {
        if (mAdapter != null) {
            int max = (mAdapter.getRealCount() - 1) / 2;
            if (limit > max) {
                limit = max;
            }
        }
        super.setOffscreenPageLimit(limit);
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        mAdapter = new PagerAdapterWrapper(adapter, this);
        if (mAdapter.getRealCount() <= 1) {//默认page数为1时不能循环
            canLoop = false;
        } else {
            canLoop = true;
        }
        super.setAdapter(mAdapter);
        setCurrentItem(0, false);//必须在setAdapter之后调用才有效
        setOffscreenPageLimit(getOffscreenPageLimit());
        /*默认开始自动滚动*/
        startTurning(autoTurningTime);
    }

    public void setCanLoop(boolean isCanLoop) {
        int realPos = getCurrentItem();
        canLoop = isCanLoop;
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
            setCurrentItem(realPos, false);//必须在setAdapter之后调用才有效
        }
    }

    public boolean isCanLoop() {
        return canLoop;
    }

    @Override
    public PagerAdapter getAdapter() {
        return mAdapter != null ? mAdapter.getRealAdapter() : mAdapter;
    }

    public int getInnerCurrentItem() {
        return mAdapter != null ? super.getCurrentItem() : 0;
    }

    public void setInnerCurrentItem(int item, boolean smoothScroll) {
        if (item != getInnerCurrentItem()) {
            super.setCurrentItem(item, smoothScroll);
        }
    }

    @Override
    public int getCurrentItem() {
        return mAdapter != null ? mAdapter.toRealPosition(super.getCurrentItem()) : 0;
    }

    public void setCurrentItem(int item, boolean smoothScroll) {
        int innerItem = mAdapter.toInnerPosition(item);
        super.setCurrentItem(innerItem, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        int innerItem = mAdapter.toInnerPosition(item);
        super.setCurrentItem(innerItem);
    }

    public void setPageTransformer(boolean reverseDrawingOrder, PageTransformer transformer) {
        super.setPageTransformer(reverseDrawingOrder, transformer);
        mPageTransformer = transformer;
    }

    private int getClientWidth() {
        return getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        /*解决调用setCurrentItem时，transformPage的transformPos不准确*/
        if (mPageTransformer != null) {
            final int scrollX = getScrollX();
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = getChildAt(i);
                final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                if (lp.isDecor) continue;
                final float transformPos = (float) (child.getLeft() - scrollX) / getClientWidth();
                mPageTransformer.transformPage(child, transformPos);
            }
        }
    }

    public class PagerAdapterWrapper extends PagerAdapter {

        private LoopViewPager mViewPager;
        private PagerAdapter mOutAdapter;
        private int extraCount = 2;//左右两边额外各加的页数（默认值可变）
        private HashMap<Integer, Object> scapObjects = new HashMap<>();

        public PagerAdapterWrapper(PagerAdapter adapter, LoopViewPager viewPager) {
            this.mOutAdapter = adapter;
            this.mViewPager = viewPager;
            mOutAdapter.registerDataSetObserver(new DataSetObserver() {
                @Override
                public void onChanged() {
                    super.onChanged();
                    notifyDataSetChanged();
                }

                @Override
                public void onInvalidated() {
                    super.onInvalidated();
                }
            });
        }

        @Override
        public void notifyDataSetChanged() {
            scapObjects.clear();
            super.notifyDataSetChanged();
        }

        public int getExtraCount() {
            if (mViewPager.canLoop) {
                return extraCount;
            } else {
                return 0;
            }
        }

        public int toRealPosition(int innerPosition) {
            if (innerPosition >= getCount()) {
                innerPosition = getCount() - 1;
            }
            int realCount = getRealCount();
            if (realCount == 0)
                return 0;
            int realPosition = (innerPosition - getExtraCount()) % realCount;
            if (realPosition < 0)
                realPosition += realCount;

            return realPosition;
        }

        public int toInnerPosition(int realPosition) {
            int position = (realPosition + getExtraCount());
            return position;
        }


        private int getRealFirstPosition() {
            return getExtraCount();
        }

        private int getRealLastPosition() {
            return getRealFirstPosition() + getRealCount() - 1;
        }

        /*<1><2>  <0><1><2>	<0><1>首尾各加两个*/
        @Override
        public int getCount() {
            return getRealCount() + getExtraCount() * 2;
        }

        public int getRealCount() {
            return mOutAdapter.getCount();
        }

        public PagerAdapter getRealAdapter() {
            return mOutAdapter;
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Log.i("123", "instantiateItem:" + position);
            int realPosition = toRealPosition(position);
            Object object = null;
            for (Integer key : scapObjects.keySet()) {
                int realPosition2 = toRealPosition(key);
                if (realPosition2 == realPosition) {
                    object = scapObjects.get(key);
                    break;
                }
            }
            if (object == null) {
                object = mOutAdapter.instantiateItem(container, realPosition);
            } else {
                /* 如果已经添加进去，先移除View（可以先做个判断：getParent()！=null
                或者!fragment.isDetached()），这里就不做判断了*/
                mOutAdapter.destroyItem(container, realPosition, object);
                Object newObject = mOutAdapter.instantiateItem(container, realPosition);
                /*触发requestLayout更新UI：对于addView可以不用写出，对于Fragment需要手动更新一下*/
                mViewPager.requestLayout();

                /*如果每次instantiateItem都是new的新对象，那么newObject!=object，
                上一步destroyItem移除的View还要添加进去，这种情况针对的是realCout等于2时*/
                if (newObject != object) {
                    if (object instanceof View) {
                        container.addView((View) object);
                    }
                }
                object = newObject;
            }
            /*缓存每个page，用于判断*/
            scapObjects.put(position, object);
            return object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            Log.i("123", "destroyItem:" + position);
            int realPosition = toRealPosition(position);
            Object object2 = null;
            int curPosition = mViewPager.getInnerCurrentItem();
            int pageLimit = mViewPager.getOffscreenPageLimit();
            int startPos = Math.max(0, curPosition - pageLimit);
            int N = getCount();
            int endPos = Math.min(N - 1, curPosition + pageLimit);
            for (int i = startPos; i <= endPos; i++) {
                int realPosition2 = toRealPosition(i);
                if (realPosition == realPosition2) {
                    object2 = scapObjects.get(i);
                    break;
                }
            }
            /*因为循环，头尾各加了一个相同的View，所以当移除头或尾部View时，需要判断是否移除：
            比如在头部显示了该View，末尾不可见的该View不能再移除操作，否则头部View看不见了*/
            if (object != object2) {
                mOutAdapter.destroyItem(container, realPosition, object);
                scapObjects.remove(position);
            }

        }

        @Override
        public void finishUpdate(ViewGroup container) {
            mOutAdapter.finishUpdate(container);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return mOutAdapter.isViewFromObject(view, object);
        }

        @Override
        public void restoreState(Parcelable bundle, ClassLoader classLoader) {
            mOutAdapter.restoreState(bundle, classLoader);
        }

        @Override
        public Parcelable saveState() {
            return mOutAdapter.saveState();
        }

        @Override
        public void startUpdate(ViewGroup container) {
            mOutAdapter.startUpdate(container);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            mOutAdapter.setPrimaryItem(container, toRealPosition(position), object);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopTurning();
    }

    private List<OnPageChangeListener> mOnPageChangeListeners;
    private OnPageChangeListener mOnPageChangeListener;

    @Deprecated
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mOnPageChangeListener = listener;
    }

    public void addOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners == null) {
            mOnPageChangeListeners = new ArrayList<>();
        }
        mOnPageChangeListeners.add(listener);
    }

    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.remove(listener);
        }
    }

    public void clearOnPageChangeListeners() {
        if (mOnPageChangeListeners != null) {
            mOnPageChangeListeners.clear();
        }
    }

    private void customPageScrolled(int position, float offset, int offsetPixels) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
        }
        if (mOnPageChangeListeners != null) {
            for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageScrolled(position, offset, offsetPixels);
                }
            }
        }

    }

    private void customPageSelected(int position) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageSelected(position);
        }
        if (mOnPageChangeListeners != null) {
            for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageSelected(position);
                }
            }
        }

    }

    private void customScrollStateChanged(int state) {
        if (mOnPageChangeListener != null) {
            mOnPageChangeListener.onPageScrollStateChanged(state);
        }
        if (mOnPageChangeListeners != null) {
            for (int i = 0, z = mOnPageChangeListeners.size(); i < z; i++) {
                OnPageChangeListener listener = mOnPageChangeListeners.get(i);
                if (listener != null) {
                    listener.onPageScrollStateChanged(state);
                }
            }
        }

    }

    public class ViewPagerScroller extends Scroller {
        private int mScrollDuration = 800;// 滑动速度,值越大滑动越慢，滑动太快会使3d效果不明显
        private boolean zero;

        public ViewPagerScroller(Context context) {
            super(context);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator,
                                 boolean flywheel) {
            super(context, interpolator, flywheel);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy) {
            super.startScroll(startX, startY, dx, dy, zero ? 0 : mScrollDuration);
        }

        public int getScrollDuration() {
            return mScrollDuration;
        }

        public void setScrollDuration(int scrollDuration) {
            this.mScrollDuration = scrollDuration;
        }

        public boolean isZero() {
            return zero;
        }

        public void setZero(boolean zero) {
            this.zero = zero;
        }
    }
}
