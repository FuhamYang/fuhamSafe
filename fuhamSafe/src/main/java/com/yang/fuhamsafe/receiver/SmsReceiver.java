package com.yang.fuhamsafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

import com.yang.fuhamsafe.R.raw;
import com.yang.fuhamsafe.service.LocationService;

import java.util.ArrayList;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//获取短信数组（短信过长时，会将短信分成几段进行发送，所以是一个数组）
		Object[] objects = (Object[]) intent.getExtras().get("pdus");
		
		for (Object object : objects) {
			//把对象转成字节数组，再转成短信
			SmsMessage message = SmsMessage.createFromPdu((byte[])object);
			//获取发送的号码
			String sendNumber = message.getOriginatingAddress();
			//获取短信内容
			String messgeBody = message.getMessageBody();
			
			System.out.println(sendNumber+messgeBody);
			
			if (messgeBody.equals("baojing")) {
				//第二个文件是要播放的资源文件，可以把资源文件放到raw目录中
				MediaPlayer player = MediaPlayer.create(context, raw.before);
				//设置左右声道为最大值
				player.setVolume(1f, 1f);
				//设置为单曲循环
				player.setLooping(true);

				player.start();
			}
			else if (messgeBody.equals("weizhi")) {
				
				context.startService(new Intent(context,LocationService.class));
				//第一次收到短信时只是开启获取位置的服务
				SharedPreferences sharedPreferences = context.getSharedPreferences("config", Context.MODE_PRIVATE);
				//提示用户正在获取位置信息
				String smsString = sharedPreferences.getString("location", "getting location ....");
				String phoneNumber = sharedPreferences.getString("phone_number", "");
				SmsManager smsManager = SmsManager.getDefault();
		    	//先对短信进行切割
		    	ArrayList<String> contents = smsManager.divideMessage(smsString);
		    	for(String content :contents)
		    	smsManager.sendTextMessage(phoneNumber, null, content, null, null);

				
			}
			else if (messgeBody.equals("shuoping")) {
				//获取设备策略管理服务
				DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//锁屏
				devicePolicyManager.lockNow();
				//重置密码
				devicePolicyManager.resetPassword("123", 0);
			}else if (messgeBody.equals("qingchu")) {
				//获取设备策略管理服务
				DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
				//恢复出厂设置
				devicePolicyManager.wipeData(0);
			}
			
			abortBroadcast();//中断短信的广播
			
		}
		
	}
	
	

}
