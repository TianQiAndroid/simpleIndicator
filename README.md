# ViewPagerIndicator
## 一、自定义属性解释
1. tab_text_size，tab字体大小，format="reference|dimension" 
2. tab_text_color， tab默认字体颜色，format="reference|color"
3. tab_selected_text_color，tab选中字体颜色，format="reference|color
4. indicator_color，指针颜色，format="reference|color"
5. indicator_height，指针高度，format="reference|dimension"

## 二、用法
    <com.example.simpletab.CmmViewPagerIndicator
        android:id="@+id/simpleIndicator"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:scrollbars="none"
        app:indicator_color="#0000ff"
        app:indicator_height="2dp"
        app:tab_Selected_text_color="#00ff00"
        app:tab_text_color="#ff0000"
        app:tab_text_size="16sp" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1px"
        android:background="#00ff00" />

    <android.support.v4.view.ViewPager
        android:id="@+id/viewPager"
        android:layout_width="match_parent"
        android:layout_height="match_parent" >
    </android.support.v4.view.ViewPager>
    
## 三、注意事项
1. 必须使用indicator的setOnPageChangeListener设置ViewPager的OnPageChangeListener
2. indicator的setViewPager方法必须放在ViewPager的setAdapter方法后


