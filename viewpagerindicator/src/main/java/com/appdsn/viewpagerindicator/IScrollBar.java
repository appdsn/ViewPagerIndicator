package com.appdsn.viewpagerindicator;

import android.view.View;

/**
 * Created by wbz360 on 2017/4/10.
 */
public interface IScrollBar {
	/*必须是一个继承自View的类,positionOffset是滑动离开curTabView的比例，接近nextTabView的比例*/
	void changScrollBar(View curTabView, View nextTabView, float positionOffset);
}
