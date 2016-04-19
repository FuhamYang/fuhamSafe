package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.utils.MD5Utils;

//要将其启动模式，设置为singleInstance,不然会出现，在本程序运行时，点击home键，返回桌面
//再点击加锁程序，跳转到密码输入页面。
// 此时任务栈中：本程序-》密码输入页面；而不是：本程序-》桌面-》加锁程序-》密码输入页面
//这是由于当点击加锁程序时，桌面会被杀死，而加锁程序则被拦截了，没有展示
//所以，由于，activity的启动模式是标准模式，不会新建任务栈，当输完密码时，会直接进入本程序


public class LockPasswordActivity extends Activity {

    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_password);
        //获取看门狗服务传过来的包名
        name = getIntent().getStringExtra("name");

    }

    public void ok(View view){
        EditText editText = (EditText) findViewById(R.id.editText);
        String password = editText.getText().toString();
        SharedPreferences sharedPreferences = getSharedPreferences("config",MODE_PRIVATE);
        String savePassword = sharedPreferences.getString("lock_password", "");
        if (!TextUtils.isEmpty(password)) {
            if(MD5Utils.encode(password).equals(savePassword)){
                //由于已经输入了正确的密码，允许进入加锁应用，所以要发送广播给看门狗，停止保护该加锁应用
                Intent intent = new Intent();
                // 发送停止保护的广播
                intent.setAction("com.yang.stopprotect");
                // 将输入正确密码之后，将包名回传给看门狗，用于判断该加锁程序已输入了正确的密码
                intent.putExtra("packageName", name);

                sendBroadcast(intent);

                finish();

            }
            else {
                Toast.makeText(LockPasswordActivity.this, "密码不正确!", Toast.LENGTH_SHORT).show();

            }
        } else {
            //为输入框添加动画效果，2、添加插补器，设置动画的方式
            Animation shake = AnimationUtils.loadAnimation(LockPasswordActivity.this, R.anim.shake);
            editText.startAnimation(shake);
            //设置手机震动
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            //震动1秒
            vibrator.vibrate(1000);
            //1、先等待1秒，再震动2秒，再等待1秒，再震动3秒；
            //2、-1表示只执行一次，0表示从第0个位置开始循环，1表示从第1个位置开始循环
            //vibrator.vibrate(new long[]{1000,2000,1000,3000},-1);
            //取消震动
            //vibrator.cancel();
            Toast.makeText(LockPasswordActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();

        }

    }

    // 监听当前页面的后退健
    // <intent-filter>
    // <action android:name="android.intent.action.MAIN" />
    // <category android:name="android.intent.category.HOME" />
    // <category android:name="android.intent.category.DEFAULT" />
    // <category android:name="android.intent.category.MONKEY"/>
    // </intent-filter>
    @Override
    public void onBackPressed() {
        // 当用户输入后退健 的时候。我们直接进入到桌面，而不是回到加锁应用的界面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

}
