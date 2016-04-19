package com.yang.fuhamsafe.fragment;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.receiver.AdminReceiver;

public class SetStep4Fragment extends Fragment {
	private Button button;

	private ComponentName componentName;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.fragment_set_step4, null);

		//设备管理组件
		componentName = new ComponentName(getActivity(), AdminReceiver.class);
		button = (Button) view.findViewById(R.id.enterTheftproof);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//隐式启动设备管理器
				Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
				intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
				intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "超级设备管理器值得你拥有！");
				
				startActivity(intent);
			}
		});
		return view;
		
	}
	
	
	
	
	
}
