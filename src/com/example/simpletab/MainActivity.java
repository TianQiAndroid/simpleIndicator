package com.example.simpletab;

import java.util.ArrayList;
import java.util.List;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

public class MainActivity extends FragmentActivity {

	private ViewPager mViewPager;
	private ViewPagerIndicator mSimpleIndicator;

	private List<Fragment> mList = new ArrayList<Fragment>();

	private String tabs[];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		tabs = new String[] { "TAB1", "TAB2", "TAB3"};
//		tabs = new String[] { "TAB1", "TAB2", "TAB3", "TAB4" };
		tabs = new String[] { "TAB1", "TAB2", "TAB3", "TAB4","TAB5", "TAB6", "TAB7", "TAB8"  };
		mViewPager = (ViewPager) findViewById(R.id.viewPager);
		mSimpleIndicator = (ViewPagerIndicator) findViewById(R.id.simpleIndicator);
		mList.add(new FragmentOne());
		mList.add(new FragmentTwo());
		mList.add(new FragmentThree());
		mList.add(new FragmentFour());
		mList.add(new FragmentOne());
		mList.add(new FragmentTwo());
		mList.add(new FragmentThree());
		mList.add(new FragmentFour());
		mViewPager.setAdapter(new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			@Override
			public CharSequence getPageTitle(int position) {
				return tabs[position];
			}

			@Override
			public int getCount() {
				return tabs.length;
			}

			@Override
			public Fragment getItem(int arg0) {
				return mList.get(arg0);
			}
		});
		mSimpleIndicator.setViewPager(mViewPager);
		mSimpleIndicator.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

}
