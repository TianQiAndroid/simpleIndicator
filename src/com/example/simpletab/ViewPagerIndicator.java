package com.example.simpletab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewPagerIndicator extends HorizontalScrollView {
	private Paint mPaint; // 画指示符的paint
	private int mTop; // 指示符的top
	private int mLeft; // 指示符的left
	private int mWidth; // 指示符的width
	private int mHeight; // 指示符的高度
	private int mIndicatorColor; // 指示符的颜色
	private int mChildCount; // 子item的个数，用于计算指示符的宽度
	private int mTabTextColor; // 未选中文字颜色
	private int mTabSelectedTextColor; // 选中颜色
	private int mTabTextSize; // tab文字的大小

	// 默认可见tab为4
	private int mVisibleTabCount = 4;

	public int getmVisibleTabCount() {
		return mVisibleTabCount;
	}

	public void setmVisibleTabCount(int mVisibleTabCount) {
		this.mVisibleTabCount = mVisibleTabCount;
	}

	// 与指示符相关联的viewPager
	private ViewPager mViewPager;

	// OnPageChangeListener必须通过指示符设定才有效
	private OnPageChangeListener listener;
	/**
	 * 指示符（HorizontalScrollView）包裹的线性布局
	 */
	private LinearLayout mLinearLayout;

	/**
	 * 屏幕的宽度
	 */
	private int mScreenWidth;
	/**
	 * 指示符矩形框
	 */
	private Rect mRect;

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mLinearLayout = new LinearLayout(getContext());
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
		mLinearLayout.setLayoutParams(layoutParams);
		this.addView(mLinearLayout);

		// 必须设置背景，默认情况下，ViewGroup的onDraw方法不会执行
		mLinearLayout.setBackgroundColor(Color.TRANSPARENT);

		// 获取自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator, 0, 0);
		// 默认指示符颜色，红色
		mIndicatorColor = ta.getColor(
				R.styleable.ViewPagerIndicator_indicator_color, Color.RED);
		// 默认指示符高度，4dp
		mHeight = ta.getDimensionPixelSize(
				R.styleable.ViewPagerIndicator_indicator_height, dip2px(4));
		// 默认tab文字大小，12sp
		mTabTextSize = ta.getDimensionPixelSize(
				R.styleable.ViewPagerIndicator_tab_text_size, dip2px(12));
		mTabTextSize = (int) (mTabTextSize / getContext().getResources()
				.getDisplayMetrics().density);
		mTabTextSize = sp2px(mTabTextSize);
		// 默认tab文字颜色，黑色
		mTabTextColor = ta.getColor(
				R.styleable.ViewPagerIndicator_tab_text_color, Color.BLACK);
		// 默认选中tab文字颜色，红色
		mTabSelectedTextColor = ta.getColor(
				R.styleable.ViewPagerIndicator_tab_selected_text_color,
				Color.RED);
		ta.recycle();

		// 初始化paint
		mPaint = new Paint();
		mPaint.setColor(mIndicatorColor);
		mPaint.setAntiAlias(true);

		DisplayMetrics dm = new DisplayMetrics();
		// 获取屏幕信息
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		// 定义indicator矩形
		mRect = new Rect(0, 0, 0, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mTop = getMeasuredHeight(); // 测量的高度即指示符的顶部位置
		int width = mScreenWidth; // 获取屏幕宽度
		int height = mTop + mHeight; // 重新定义一下测量的高度
		if (mChildCount <= mVisibleTabCount) {
			// tag个数少于可见个数，tab宽度为平分屏幕宽度
			mWidth = width / mChildCount;
		} else {
			// tag个数大于可见个数，tab宽度为屏幕宽度除以可见个数
			mWidth = width / mVisibleTabCount;
		}

		// 重新设定tab的LayoutParams
		for (int i = 0; i < mChildCount; i++) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLinearLayout
					.getChildAt(i).getLayoutParams();
			params.weight = 0;
			params.height = height;
			params.width = mWidth;
			mLinearLayout.getChildAt(i).setLayoutParams(params);

		}
		// 重新设置宽高，宽度必须为屏幕宽度，否则HorizontalScrollView不可以滚动
		setMeasuredDimension(mScreenWidth, height);
		// setMeasuredDimension(mWidth * mChildCount, height);
	}

	/**
	 * 指示符滚动
	 * 
	 * @param position
	 *            现在的位置
	 * @param offset
	 *            偏移量 0 ~ 1
	 */
	private void scroll(int position, float offset) {
		mLeft = (int) ((position + offset) * mWidth);

		// 容器滚动，当移动到倒数最后一个的时候，开始滚动
		if (offset > 0 && position >= (mVisibleTabCount - 2)
				&& mChildCount > mVisibleTabCount) {
			this.scrollTo((position - (mVisibleTabCount - 2)) * mWidth
					+ (int) (mWidth * offset), 0);
		}
		// 别忘记调用invalidate，重绘指示符
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mRect.left = mLeft;
		mRect.top = mTop;
		mRect.right = mLeft + mWidth;
		mRect.bottom = mTop + mHeight;
		// 绘制矩形
		canvas.drawRect(mRect, mPaint);
		super.onDraw(canvas);
	}

	/**
	 * 设置与指示符关联的viewPager，该方法必须在viewpager的setAdapter方法后调用
	 * 
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		if (viewPager == null) {
			throw new RuntimeException("ViewPager can not be null.");
		}
		this.mViewPager = viewPager;
		// 获取指示符tab个数
		mChildCount = mViewPager.getAdapter().getCount();
		// 动态创建tab
		for (int i = 0; i < mChildCount; i++) {
			// 创建tab，实际上是一个textview
			TextView tv = new TextView(getContext());
			// 设置tab文字
			tv.setText(mViewPager.getAdapter().getPageTitle(i));
			// 设置tab点击事件
			tv.setOnClickListener(new IndicatorOnclickListener(i));
			// 设置tab的padding
			tv.setPadding(0, dip2px(10), 0, dip2px(10));
			// 设置tab的位置大小
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
			// 设置tab的文字居中
			tv.setGravity(Gravity.CENTER);
			// 将tab添加到指示符中
			mLinearLayout.addView(tv);
		}

		// 初始化tab颜色，第一个选中
		resetColor(0);

		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				resetColor(arg0);
				if (listener != null) {
					listener.onPageSelected(arg0);
				}

			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				scroll(arg0, arg1);
				if (listener != null) {
					listener.onPageScrolled(arg0, arg1, arg2);
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				if (listener != null) {
					listener.onPageScrollStateChanged(arg0);
				}
			}
		});
	}

	/**
	 * 设置OnPageChangeListener
	 * 
	 * @param l
	 */
	public void setOnPageChangeListener(OnPageChangeListener l) {
		this.listener = l;
	}

	/**
	 * indicator点击回调类
	 * 
	 * @author Shilei
	 * 
	 */
	private class IndicatorOnclickListener implements OnClickListener {

		private int position;

		public IndicatorOnclickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			mViewPager.setCurrentItem(position);
			resetColor(position);
		}

	}

	/**
	 * 重置title颜色
	 * 
	 * @param position
	 */
	public void resetColor(int position) {
		for (int i = 0; i < mChildCount; i++) {
			TextView tv = (TextView) mLinearLayout.getChildAt(i);
			if (i == position) {
				tv.setTextColor(mTabSelectedTextColor);
			} else {
				tv.setTextColor(mTabTextColor);
			}
		}
	}

	/**
	 * dip转px
	 * 
	 * @param dpValue
	 * @return
	 */
	private int dip2px(float dpValue) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * sp转px
	 * 
	 * @param spValue
	 * @return
	 */
	private int sp2px(float spValue) {
		float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}