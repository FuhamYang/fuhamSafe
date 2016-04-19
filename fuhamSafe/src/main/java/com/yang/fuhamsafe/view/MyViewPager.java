package com.yang.fuhamsafe.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {
	
	public static boolean isCanScroll = true;
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	//重写触摸事件方法，控制页面滑动
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//如果是不能滑动且在滑动中
		if (!isCanScroll && ev.getAction() == MotionEvent.ACTION_MOVE) {
			//返回false事件处理依然会继续向下进行传递，返回true则消费了事件，不再向下传递
			return false;
		} else {
			return super.onTouchEvent(ev);
		}
	}
	

}

