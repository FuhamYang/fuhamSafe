package com.yang.fuhamsafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yang.fuhamsafe.activity.SplashActivity;
//监听开机的广播
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// 启动Acivity，实现开机自启动
		Intent it = new Intent(context, SplashActivity.class);
		// 设置开启新的任务栈；因为之前直接点击应用
		// 开启时系统会自动创建；而现在要自己进行创建
		it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(it);
		
		SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		//是否
		boolean openTheftproof = sharedPreferences.getBoolean("open_theftproof", false);
		
		if (openTheftproof) {	
			//获取之前存储的sim卡序列号
			//String simSerialNumber = sharedPreferences.getString("simSerialNumber", null);
			String simSerialNumber = "111";
			if (!TextUtils.isEmpty(simSerialNumber)) {
				//获取当前sim卡序列号
				//TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				if ("dskdkd".equals(simSerialNumber)) {
					System.out.println("手机安全！");
				}else {
					System.out.println("手机危险！");
					/*String phone = sharedPreferences.getString("phone_number", "");
					//发送短信
					SmsManager smsManager = SmsManager.getDefault();
					smsManager.sendTextMessage(phone, null, "手机卡已改变！", null, null);
			*/	}
			}
		}
		
	}

}
