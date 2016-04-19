package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import com.yang.fuhamsafe.R;

public class SmokeActivity extends Activity{
	private AlphaAnimation aa;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_smoke);
		alpha();
		new Thread(){
			@Override
			public void run() {
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finish();
			}
		}.start();
	}
	public void alpha(){
		RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout_smoke);

		//透明度从0.3到1（0是完全透明，1是完全不透明）
		aa = new AlphaAnimation(0, 1);
		aa.setDuration(1000);
		aa.setRepeatCount(1);
		//启动
		relativeLayout.startAnimation(aa);
	}
}
