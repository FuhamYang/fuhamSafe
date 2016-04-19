package com.yang.fuhamsafe.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.yang.fuhamsafe.R;

public class HelpActivity extends Activity {
	private ObjectAnimator oa;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		//ImageView imageView = (ImageView) findViewById(R.id.imageView1);

		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
		//用户点击屏幕时，关闭当前activity
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		translation(relativeLayout);
	}

	//属性动画改变原来ImageView的位置
	public void translation(RelativeLayout relativeLayout) {
		//1、设置作用于哪个imageView；
		//2、设置要改变的属性
		//3、设置平移的路径
		oa = ObjectAnimator.ofFloat(
				relativeLayout, "translationX", 50,-70,100,-100);
		oa.setDuration(2000);
		oa.setRepeatCount(10);
		oa.setRepeatMode(ValueAnimator.REVERSE);
		oa.start();
	}
}
