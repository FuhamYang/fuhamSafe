package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.service.CallProtectService;
import com.yang.fuhamsafe.service.DesktopService;
import com.yang.fuhamsafe.service.InPhoneService;
import com.yang.fuhamsafe.service.WatchDogService;
import com.yang.fuhamsafe.view.SettingItemLayout;
import com.yang.fuhamsafe.view.SettingToastStyleLayout;

public class SettingActivity extends Activity {
	private static final String[] styles = new String[] { "半透明", "活力橙", "卫士蓝",
			"金属灰", "苹果绿" };
	private SettingItemLayout silItem;
	private SettingItemLayout inPhoneItem;
	private SettingItemLayout windowsDialogItem;
	private SharedPreferences sharedPreferences;
	private SettingToastStyleLayout settingToastStyleLayout;
	private SettingItemLayout blackNumberItem;
	private SettingItemLayout appLockItem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		// 获取sharedPreferences来存储数据
		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		autoUpdateSet();
		inPhoneSet();
		isOpenAppLock();
		isOpenBlackNumber();
		addressStyleSetting();
		setAddressLocation();
		openWindowsDialog();
	}

	//设置是否开启黑名单
	public void isOpenBlackNumber(){
		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是开启
		boolean isOpen = sharedPreferences.getBoolean("open_black_number", true);
		blackNumberItem = (SettingItemLayout) findViewById(R.id.black_number_Item);

		if (isOpen) {
			blackNumberItem.setCheckBox(true);

		} else {
			blackNumberItem.setCheckBox(false);
		}

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		blackNumberItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (blackNumberItem.isCheckBox()) {
					blackNumberItem.setCheckBox(false);
					sharedPreferences.edit().putBoolean("open_black_number", false)
							.commit();
					// 关闭服务
					stopService(new Intent(SettingActivity.this,
							CallProtectService.class));
				} else {
					blackNumberItem.setCheckBox(true);
					sharedPreferences.edit().putBoolean("open_black_number", true)
							.commit();
					// 开启服务
					startService(new Intent(SettingActivity.this,
							CallProtectService.class));
				}
			}
		});
	}

	//设置是否开启黑名单
	public void isOpenAppLock(){
		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是开启
		boolean isOpen = sharedPreferences.getBoolean("open_app_lock", true);
		appLockItem = (SettingItemLayout) findViewById(R.id.app_lock_Item);

		if (isOpen) {
			appLockItem.setCheckBox(true);

		} else {
			appLockItem.setCheckBox(false);
		}

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		appLockItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (appLockItem.isCheckBox()) {
					appLockItem.setCheckBox(false);
					sharedPreferences.edit().putBoolean("open_app_lock", false)
							.commit();
					// 关闭服务
					stopService(new Intent(SettingActivity.this,
							WatchDogService.class));
				} else {
					appLockItem.setCheckBox(true);
					sharedPreferences.edit().putBoolean("open_app_lock", true)
							.commit();
					// 开启服务
					startService(new Intent(SettingActivity.this,
							WatchDogService.class));
				}
			}
		});
	}

	// 设置是否开启归属地提示
	public void inPhoneSet() {
		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是开启
		boolean isOpen = sharedPreferences.getBoolean("in_phone", true);
		inPhoneItem = (SettingItemLayout) findViewById(R.id.in_phone_Item);

		if (isOpen) {
			inPhoneItem.setCheckBox(true);

		} else {
			inPhoneItem.setCheckBox(false);
		}

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		inPhoneItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (inPhoneItem.isCheckBox()) {
					inPhoneItem.setCheckBox(false);
					sharedPreferences.edit().putBoolean("in_phone", false)
							.commit();
					// 关闭服务
					stopService(new Intent(SettingActivity.this,
							InPhoneService.class));
				} else {
					inPhoneItem.setCheckBox(true);
					sharedPreferences.edit().putBoolean("in_phone", true)
							.commit();
					// 开启服务
					startService(new Intent(SettingActivity.this,
							InPhoneService.class));
				}
			}
		});
	}
	//桌面悬浮窗显示设置
	public void openWindowsDialog() {
		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是显示悬浮窗的
		boolean autoUpdate = sharedPreferences.getBoolean("open_windows_dialog", true);
		windowsDialogItem = (SettingItemLayout) findViewById(R.id.windows_dialog_Item);

		if (autoUpdate) {
			windowsDialogItem.setCheckBox(true);

		} else {
			windowsDialogItem.setCheckBox(false);
		}

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		windowsDialogItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (windowsDialogItem.isCheckBox()) {
					windowsDialogItem.setCheckBox(false);
					sharedPreferences.edit().putBoolean("open_windows_dialog", false)
							.commit();
					stopService(new Intent(SettingActivity.this,DesktopService.class));
				} else {
					windowsDialogItem.setCheckBox(true);
					sharedPreferences.edit().putBoolean("open_windows_dialog", true)
							.commit();
					startService(new Intent(SettingActivity.this,DesktopService.class));


				}
			}
		});
	}

	// 设置是否开启自动更新
	public void autoUpdateSet() {

		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是自动更新的
		boolean autoUpdate = sharedPreferences.getBoolean("auto_update", true);
		silItem = (SettingItemLayout) findViewById(R.id.sil_Item);

		if (autoUpdate) {
			silItem.setCheckBox(true);

		} else {
			silItem.setCheckBox(false);
		}

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		silItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (silItem.isCheckBox()) {
					silItem.setCheckBox(false);
					sharedPreferences.edit().putBoolean("auto_update", false)
							.commit();
				} else {
					silItem.setCheckBox(true);
					sharedPreferences.edit().putBoolean("auto_update", true)
							.commit();

				}
			}
		});

	}

	// 设置归属地提示样式
	public void addressStyleSetting() {

		// 获取存储的参数，如果没有则设置默认值为true,也就是默认设置是自动更新的
		int style = sharedPreferences.getInt("address_style", 0);
		settingToastStyleLayout = (SettingToastStyleLayout) findViewById(R.id.address_style_Item);
		settingToastStyleLayout.setInfo(styles[style]);

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		settingToastStyleLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				showStyleSelectDialog();
			}
		});

	}

	// 设置归属地提示样式
	public void setAddressLocation() {

		settingToastStyleLayout = (SettingToastStyleLayout) findViewById(R.id.address_location_Item);
		settingToastStyleLayout.setInfo("点击设置提示框位置");

		// 给自定义组合控件添加点击事件，不要忘了在layout中设置checkbox的属性
		settingToastStyleLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(SettingActivity.this,
						SetAddressLocationActivity.class));
			}
		});

	}

	// 弹出样式选择框
	protected void showStyleSelectDialog() {
		int style = sharedPreferences.getInt("address_style", 0);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		// 设置图标
		builder.setIcon(R.drawable.art);
		builder.setTitle("归属地提示风格");
		builder.setSingleChoiceItems(styles, style,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						sharedPreferences.edit().putInt("address_style", which)
								.commit();
						settingToastStyleLayout.setInfo(styles[which]);
						stopService(new Intent(SettingActivity.this,
								InPhoneService.class));
						startService(new Intent(SettingActivity.this,
								InPhoneService.class));
						dialog.dismiss();

					}
				});
		builder.setNegativeButton("取消", null);
		builder.show();

	}

}
