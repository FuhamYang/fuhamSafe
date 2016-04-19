package com.yang.fuhamsafe.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.yang.fuhamsafe.activity.LockPasswordActivity;
import com.yang.fuhamsafe.dao.LockAppDao;

import java.util.ArrayList;
import java.util.List;

public class WatchDogService extends Service {
    private boolean flag;
    private WatchDogReceiver receiver;
    //用于存储已输入正确密码的加锁应用包名
    private List<String> listTempUnlock;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        flag =true;
        listTempUnlock = new ArrayList<>();
        receiver = new WatchDogReceiver();

        IntentFilter filter = new IntentFilter();
        //接收停止保护的广播
        filter.addAction("com.yang.stopprotect");

        //注册一个锁屏的广播
        /**
         * 当屏幕锁住的时候。狗就休息
         * 屏幕解锁的时候。让狗活过来
         */
        filter.addAction(Intent.ACTION_SCREEN_OFF);

        filter.addAction(Intent.ACTION_SCREEN_ON);


        registerReceiver(receiver, filter);

        startWatchDog();
    }
    //临时停止保护的包名
    private String tempStopProtectPackageName = "";

    private class WatchDogReceiver extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals("com.yang.stopprotect")){
                //获取到停止保护的加锁应用的包名
                tempStopProtectPackageName = intent.getStringExtra("packageName");
                //将该包名加入集合
                listTempUnlock.add(tempStopProtectPackageName);
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                System.out.println("锁屏了！！####");
                //锁屏时，将上一次输入正确密码的加锁应用，删除，即再次点亮屏幕时需重新输入密码
                tempStopProtectPackageName = "";
                listTempUnlock.clear();
                // 让狗休息
                flag = false;
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                //让狗继续干活
                System.out.println("开屏了！！####");
                flag = true;
                listTempUnlock = new ArrayList<>();
                startWatchDog();

            }


        }

    }



    private void startWatchDog() {
        new Thread(){
            @Override
            public void run() {
                //获取所有的应用
                List<String> listLock = new LockAppDao(WatchDogService.this).selectAll();

                while(flag){
                    ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                    //获取任务栈中所有的应用
                    List<RunningTaskInfo> list =  activityManager.getRunningTasks(100);
                    //获取栈顶应用
                    RunningTaskInfo runningTaskInfo = list.get(0);
                    //获取栈顶的包名
                    String name = runningTaskInfo.topActivity.getPackageName();
                    //查找是否是加锁程序

                    for (String tempLock:listLock
                         ) {
                        if (tempLock.equals(name)){
                            //判断该加锁应用是否在已输入密码的加锁应用集合中
                            boolean isInList = false;
                            for (String tempUnlock:listTempUnlock
                                    ) {
                                //在集合中
                                if (tempUnlock.equals(name)) {
                                    isInList = true;
                                }
                            }
                            //如果不在集合中要求输入密码
                            if (!isInList) {
                                Intent intent = new Intent(WatchDogService.this, LockPasswordActivity.class);
                                //发送包名
                                intent.putExtra("name", name);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        }

                    }

                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}
