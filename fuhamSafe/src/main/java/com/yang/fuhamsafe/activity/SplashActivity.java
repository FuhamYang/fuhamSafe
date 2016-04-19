package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.dao.AntivirusDao;
import com.yang.fuhamsafe.service.CallProtectService;
import com.yang.fuhamsafe.service.DesktopService;
import com.yang.fuhamsafe.service.InPhoneService;
import com.yang.fuhamsafe.service.WatchDogService;
import com.yang.fuhamsafe.utils.StreamUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends Activity {
	protected static final int UPDATE_DIALOG = 0;
	protected static final int URL_ERRO = 1;
	protected static final int PROTOCOL_ERRO = 2;
	protected static final int IO_ERRO = 3;
	protected static final int JSON_ERRO = 4;
	protected static final int ENTER_HOME = 5;
	private TextView versionTextView;
	private TextView processTextView;
	private String mVersionName;
	private String mDescription;
	private String mDownloadUrl;
	private int mVersionCode;
	boolean flag = false;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// 判断出现哪种错误
			switch (msg.what) {
				case UPDATE_DIALOG:
					showUpdateDailog();
					break;
				case URL_ERRO:
					Toast.makeText(SplashActivity.this, "URL错误！",
							Toast.LENGTH_SHORT).show();
					enterHome();
					break;
				case PROTOCOL_ERRO:
					Toast.makeText(SplashActivity.this, "链接错误！", Toast.LENGTH_SHORT)
							.show();
					enterHome();
					break;
				case IO_ERRO:
					Toast.makeText(SplashActivity.this, "网络错误！", Toast.LENGTH_SHORT)
							.show();
					enterHome();
					break;
				case JSON_ERRO:
					Toast.makeText(SplashActivity.this, "数据解析错误！",
							Toast.LENGTH_SHORT).show();
					enterHome();
					break;
				case ENTER_HOME:
					enterHome();
					break;
				default:
					break;
			}
		};
	};
	private SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
		// 从资源文件中将数据库拷贝到文件夹中
		copyDB("antivirus.db");
		copyDB("address.db");
		//创建快捷方式
		createShortCut();

		//更新病毒数据库
		AntivirusDao.update();
		//检查悬浮窗设置
		openWindowsDialog();
		//检查黑名单设置
		isOpenBlackNumber();
		//初始化背景图片
		initBackground();
		//检查程序锁设置
		checkIsOpenAppLock();
		//获取root权限
		upgradeRootPermission();

		RelativeLayout splashLayout = (RelativeLayout) findViewById(R.id.splashLayout);
		processTextView = (TextView) findViewById(R.id.tv_process);
		versionTextView = (TextView) findViewById(R.id.tv_version);
		versionTextView.setText("版本" + getVersionName());
		// 检查应用锁设置
		checkInPhoneSet();
		//检查是否自动更新应用
		checkIsAutoUpdate();

		// 渐变动画效果
		AlphaAnimation anim = new AlphaAnimation(0.2f, 1f);
		anim.setDuration(2000);
		splashLayout.startAnimation(anim);

	}




	// 检查版本更新的设置
	public void checkIsAutoUpdate() {
		boolean autoUpdate = sharedPreferences.getBoolean("auto_update", true);
		// 判断是否需要更新
		if (autoUpdate) {
			checkVersion();
		} else {
			// 延迟发送消息
			handler.sendEmptyMessageDelayed(ENTER_HOME, 2000);
		}

	}

	// 获取版本名字
	private String getVersionName() {
		// 获取包管理器
		PackageManager packageManager = getPackageManager();
		try {
			// 获取包信息
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			// 获取版本号
			// int versionCode = packageInfo.versionCode;
			// 获取版本名
			String versionName = packageInfo.versionName;
			return versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			// 找不到包名会抛此异常
			e.printStackTrace();
		}
		return null;
	}

	// 获取版本号
	private int getVersionCode() {
		// 获取包管理器
		PackageManager packageManager = getPackageManager();
		try {
			// 获取包信息
			PackageInfo packageInfo = packageManager.getPackageInfo(
					getPackageName(), 0);
			// 获取版本号
			int versionCode = packageInfo.versionCode;

			return versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			// 找不到包名会抛此异常
			e.printStackTrace();
		}
		return 0;
	}

	// 检查版本
	private void checkVersion() {
		final long startTime = System.currentTimeMillis();
		// 在子线程中进行耗时操作
		new Thread() {
			@Override
			public void run() {

				Message message = handler.obtainMessage();
				HttpURLConnection httpURLConnection = null;
				try {
					URL url = new URL("http://10.0.2.2:8080/update.json");
					httpURLConnection = (HttpURLConnection) url
							.openConnection();
					httpURLConnection.setRequestMethod("GET");
					httpURLConnection.setConnectTimeout(5000);
					httpURLConnection.setReadTimeout(5000);
					httpURLConnection.connect();
					if (httpURLConnection.getResponseCode() == 200) {
						InputStream inputStream = httpURLConnection
								.getInputStream();
						String result = StreamUtils.getFromStream(inputStream);
						// 解析json
						JSONObject jsonObject = new JSONObject(result);
						mVersionName = jsonObject.getString("versionName");
						mDescription = jsonObject.getString("description");
						mDownloadUrl = jsonObject.getString("downloadUrl");
						mVersionCode = jsonObject.getInt("versionCode");

						// 判断是否要更新
						if (mVersionCode > getVersionCode()) {
							message.what = UPDATE_DIALOG;
						} else {
							long endTime = System.currentTimeMillis();
							if (endTime - startTime < 2000)
								Thread.sleep(2000 - (endTime - startTime));

							message.what = ENTER_HOME;
						}

					}

				} catch (MalformedURLException e) {
					// url错误异常
					message.what = URL_ERRO;
					e.printStackTrace();
				} catch (ProtocolException e) {
					// 链接错误异常
					message.what = PROTOCOL_ERRO;
					e.printStackTrace();
				} catch (IOException e) {
					// 网络错误异常
					message.what = IO_ERRO;
					e.printStackTrace();
				} catch (JSONException e) {
					// 数据解析错误异常
					message.what = JSON_ERRO;
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					handler.sendMessage(message);

					if (httpURLConnection != null) {
						httpURLConnection.disconnect();
					}
				}
			}

		}.start();

	}

	// 设置更新弹窗
	private void showUpdateDailog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("最新版本：" + mVersionName);
		builder.setMessage(mDescription);
		builder.setPositiveButton("马上更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {

				download();
			}

		});
		builder.setNegativeButton("稍后", new OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});
		// 用户既不点击更新，也不点击取消时，直接点返回键时会调用此方法
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				enterHome();
			}
		});

		builder.show();

	}

	// 进入主页面
	private void enterHome() {

		Intent intent = new Intent(this, HomeActivity.class);
		// 图片没有初始化完成，则休眠
		while (!flag) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		startActivity(intent);
		finish();
	}

	// 下载新版本的安装包
	private void download() {
		// TODO Auto-generated method stub
		HttpUtils utils = new HttpUtils();
		processTextView.setVisibility(View.VISIBLE);

		utils.download(
				// 下载地址
				mDownloadUrl,
				// 存放路径
				"sdcard/update.apk",

				new RequestCallBack<File>() {
					//arg0是下载成功之后的文件信息
					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// 调用系统的安装器
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.addCategory(Intent.CATEGORY_DEFAULT);
						// 设置数据：文件；设置数据格式
						//arg0.result是下载成功后的文件文件
						intent.setDataAndType(Uri.fromFile(arg0.result),
								"application/vnd.android.package-archive");
						// 当退出此activity时，会调用onActivityResult方法
						startActivityForResult(intent, 0);

					}

					// 文件下载进度
					@Override
					public void onLoading(long total, long current,
										  boolean isUploading) {
						// TODO Auto-generated method stub
						super.onLoading(total, current, isUploading);
						// 设置进度可见
						processTextView.setText("下载进度:" + current * 100 / total
								+ "%");

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// TODO Auto-generated method stub
						System.out.println(arg1);
						Toast.makeText(SplashActivity.this, "下载失败!",
								Toast.LENGTH_SHORT).show();
					}
				});
	}
	//当从上一个activity中,返回会该activity时调用
	// 防止用户在弹出更新提醒时点击返回键后，卡死在闪屏页面
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		enterHome();
		super.onActivityResult(requestCode, resultCode, data);

	}

	// 初始化背景图片
	private void initBackground() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {

				File imagefile = new File(getFilesDir(), "background.png");
				// 如果背景图片没有初始化，则初始化背景图片
				if (!imagefile.exists()) {
					// 获取原始图片
					Bitmap bitmap = BitmapFactory.decodeResource(
							getResources(), R.drawable.secai);
					// 将图片进行处理，再写入内存中
					storeImageToSDCARD(blurBitmap(bitmap), "background.png");

				}
				// flag用于与另外一个线程进行同步
				flag = true;
			};
		}.start();
	}

	// 将图片进行处理
	public Bitmap blurBitmap(Bitmap bitmap) {

		// Let's create an empty bitmap with the same size of the bitmap we want
		// to blur
		//新建一张空白的与原图同样大小的图片
		Bitmap outBitmap = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);

		// Instantiate a new Renderscript
		//新建一个渲染脚本
		RenderScript rs = RenderScript.create(getApplicationContext());

		// Create an Intrinsic Blur Script using the Renderscript
		//新建一个内在模糊脚本
		ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs,
				Element.U8_4(rs));

		// Create the Allocations (in/out) with the Renderscript and the in/out
		// bitmaps
		//创建输入/输出图片的配置
		Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
		Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);

		// Set the radius of the blur
		//设置模糊的程度
		blurScript.setRadius(25.f);

		// Perform the Renderscript
		//执行模糊脚本
		blurScript.setInput(allIn);
		blurScript.forEach(allOut);

		// Copy the final bitmap created by the out Allocation to the outBitmap
		//将处理好的图片拷贝到输出图片
		allOut.copyTo(outBitmap);

		// recycle the original bitmap
		bitmap.recycle();

		// After finishing everything, we destroy the Renderscript.
		rs.destroy();

		return outBitmap;

	}

	// 将图片写入内存中
	public void storeImageToSDCARD(Bitmap bitmapAfter, String ImageName) {
		File imagefile = new File(getFilesDir(), ImageName);

		try {

			FileOutputStream fos = new FileOutputStream(imagefile);
			System.out.print(bitmapAfter);
			//保存文件，1、指定图片格式；2、指定图片的质量（0——100），100的质量最高；3、输出流
			bitmapAfter.compress(Bitmap.CompressFormat.PNG, 100, fos);

			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	//创建快捷方式
	private void createShortCut(){

		Intent intent = new Intent();
		//匹配设置桌面快捷方式的广播
		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		//如果设置为true则可以创建重复的快捷方式
		intent.putExtra("duplicate", false);
		//设置快捷方式名字
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "富汉卫士");
		//设置快捷方式图标
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.img1));

		Intent shortcut = new Intent();
		//由于该intent是由桌面运行，所以不能显示启动，必须隐式启动
		//在要启动的activity中配置启动的action
		shortcut.setAction("yang.shortcut");
		//可能由于该Intent是在桌面应用
		// 运行的，所以系统不会
		//自动添加默认的Category,所以必须自己添加，不添加运行不成功
		shortcut.addCategory("android.intent.category.DEFAULT");
		//将启动快捷方式的intent存放在发送广播的Intent中
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
		//发送广播
		sendBroadcast(intent);

	}

	// 检查来电归属地显示设置
	public void checkIsOpenAppLock() {
		boolean isOpen = sharedPreferences.getBoolean("open_app_lock", true);
		if (isOpen) {
			// 开启服务
			startService(new Intent(SplashActivity.this, WatchDogService.class));
		}
	}
	// 检查应用锁设置
	public void checkInPhoneSet() {
		boolean isOpen = sharedPreferences.getBoolean("in_phone", true);
		if (isOpen) {
			// 开启服务
			startService(new Intent(SplashActivity.this, InPhoneService.class));
		}
	}
	// 获取root权限
	public  boolean upgradeRootPermission() {
		Process process = null;
		DataOutputStream os = null;
		try {
			//getPackageCodePath()获取apk安装路径，并修改它个权限
			String cmd = "chmod 777 " + getPackageCodePath();
			//获取一个具有root权限的进程
			process = Runtime.getRuntime().exec("su");
			//得到进程的输出流
			os = new DataOutputStream(process.getOutputStream());
			//写入要执行的命令
			os.writeBytes(cmd + "\n");
			os.writeBytes("exit\n");
			os.flush();
			process.waitFor();
		} catch (Exception e) {
			return false;
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				process.destroy();
			} catch (Exception e) {
			}
		}
		return true;
	}


	// 从资源文件中将数据库拷贝到文件夹中
	public void copyDB(String dbName) {

		File file = new File(getFilesDir(), dbName);
		if (file.exists()) {
			return;
		}
		InputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = getAssets().open(dbName);
			outputStream = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	// 检查桌面悬浮窗显示设置
	public void openWindowsDialog() {
		boolean open = sharedPreferences.getBoolean("open_windows_dialog", true);
		// 判断是否需要显示悬浮窗
		if (open) {
			startService(new Intent(this, DesktopService.class));
		}

	}

	//检查黑名单设置
	public void isOpenBlackNumber() {
		boolean open = sharedPreferences.getBoolean("open_black_number", false);
		// 判断是否需要显示悬浮窗
		if (open) {
			startService(new Intent(this, CallProtectService.class));
		}
	}

}
