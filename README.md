框架简介<br>
=
　　强大的viewpager指数器(也可以单独使用)，主要功能：
* 自定义滑动块样式（滑块是一个View，你可以定制这个滑块的各种风格，比如可以设置一张图片， 自带一个颜色块滑块，可以设置颜色，圆角半径，高度，宽度（固定或者跟随tabView宽变化），位置（包括上，中，下三个位置）等；可以设置滑块是否覆盖在tabView上面，还是下面； 当手指滑动ViewPager时，滑块还可以联动等，或者禁用滑块，将不会显示滑块）
* 自定义每个tabView样式（每个tab都是一个View，你可以自定义这个View，比如：常用的底部导航tab状态切换）；

使用方法
=

（1）编写XML文件
--
```java 
    <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical" >
    
        <com.appdsn.viewpagerindicator.ViewPagerIndicator
            android:id="@+id/indicator"
            android:layout_width="match_parent"
            android:layout_height="50dp"
            android:background="#FF4C55" />
        <android.support.v4.view.ViewPager
            android:id="@+id/viewPager"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    
    </LinearLayout>
 ```
 
（2）代码中设置属性
-

```java   
{
    //将indicator绑定一个ViewPager，实现和ViewPager联动
    indicator.bindViewPager(viewPager);
          
    //设置indicator的数据：包括每个tabview,一个ScrollBar
    indicator.setIndicatorAdapter(new IndicatorAdapter() {
        @Override
        public View getTabView(Context context, int position) {
            TextView tabView = new TextView(ScrollTabActivity.this);
            tabView.setTextColor(Color.BLACK);
            tabView.setText(tabNames[position]);
            tabView.setGravity(Gravity.CENTER);
            tabView.setTextColor(Color.WHITE);
            tabView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            tabView.setPadding(30, 0, 30, 0);
            return tabView;
         }
          
        @Override
        public IScrollBar getScrollBar(Context context) {
            LineScrollBar scrollBar = new LineScrollBar(context);
            scrollBar.setColor(Color.WHITE);
            scrollBar.setHeight(dip2px(getApplicationContext(), 2));
            scrollBar.setWidth(dip2px(getApplicationContext(), 30));
            return scrollBar;
        }
          
        @Override
        public int getTabCount() {
            return tabNames.length;
        }
          
        @Override
        public void onTabChange(View view, int position,
                                              float selectPercent) {
            //view.setAlpha(selectPercent);
        }
    });
}
 ```

（3）其他设置项
-
* <b>indicator可设置项</b>
```java
{
    //设置是否平滑滚动ScrollBar，以及ViewPager的平滑切换，默认是开启的
    indicator.setSmoothScrollEnable(true);
    //设置指示器的tab是否可滚动，或者是固定宽度的
    indicator.setFixEnable(false);
    //滚动条覆盖方式有两种：在tabView的上面，或者后面
    indicator.setScrollBarFront(false);
    //设置tabview的点击事件，实现自己的点击事件逻辑
    indicator.setOnTabViewClickListener();
 }
 ```

* <b>LineScrollBar可设置项</b>
```java
LineScrollBar scrollBar = new LineScrollBar(context);
scrollBar.setColor(Color.WHITE);//滚动块颜色
scrollBar.setHeight(dip2px(context, 2));//滚动块高度，不设置默认和每个tabview高度一致
scrollBar.setRadius(dip2px(context, 1));//滚动块圆角半径
scrollBar.setGravity(Gravity.BOTTOM);//可设置上中下三种
scrollBar.setWidth(0);//滚动块宽度，不设置默认和每个tabview宽度一致
```


（4）自定义ScrollBar
-
> 可自定义ScrollBar的实现，先要继承自View，再实现IScrollBar接口，主要根据以下三个参数来决定这个View的实现方式及位置，可参考自带的LineScrollBar实现。
```java
public interface IScrollBar {
	/*必须是一个继承自View的类,positionOffset是滑动离开curTabView的比例，接近nextTabView的比例*/
	void changScrollBar(View curTabView, View nextTabView, float positionOffset);
}

 ```

联系方式
-
* Email：2792889279@qq.com
* qq： 2792889279

Licenses
-
        
        Copyright 2018 wbz360(王宝忠)

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

         　　　　http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.






