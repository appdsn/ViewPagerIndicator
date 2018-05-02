package com.appdsn.viewpagerindicator;

import android.content.Context;
import android.view.View;

/**
 * Created by wbz360 on 2017/4/7.
 * <p>
 * 获取每一个pager对应的指示器tabitem，scrollBar,扩展性强，可以自定义view
 */
public interface IndicatorAdapter {

    /*实例化每个tabview（在设置IndicatorAdapter时，会立即循环调用一次性实例化完所有的tabview）*/
    View getTabView(Context context, int position);

    /*实例化一个滚动条，样式可以自定义*/
    IScrollBar getScrollBar(Context context);

    /*tabview的数量，如果绑定了ViewPager，则tab数量默认和ViewPager数据量保持一致*/
    int getTabCount();

    /**
     * 当page在互动的过程中，可以联动indicator
     *
     * @param tabView       是当前的tabView
     * @param position      是当前的tabView对应的索引
     * @param selectPercent 是当前的tabView在正常状态下，任何属性需要改变的百分比
     */
    void onTabChange(View tabView, int position, float selectPercent);
}
