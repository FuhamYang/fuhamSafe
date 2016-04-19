package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.utils.MD5Utils;
import com.yang.fuhamsafe.utils.SmsUtils;
import com.yang.fuhamsafe.utils.ToastUtils;

public class ToolsActivity extends Activity {

    private TextView textView;
    private SharedPreferences sharedPreference;
    private String savePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);
        sharedPreference = getSharedPreferences("config",MODE_PRIVATE);
        textView = (TextView) findViewById(R.id.timeDetail);
        textView.setText("最近备份：" + sharedPreference.getString("latest_sms_backup", "无短信备份"));
    }

    public void lockApp(View view){
        savePassword = sharedPreference.getString("lock_password","");
        if (savePassword.equals("")){
            showSetPasswordDialog();
        }else{
            showEntryPasswordDialog();
        }


    }

    public void backupSms(View view){
        //设置进度弹窗
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("温馨提示");
        progressDialog.setMessage("正在备份短息，请您耐心等待.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
        new Thread(){
            @Override
            public void run() {
                SmsUtils.backUp(ToolsActivity.this, new SmsUtils.BackupCallBackParameter() {
                    @Override
                    public void before(int count) {
                        progressDialog.setMax(count);
                    }

                    @Override
                    public void onBackup(int progress) {
                        progressDialog.setProgress(progress);
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.setMessage("备份完成！");
                        textView.setText("最近备份：" + sharedPreference.getString("latest_sms_backup", "无短信备份"));
                    }
                });

                Looper.prepare();
                ToastUtils.showToast(ToolsActivity.this, "备份完成！");
                Looper.loop();

            }
        }.start();


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

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String password = passwordEditText.getText().toString();

                if (!TextUtils.isEmpty(password)) {
                    if(MD5Utils.encode(password).equals(savePassword)){
                        dialog.dismiss();
                        startActivity(new Intent(ToolsActivity.this,LockAppActivity.class));
                    }
                    else {
                        Toast.makeText(ToolsActivity.this, "密码不正确!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    //为输入框添加动画效果，2、添加插补器，设置动画的方式
                    Animation shake = AnimationUtils.loadAnimation(ToolsActivity.this, R.anim.shake);
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
                    Toast.makeText(ToolsActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();

                }
            }


        });

        cancel.setOnClickListener(new View.OnClickListener() {

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

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String password = passwordEditText.getText().toString();
                String passwordConfirm = passwordConfirmEditText.getText().toString();

                // TODO Auto-generated method stub
                if (!TextUtils.isEmpty(password) &&
                        !TextUtils.isEmpty(passwordConfirm)) {
                    if (password.equals(passwordConfirm)) {
                        sharedPreference.edit().putString("lock_password", MD5Utils.encode(password)).commit();
                        dialog.dismiss();
                        startActivity(new Intent(ToolsActivity.this,LockAppActivity.class ));
                    } else {
                        Toast.makeText(ToolsActivity.this, "两次密码不相同!", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    Toast.makeText(ToolsActivity.this, "密码不能为空!", Toast.LENGTH_SHORT).show();
                }
            }


        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
    }


}
