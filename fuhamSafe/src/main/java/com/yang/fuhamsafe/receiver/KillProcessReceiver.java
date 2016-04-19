package com.yang.fuhamsafe.receiver;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

/**
 * Created by fuhamyang on 2015/12/28.
 */
public class KillProcessReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //获取进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取手机上所有正在运行的进程信息
        List<RunningAppProcessInfo> runningAppProcessInfos =
                activityManager.getRunningAppProcesses();
        for (RunningAppProcessInfo runningAppProcessInfo:
               runningAppProcessInfos ) {
            if (!runningAppProcessInfo.processName.equals(context.getPackageName()))
                activityManager.killBackgroundProcesses(runningAppProcessInfo.processName);
        }

    }
}
