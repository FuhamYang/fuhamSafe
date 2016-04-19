package com.yang.fuhamsafe.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.view.SettingItemLayout;

public class SetStep2Fragment extends Fragment {
	private SettingItemLayout silItem;
	private SharedPreferences sharedPreferences;
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_set_step2, null);

		
		// 获取sharedPreferences来存储数据
		sharedPreferences = getActivity().getSharedPreferences("config", Context.MODE_PRIVATE);
		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是自动更新的
		String simSerialNumber = sharedPreferences.getString("simSerialNumber", "false");
		silItem = (SettingItemLayout) view.findViewById(R.id.sil_Item);

		if (!simSerialNumber.equals("false")) {
			silItem.setCheckBox(true);
			
		} else {
			silItem.setCheckBox(false);
		}

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		silItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (silItem.isCheckBox()) {
					silItem.setCheckBox(false);
					sharedPreferences.edit().remove("simSerialNumber")
					.commit();
				} else {
					silItem.setCheckBox(true);
					//获取电话管理器,需要获取读手机状态的权限
					TelephonyManager telephonyManager = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
					//获取sim卡序列号
					String simSerialNumber = telephonyManager.getSimSerialNumber();					
					sharedPreferences.edit().putString("simSerialNumber", simSerialNumber)
							.commit();
				}
				
			}
		});

		return view;
	}
	
	public boolean getIsCheckBox(){
		
		return silItem.isCheckBox();
		
	}
	
	
}
