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
	private Paint mPaint; // ��ָʾ����paint
	private int mTop; // ָʾ����top
	private int mLeft; // ָʾ����left
	private int mWidth; // ָʾ����width
	private int mHeight; // ָʾ���ĸ߶�
	private int mIndicatorColor; // ָʾ������ɫ
	private int mChildCount; // ��item�ĸ��������ڼ���ָʾ���Ŀ��
	private int mTabTextColor; // δѡ��������ɫ
	private int mTabSelectedTextColor; // ѡ����ɫ
	private int mTabTextSize; // tab���ֵĴ�С

	// Ĭ�Ͽɼ�tabΪ4
	private int mVisibleTabCount = 4;

	public int getmVisibleTabCount() {
		return mVisibleTabCount;
	}

	public void setmVisibleTabCount(int mVisibleTabCount) {
		this.mVisibleTabCount = mVisibleTabCount;
	}

	// ��ָʾ���������viewPager
	private ViewPager mViewPager;

	// OnPageChangeListener����ͨ��ָʾ���趨����Ч
	private OnPageChangeListener listener;
	/**
	 * ָʾ����HorizontalScrollView�����������Բ���
	 */
	private LinearLayout mLinearLayout;

	/**
	 * ��Ļ�Ŀ��
	 */
	private int mScreenWidth;
	/**
	 * ָʾ�����ο�
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

		// �������ñ�����Ĭ������£�ViewGroup��onDraw��������ִ��
		mLinearLayout.setBackgroundColor(Color.TRANSPARENT);

		// ��ȡ�Զ�������
		TypedArray ta = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator, 0, 0);
		// Ĭ��ָʾ����ɫ����ɫ
		mIndicatorColor = ta.getColor(
				R.styleable.ViewPagerIndicator_indicator_color, Color.RED);
		// Ĭ��ָʾ���߶ȣ�4dp
		mHeight = ta.getDimensionPixelSize(
				R.styleable.ViewPagerIndicator_indicator_height, dip2px(4));
		// Ĭ��tab���ִ�С��12sp
		mTabTextSize = ta.getDimensionPixelSize(
				R.styleable.ViewPagerIndicator_tab_text_size, dip2px(12));
		mTabTextSize = (int) (mTabTextSize / getContext().getResources()
				.getDisplayMetrics().density);
		mTabTextSize = sp2px(mTabTextSize);
		// Ĭ��tab������ɫ����ɫ
		mTabTextColor = ta.getColor(
				R.styleable.ViewPagerIndicator_tab_text_color, Color.BLACK);
		// Ĭ��ѡ��tab������ɫ����ɫ
		mTabSelectedTextColor = ta.getColor(
				R.styleable.ViewPagerIndicator_tab_selected_text_color,
				Color.RED);
		ta.recycle();

		// ��ʼ��paint
		mPaint = new Paint();
		mPaint.setColor(mIndicatorColor);
		mPaint.setAntiAlias(true);

		DisplayMetrics dm = new DisplayMetrics();
		// ��ȡ��Ļ��Ϣ
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		// ����indicator����
		mRect = new Rect(0, 0, 0, 0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mTop = getMeasuredHeight(); // �����ĸ߶ȼ�ָʾ���Ķ���λ��
		int width = mScreenWidth; // ��ȡ��Ļ���
		int height = mTop + mHeight; // ���¶���һ�²����ĸ߶�
		if (mChildCount <= mVisibleTabCount) {
			// tag�������ڿɼ�������tab���Ϊƽ����Ļ���
			mWidth = width / mChildCount;
		} else {
			// tag�������ڿɼ�������tab���Ϊ��Ļ��ȳ��Կɼ�����
			mWidth = width / mVisibleTabCount;
		}

		// �����趨tab��LayoutParams
		for (int i = 0; i < mChildCount; i++) {
			LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mLinearLayout
					.getChildAt(i).getLayoutParams();
			params.weight = 0;
			params.height = height;
			params.width = mWidth;
			mLinearLayout.getChildAt(i).setLayoutParams(params);

		}
		// �������ÿ�ߣ���ȱ���Ϊ��Ļ��ȣ�����HorizontalScrollView�����Թ���
		setMeasuredDimension(mScreenWidth, height);
		// setMeasuredDimension(mWidth * mChildCount, height);
	}

	/**
	 * ָʾ������
	 * 
	 * @param position
	 *            ���ڵ�λ��
	 * @param offset
	 *            ƫ���� 0 ~ 1
	 */
	private void scroll(int position, float offset) {
		mLeft = (int) ((position + offset) * mWidth);

		// �������������ƶ����������һ����ʱ�򣬿�ʼ����
		if (offset > 0 && position >= (mVisibleTabCount - 2)
				&& mChildCount > mVisibleTabCount) {
			this.scrollTo((position - (mVisibleTabCount - 2)) * mWidth
					+ (int) (mWidth * offset), 0);
		}
		// �����ǵ���invalidate���ػ�ָʾ��
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mRect.left = mLeft;
		mRect.top = mTop;
		mRect.right = mLeft + mWidth;
		mRect.bottom = mTop + mHeight;
		// ���ƾ���
		canvas.drawRect(mRect, mPaint);
		super.onDraw(canvas);
	}

	/**
	 * ������ָʾ��������viewPager���÷���������viewpager��setAdapter���������
	 * 
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		if (viewPager == null) {
			throw new RuntimeException("ViewPager can not be null.");
		}
		this.mViewPager = viewPager;
		// ��ȡָʾ��tab����
		mChildCount = mViewPager.getAdapter().getCount();
		// ��̬����tab
		for (int i = 0; i < mChildCount; i++) {
			// ����tab��ʵ������һ��textview
			TextView tv = new TextView(getContext());
			// ����tab����
			tv.setText(mViewPager.getAdapter().getPageTitle(i));
			// ����tab����¼�
			tv.setOnClickListener(new IndicatorOnclickListener(i));
			// ����tab��padding
			tv.setPadding(0, dip2px(10), 0, dip2px(10));
			// ����tab��λ�ô�С
			tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
			// ����tab�����־���
			tv.setGravity(Gravity.CENTER);
			// ��tab��ӵ�ָʾ����
			mLinearLayout.addView(tv);
		}

		// ��ʼ��tab��ɫ����һ��ѡ��
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
	 * ����OnPageChangeListener
	 * 
	 * @param l
	 */
	public void setOnPageChangeListener(OnPageChangeListener l) {
		this.listener = l;
	}

	/**
	 * indicator����ص���
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
	 * ����title��ɫ
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
	 * dipתpx
	 * 
	 * @param dpValue
	 * @return
	 */
	private int dip2px(float dpValue) {
		float scale = getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * spתpx
	 * 
	 * @param spValue
	 * @return
	 */
	private int sp2px(float spValue) {
		float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}
}