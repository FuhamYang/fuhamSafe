package com.yang.fuhamsafe.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class LocationService extends Service {

	private SharedPreferences sharedPreferences;
	private LocationManager locationManager;
	private MyLocationListener myLocationListener;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		//获取位置管理器
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		Criteria criteria = new Criteria();
		//是否允许付费操作，比如使用网络，发送短信等
		criteria.setCostAllowed(true);
		//设置定位的精确度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		
		//获取当前最合适的位置提供者,1.设置提供者的标准；2.true提供者可用的时候才返回，false不可用时一样返回	
		String bestProvider = locationManager.getBestProvider(criteria, true);
		
		//获取所有位置提供者
		//List<String> allProviders = locationManager.getAllProviders();
		myLocationListener = new MyLocationListener();
		locationManager.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
		
	}
	//位置信息侦听器
	class MyLocationListener implements LocationListener{
		//位置发生改变时
		@Override
		public void onLocationChanged(Location location) {
		
			String j = "经度：" + location.getLongitude();
			String w = "纬度：" + location.getLatitude();
			String accuacy = "精确度：" + location.getAccuracy();
			String altitude = "海拔:" + location.getAltitude();
			System.out.println(j+w+accuacy+altitude);
			//存储位置信息
			sharedPreferences.edit().putString("location", "j:"+ 
			location.getLongitude() + "; w:" + location.getLatitude()).commit();
			//停止服务
			stopSelf();
		}
		//状态发生变化（从能拿到坐标到不能拿到坐标，或从不能拿到坐标到能拿到坐标，的状态转换）
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		//当用户把gps定位打开时
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		//当用户把gps定位关闭时
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//服务停止时，停止更新位置，节省电量
		locationManager.removeUpdates(myLocationListener);
	}
}
