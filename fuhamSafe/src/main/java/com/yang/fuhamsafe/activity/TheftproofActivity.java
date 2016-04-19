package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.view.ToggleButton;

public class TheftproofActivity extends Activity {
	private ToggleButton openStatus;
	private TextView openInfo;
	private TextView phoneNumber;
	private SharedPreferences sharedPreferences;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_theftproof);
		//获取之前存储的安全号码
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

		phoneNumber = (TextView) findViewById(R.id.phone_nunber);
		phoneNumber.setText(sharedPreferences.getString("phone_number", ""));

		//获取之前防盗设置（开启或关闭）
		openInfo = (TextView) findViewById(R.id.openInfo);
		openStatus = (ToggleButton) findViewById(R.id.openStatus);
		openStatus.setSlideBackgroundResource(R.drawable.slide_background);
		openStatus.setSwitchBackgroundResource(R.drawable.swich_background_open,
				R.drawable.swich_background_close);
		boolean openTheftproof = sharedPreferences.getBoolean("open_theftproof", false);
		if (openTheftproof) {
			openInfo.setText("已开启防盗保护");
			openStatus.setIsOpen(true);
		}else{
			openInfo.setText("已关闭防盗保护");
			openStatus.setIsOpen(false);
		}
		openStatus.setOnToggleStateChangeListener(new ToggleButton.OnToggleStateChangeListener() {
			@Override
			public void onToggleStateChange(boolean isOpen) {
				if (isOpen) {
					openInfo.setText("已开启防盗保护");
					sharedPreferences.edit().putBoolean("open_theftproof", true).commit();
				} else {
					openInfo.setText("已关闭防盗保护");
					sharedPreferences.edit().remove("open_theftproof").commit();
				}
			}
		});

	}


	public void reEnterGuide(View view){
		startActivity(new Intent(this,GuideActivity.class));
		finish();
	}

}
