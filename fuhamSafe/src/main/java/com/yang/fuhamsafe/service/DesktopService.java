package com.yang.fuhamsafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.activity.HomeActivity;
import com.yang.fuhamsafe.bean.AppProcessInfo;
import com.yang.fuhamsafe.engine.AppProcessInfos;
import com.yang.fuhamsafe.utils.SuUtil;
import com.yang.fuhamsafe.utils.ToastUtils;
import com.yang.fuhamsafe.view.WindowLayout;

import java.util.List;

public class DesktopService extends Service {

	private WindowManager windowManager;
	private View view;
	private float alpha;
	private ImageView imageViewB;
	private ImageView imageViewT;
	private SharedPreferences sharedPreferences;
	private float startX;
	private float startY;
	private int width;
	private int height;
	private String ram;
	private WindowManager.LayoutParams layoutParams;
	private RelativeLayout relativeLayoutSecond;
	private View viewFirst;
	private WindowManager.LayoutParams params;
	private ImageView imageView;
	private View smokeView;
	private WindowManager smokeWindowManager;
	private TextView ramTextView;
	private long releasedRam = 0;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//点击显示RAM占用情况
			if (msg.what == 0) {
				if (imageView.getVisibility() != View.VISIBLE) {

					getAvalableRamPercent();

					ramTextView.setText(ram);

					viewFirst.setVisibility(View.GONE);

					relativeLayoutSecond.setVisibility(View.VISIBLE);
					windowManager.updateViewLayout(view, params);
				}
				//变回只有图标的悬浮窗
			} else if (msg.what == 1) {
				if (imageView.getVisibility() != View.VISIBLE){
					viewFirst.setVisibility(View.VISIBLE);
					relativeLayoutSecond.setVisibility(View.GONE);
					windowManager.updateViewLayout(view, params);
				}

			}else if (msg.what == 2){

				ToastUtils.showToast(DesktopService.this,"清理"+Formatter.formatFileSize(DesktopService.this,releasedRam*1024));

				//移除烟雾，清理进程，并让悬浮窗返回原位置
			} else if (msg.what == 3) {
				upSet();


				//更新window的显示页面
			} else if(msg.what == 4){

				windowManager.updateViewLayout(view, params);
			}else if (msg.what == 5){
				showSmoke();

			}else if(msg.what == 6){
				imageViewB.setAlpha(alpha);
				imageViewT.setAlpha(alpha);
				smokeWindowManager.updateViewLayout(smokeView, layoutParams);
			}else if (msg.what == 7) {
				smokeWindowManager.removeView(smokeView);
			}
		}
	};


	private void getAvalableRamPercent() {
		//获取进程管理者
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		//获取手机上所有正在运行的进程信息
		List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos =
				activityManager.getRunningAppProcesses();

		//用于存储获取到的RAM信息
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		//获取RAM的基本信息
		activityManager.getMemoryInfo(memoryInfo);
		//获取剩余内存所占比例
		ram =  (memoryInfo.availMem*100/memoryInfo.totalMem)+"%";

	}


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
		showMyToast();


	}

	public void showMyToast() {
		// 获取窗口管理器
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

		//获取屏幕长宽
		width = windowManager.getDefaultDisplay().getWidth();
		height = windowManager.getDefaultDisplay().getHeight();

		params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;

		// 将重心位置设置为左上方，即（0，0）从左上方开始，而不是默认的中心位置
		params.gravity = Gravity.LEFT + Gravity.TOP;

		int x = sharedPreferences.getInt("desktop_location_x", 0);
		int y = sharedPreferences.getInt("desktop_location_y", 0);
		// 设置已设置的位置
		params.x = 0;
		params.y = y;

		params.format = PixelFormat.TRANSLUCENT;
		// 设置窗口不能获取焦点，
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置窗口的权限,需要在配置清单文件添加权限SYSTEM_ALERT_WINDOW
		params.type = WindowManager.LayoutParams.TYPE_TOAST;

		params.setTitle("Toast");

		view = View.inflate(this, R.layout.desk_top, null);
		viewFirst = view.findViewById(R.id.viewFirst);
		ramTextView = (TextView)view.findViewById(R.id.textViewRam);
		relativeLayoutSecond = (RelativeLayout) view
				.findViewById(R.id.relativeLayoutSecond);
		imageView = (ImageView) view.findViewById(R.id.ivRockey);

		// 将view添加到屏幕上
		windowManager.addView(view, params);
		// 设置多击事件
		final long[] hits = new long[2];// 数组长度表示要点击多少次，激活事件
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new Thread() {
					public void run() {
						//双击打开应用程序
						// 从源数组的第二个位置开始拷贝，拷贝到目标数组的第一个位置，拷贝长度为总长度-1
						System.arraycopy(hits, 1, hits, 0, hits.length - 1);
						// 记录第一次点击时间
						hits[hits.length - 1] = SystemClock.uptimeMillis();
						// 计算最后一次与第一次点击的时间差
						if (hits[hits.length - 1] - hits[0] <= 500) {
							Intent intent = new Intent(DesktopService.this, HomeActivity.class);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);

						}else{
							//点击显示RAM占用情况
							handler.sendEmptyMessage(0);
							try {
								sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							//变回只有图标的悬浮窗
							handler.sendEmptyMessage(1);

						}



					}
				}.start();

			}
		});
		view.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					viewFirst.setVisibility(View.GONE);
					relativeLayoutSecond.setVisibility(View.GONE);
					//设置图片隐藏后才，要调用此方法才能实现隐藏效果
					windowManager.updateViewLayout(view, params);
					startX = event.getRawX();
					startY = event.getRawY();

					break;
				case MotionEvent.ACTION_MOVE:
					imageView.setBackgroundResource(R.drawable.rokey_animation);
					relativeLayoutSecond.setVisibility(View.GONE);

					AnimationDrawable animationDrawable = (AnimationDrawable) imageView
							.getBackground();
					animationDrawable.start();

					imageView.setVisibility(View.VISIBLE);
					windowManager.updateViewLayout(view, params);
					float endX = event.getRawX();
					float endY = event.getRawY();

					// 计算位移差
					float dx = endX - startX;
					float dy = endY - startY;

					params.x += dx;
					params.y += dy;

					// 使用此方法有BUG，移动到边缘时，会缺失部分区域,完善进行边界判断
					// bottom < height 其中bottom并没有加上状态栏的高度，height要减去状态栏的高度
					if (params.x < 0 || params.y < 0
							|| params.x > width - 5 - view.getWidth()
							|| params.y > height - 38 - view.getHeight()) {
						params.x -= dx;
						params.y -= dy;
					}

					windowManager.updateViewLayout(view, params);

					// 重新设置开始位置,注意这个是手指的，不要写成控件的left会有误差的
					startX = endX;
					startY = endY;

					break;
				case MotionEvent.ACTION_UP:

					if (params.y > height * 0.7) {
						killAllUserProgress();
						//将火箭移动到中间
						params.x = width / 2 - imageView.getWidth() / 2;
						new Thread() {

							public void run() {
								//释放烟雾
								handler.sendEmptyMessage(5);
								int sum = height - 100;
								//计算一秒火箭移动的距离
								int once = sum / 10;
								for (int i = 0; i < 10; i++) {
									sum = sum - once;
									params.y = sum;
									try {
										sleep(120);
									} catch (InterruptedException e) {

										e.printStackTrace();
									}
									//更新window的显示页面
									handler.sendEmptyMessage(4);
								}
								//移除烟雾，清理进程，并让悬浮窗返回原位置
								handler.sendEmptyMessage(3);
							}
						}.start();
					} else {
						sharedPreferences.edit()
								.putInt("desktop_location_x", params.x)
								.commit();
						sharedPreferences.edit()
								.putInt("desktop_location_y", params.y)
								.commit();
						upSet();
					}

					break;
				default:
					break;
				}

				return false;
			}
		});

	}

	public void killAllUserProgress(){
		new Thread(){
			public void run(){
				ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
				//清理所有的用户进程
				List<AppProcessInfo> appProcessInfos = AppProcessInfos.getAppProcessInfos(DesktopService.this);
				releasedRam = 0 ;
				for(AppProcessInfo appProcessInfo : appProcessInfos ){
					if (appProcessInfo.isUserApp() &&
							!appProcessInfo.getPackageName().equals("com.anddoes.launcher")){
						releasedRam = releasedRam + appProcessInfo.getSize();
						//activityManager.killBackgroundProcesses(appProcessInfo.getPackageName());
						SuUtil.kill(appProcessInfo.getPackageName());
					}

				}
				//弹出Toast显示清理了多少内存
				handler.sendEmptyMessage(2);

			}
		}.start();



	}

	public void showSmoke() {

		// 获取窗口管理器
		smokeWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);


		layoutParams = new WindowManager.LayoutParams();
		layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		layoutParams.windowAnimations = R.style.my;
		// 将重心位置设置为左上方，即（0，0）从左上方开始，而不是默认的中心位置
		layoutParams.gravity = Gravity.LEFT + Gravity.TOP;

		// 设置已设置的位置
		layoutParams.x = 0;
		layoutParams.y = 0;

		layoutParams.format = PixelFormat.TRANSLUCENT;
		// 设置窗口不能获取焦点，
		layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		// 设置窗口的权限,需要在配置清单文件添加权限SYSTEM_ALERT_WINDOW
		layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;

		layoutParams.setTitle("Toast");
		layoutParams.windowAnimations = R.anim.window_anim;
		smokeView = View.inflate(this, R.layout.activity_smoke, null);
		imageViewB = (ImageView)smokeView.findViewById(R.id.imageView1);
		imageViewT = (ImageView)smokeView.findViewById(R.id.imageView2);
		imageViewB.setAlpha(0.0f);
		imageViewT.setAlpha(0.0f);
		// 将view添加到屏幕上
		smokeWindowManager.addView(smokeView, layoutParams);


		new Thread() {

			public void run() {

				alpha = 0.0f;

				for (int i = 0; i < 10; i++) {

					try {
						System.out.println("####################我运行了哈哈哈");
						if (i>2)
						alpha = alpha+0.1f;
						sleep(120);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
					//更新烟雾的透明度显示页面
					handler.sendEmptyMessage(6);
				}
				handler.sendEmptyMessage(7);

			}
		}.start();



	}

	//让悬浮窗回到原位
	private void upSet() {
		imageView.setVisibility(View.GONE);
		params.x = 0;
		params.y = sharedPreferences.getInt("desktop_location_y", 0);
		relativeLayoutSecond.setVisibility(View.GONE);
		viewFirst.setVisibility(View.VISIBLE);

		windowManager.updateViewLayout(view, params);

	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		windowManager.removeView(view);

	}
	
}
