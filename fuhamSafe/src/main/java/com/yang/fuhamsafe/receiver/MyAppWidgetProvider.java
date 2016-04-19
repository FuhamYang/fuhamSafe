package com.yang.fuhamsafe.receiver;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.yang.fuhamsafe.service.WidgetService;

public class MyAppWidgetProvider extends AppWidgetProvider {

    //当前广播的生命周期只有10秒钟，因此耗时操作必须放到一个新的服务中执行
    //流程：
    //1、注册AppWidgetProvider（也是继承了广播），当用户在新建桌面
    //   小控件时，会收到广播；收到广播后启动新的服务。
    //2、该服务用来不断地按照一定的时间间隔去发送广播

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("日了狗！！！！！！！###1111111111");
        super.onReceive(context, intent);

    }



    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("日了狗！！！！！！！###222222");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        System.out.println("日了狗！！！！！！！###3333");
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        System.out.println("日了狗！！！！！！！###44444");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Intent intent = new Intent(context, WidgetService.class);
        context.startService(intent);
        System.out.println("日了狗！！！！！！！###55555");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        System.out.println("日了狗！！！！！！！###66666");
        Intent intent = new Intent(context, WidgetService.class);
        context.stopService(intent);
    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        System.out.println("日了狗！！！！！！！###77777");
    }
}
