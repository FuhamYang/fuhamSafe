package com.yang.fuhamsafe.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;
//检测服务是否存在
public class ServiceStatusUtils {
	public static boolean isServiceRunning(Context context,String serviceName) {
		//获取activity管理器
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统正在运行的服务信息，最多100个
		List<RunningServiceInfo> runningServiceInfos =  activityManager.getRunningServices(100);
		for (RunningServiceInfo runningServiceInfo : runningServiceInfos) {
			//将服务名字对比,相同则判断该服务存在
			if (serviceName.equals(runningServiceInfo.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
