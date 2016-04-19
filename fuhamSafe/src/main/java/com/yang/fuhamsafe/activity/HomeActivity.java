package com.yang.fuhamsafe.activity;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.utils.MD5Utils;

public class HomeActivity extends Activity {

	private GridView gridView;

	private String[] mItems = new String[]{
			"手机防盗","通讯卫士","应用管理","进程管理","流量统计",
			"手机杀毒","缓存清理","高级工具","设置中心"
	};

	private int mImage[] = new int[]{
			R.drawable.shouji,R.drawable.tongxun,R.drawable.ruanjian,
			R.drawable.jincheng,R.drawable.liuliang,R.drawable.shadu,
			R.drawable.huancun,R.drawable.gongju,R.drawable.shezhi
	};

	private SharedPreferences sharedPreferences;

	private String savePassword;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);



		//设置GridView
		gridView = (GridView) findViewById(R.id.gridView1);
		gridView.setAdapter(new HomeAdapter());
		//设置点击条目事件
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// TODO Auto-generated method stub

				switch (position) {
					case 0:
						//手机防盗
						savePassword = sharedPreferences.getString("password", "");
						if (TextUtils.isEmpty(savePassword)) {
							showSetPasswordDialog();
						}else {
							showEntryPasswordDialog();
						}
						break;
					case 1:
						//手机黑名单
						startActivity(new Intent(HomeActivity.this,CallProtectActivity.class));
						break;
					case 2:
						//应用管理
						startActivity(new Intent(HomeActivity.this,AppManageActivity.class));
						break;
					case 3:
						//进程管理
						startActivity(new Intent(HomeActivity.this,ProcessActivity.class));
						break;
					case 5:
						//手机杀毒
						startActivity(new Intent(HomeActivity.this,AntivirusMainActivity.class));
						break;
					case 7:
						//高级工具
						startActivity(new Intent(HomeActivity.this,ToolsActivity.class));
						break;
					case 8:
						//设置中心

						startActivity(new Intent(HomeActivity.this,SettingActivity.class));
						break;

					default:
						break;
				}
			}
		});
	}



	//弹出输入密码对话框
	protected void showEntryPasswordDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dailog_entry_password, null);
		//将布局放到dialog上
		dialog.setView(view);
		dialog.show();

		final EditText passwordEditText = (EditText) view.findViewById(R.id.password_entry);


		Button ok = (Button) view.findViewById(R.id.password_entry_ok);
		Button cancel = (Button) view.findViewById(R.id.password_entry_cancel);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String password = passwordEditText.getText().toString();

				if (!TextUtils.isEmpty(password)) {
					if(MD5Utils.encode(password).equals(savePassword)){
						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this,GuideActivity.class));
					}
					else {
						Toast.makeText(HomeActivity.this, "密码不正确!", Toast.LENGTH_SHORT).show();

					}
				} else {
					//为输入框添加动画效果，2、添加插补器，设置动画的方式
					Animation shake = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.shake);
					passwordEditText.startAnimation(shake);
					//设置手机震动
					Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					//震动1秒
					vibrator.vibrate(1000);
					//1、先等待1秒，再震动2秒，再等待1秒，再震动3秒；
					//2、-1表示只执行一次，0表示从第0个位置开始循环，1表示从第1个位置开始循环
					//vibrator.vibrate(new long[]{1000,2000,1000,3000},-1);
					//取消震动
					//vibrator.cancel();
					Toast.makeText(HomeActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();

				}
			}


		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});


	}


	//弹出设置密码对话框
	protected void showSetPasswordDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(this, R.layout.dailog_set_password, null);
		//将布局放到dialog上
		dialog.setView(view);
		dialog.show();

		final EditText passwordEditText = (EditText) view.findViewById(R.id.password);
		final EditText passwordConfirmEditText = (EditText) view.findViewById(R.id.passwordConfirm);

		Button ok = (Button) view.findViewById(R.id.password_set_ok);
		Button cancel = (Button) view.findViewById(R.id.password_set_cancel);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String password = passwordEditText.getText().toString();
				String passwordConfirm = passwordConfirmEditText.getText().toString();

				// TODO Auto-generated method stub
				if (!TextUtils.isEmpty(password) &&
						!TextUtils.isEmpty(passwordConfirm)) {
					if (password.equals(passwordConfirm)) {
						sharedPreferences.edit().putString("password", MD5Utils.encode(password)).commit();
						dialog.dismiss();
						startActivity(new Intent(HomeActivity.this, GuideActivity.class));
					} else {
						Toast.makeText(HomeActivity.this, "两次密码不相同!", Toast.LENGTH_SHORT).show();

					}
				} else {
					Toast.makeText(HomeActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
				}
			}


		});

		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}


	//GridView适配器
	class HomeAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mItems.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View converView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			//为条目设置值
			View view = View.inflate(HomeActivity.this, R.layout.home_list_item, null);
			ImageView ivItem = (ImageView) view.findViewById(R.id.iv_item);
			TextView tvItem = (TextView)view.findViewById(R.id.tv_item);
			ivItem.setImageResource(mImage[position]);
			tvItem.setText(mItems[position]);
			return view;
		}

	}





}
