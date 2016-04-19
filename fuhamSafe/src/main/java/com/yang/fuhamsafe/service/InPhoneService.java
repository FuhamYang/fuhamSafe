package com.yang.fuhamsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.dao.AddressDao;

public class InPhoneService extends Service {

	private TelephonyManager telephonyManager;
	private MyListener myListener;
	private OutCallReceiver outCallReceiver;
	private WindowManager windowManager;
	private View view;
	private int style;
	private SharedPreferences sharedPreferences;
	private float startX;
	private float startY;
	private int width;
	private int height;

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
		style = sharedPreferences.getInt("address_style", 0);
		outCallReceiver = new OutCallReceiver();

		// 设置要监听广播的action
		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_NEW_OUTGOING_CALL);
		// 注册广播
		registerReceiver(outCallReceiver, intentFilter);

		telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		myListener = new MyListener();
		// 监听电话的状态
		telephonyManager.listen(myListener,
				PhoneStateListener.LISTEN_CALL_STATE);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 注销去电广播
		unregisterReceiver(outCallReceiver);

		// 取消电话管理器的监听
		telephonyManager.listen(myListener, PhoneStateListener.LISTEN_NONE);
	}

	// 监听去电广播
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			// 获取去电号码
			String number = getResultData();
			System.out.println("广播正在执行###########" + number);

			String address = AddressDao.getAddress(number);

			showMyToast(address);

		}

	}

	public void showMyToast(String text) {
		// 获取窗口管理器
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		width = windowManager.getDefaultDisplay().getWidth();
		height = windowManager.getDefaultDisplay().getHeight();

		// 设置窗口参数
		final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// 将重心位置设置为左上方，即（0，0）从左上方开始，而不是默认的中心位置
		params.gravity = Gravity.LEFT + Gravity.TOP;

		int left = sharedPreferences.getInt("address_location_left", 0);
		int top = sharedPreferences.getInt("address_location_top", 0);
		// 设置已设置的位置
		params.x = left;
		params.y = top;

		params.format = PixelFormat.TRANSLUCENT;
		// 设置窗口不能获取焦点，
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置窗口的权限,需要在配置清单文件添加权限SYSTEM_ALERT_WINDOW
		params.type = WindowManager.LayoutParams.TYPE_PHONE;

		params.setTitle("Toast");

		view = View.inflate(this, R.layout.toast_address, null);
		switch (style) {
		case 0:
			view.setBackgroundResource(R.drawable.call_locate_white);
			break;
		case 1:
			view.setBackgroundResource(R.drawable.call_locate_orange);
			break;
		case 2:
			view.setBackgroundResource(R.drawable.call_locate_blue);
			break;
		case 3:
			view.setBackgroundResource(R.drawable.call_locate_gray);
			break;
		case 4:
			view.setBackgroundResource(R.drawable.call_locate_green);
			break;
		default:
			break;
		}
		TextView textView = (TextView) view.findViewById(R.id.textView1);
		textView.setText(text);
		// 将view添加到屏幕上
		windowManager.addView(view, params);

		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					startX = event.getRawX();
					startY = event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE:

					float endX = event.getRawX();
					float endY = event.getRawY();

					// 计算位移差
					float dx = endX - startX;
					float dy = endY - startY;


					params.x += dx;
					params.y += dy;
					
					// 使用此方法有BUG，移动到边缘时，会缺失部分区域,完善进行边界判断
					// bottom < height 其中bottom并没有加上状态栏的高度，height要减去状态栏的高度
					if (params.x < 0 || params.y < 0 || params.x > width - 5 - view.getWidth()
							|| params.y > height - 38 - view.getHeight() ){
						params.x -= dx;
						params.y -= dy;
					}
					
					windowManager.updateViewLayout(view, params);

					// 重新设置开始位置,注意这个是手指的，不要写成控件的left会有误差的
					startX = endX;
					startY = endY;

					break;
				case MotionEvent.ACTION_UP:
					sharedPreferences
							.edit()
							.putInt("address_location_left",
									params.x).commit();
					sharedPreferences
							.edit()
							.putInt("address_location_top",
									params.y).commit();
					break;
				default:
					break;
				}

				return true;
			}
		});

	}

	class MyListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:

				String address = AddressDao.getAddress(incomingNumber);
				showMyToast(address);
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				if (windowManager != null) {
					windowManager.removeView(view);
					view = null;
				}
				break;
			default:
				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}
	}
}
