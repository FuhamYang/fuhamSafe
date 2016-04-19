package com.yang.fuhamsafe.service;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.engine.AppProcessInfos;
import com.yang.fuhamsafe.receiver.MyAppWidgetProvider;

import java.util.Timer;
import java.util.TimerTask;

public class WidgetService extends Service {
    public WidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //每隔5秒更新一次桌面小控件
        //初始化定时器
        Timer timer = new Timer();
        //初始化定时任务
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //初始化一个远程的View,显示到桌面小控件
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
                AppProcessInfos.getAppProcessInfos(WidgetService.this);
                //这里并没有findviewbyid这个方法
                //通过以下方式设置控件的值
                views.setTextViewText(R.id.process_count,"正在运行的进程：" + AppProcessInfos.count);
                views.setTextViewText(R.id.process_memory,"可用空间："+ Formatter.formatFileSize(WidgetService.this,AppProcessInfos.availMem));
                //设置点击事件
                Intent intent = new Intent();
                intent.setAction("com.yang.widget");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(),0,intent,0);
                views.setOnClickPendingIntent(R.id.btn_clear,pendingIntent);

                //1、参数上下文；2、当前哪个广播处理当前桌面小控件
                //getApplicationContext() 生命周期是整个应用，应用摧毁它才摧毁
                // Activity.this的context属于activity ，activity 摧毁他就摧毁
                ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
                //获取桌面小控件管理者
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(WidgetService.this);
                //更新桌面小控件
                appWidgetManager.updateAppWidget(provider,views);

            }
        };
        //每隔5秒更新桌面小控件
        timer.schedule(timerTask,0,5000);

    }
}
