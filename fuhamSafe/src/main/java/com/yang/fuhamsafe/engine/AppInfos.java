package com.yang.fuhamsafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.yang.fuhamsafe.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuhamyang on 2015/12/16.
 */
public class AppInfos {
    public static List<AppInfo> getAppInfos(Context context){
        List<AppInfo> appInfos = new ArrayList<AppInfo>();

        //获取到包的管理器
        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装的包
        /*参数0 是   表示不接受任何flag信息，当然也能够返回得到一些基本的包信息！，但是如 PERMISSIONS  ，RECEIVERS  等等就返回不了，如果flag值不匹配 而方法中强行获取相对应的值，返回值为Null，已经做过测试
        。
        延伸： PackageManager.GET_ACTIVITIES+ PackageManager.GET_ACTIVITIES    等于 3； 参数中填入3则得到这两个的flag对应的信息。

        注意点 ：PackageManager.GET_ACTIVITIES|
                PackageManager.GET_ACTIVITIES    和上为一样的效果。 因为相或的话 同位只要有一个1则为1，*/
        List<PackageInfo> installedPackageInfos = packageManager.getInstalledPackages(0);

        for(PackageInfo installedPackageInfo :installedPackageInfos ){


            //获取到应用的图标
            Drawable icon = installedPackageInfo.applicationInfo.loadIcon(packageManager);
            //获取应用程序名字
            String appName = installedPackageInfo.applicationInfo.loadLabel(packageManager).toString();
            //获取应用程序包名
            String packageName = installedPackageInfo.packageName;
            //获取应用程序的资源路径
            String sourceDir = installedPackageInfo.applicationInfo.sourceDir;
            File file = new File(sourceDir);
            //应用程序的大小
            long appSize = file.length();

            //获取到安装应用程序的标记
            int flags = installedPackageInfo.applicationInfo.flags;
            //判断是系统应用还是用户应用
            boolean isUserApp;
            if ((flags & ApplicationInfo.FLAG_SYSTEM) !=0 ){
                //表示系统app
                isUserApp = false;
            }else {
                //表示用户APP
                isUserApp = true;
            }

            //判断是sd卡上还是在手机内存
            boolean isInExternalStorage;

            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0){
                //表示在sd卡
                isInExternalStorage = true;
            }else{
                //表示在手机内存
                isInExternalStorage = false;
            }

            AppInfo appInfo = new AppInfo(packageName,icon,appName,appSize,isUserApp,isInExternalStorage,sourceDir);
            appInfos.add(appInfo);
        }

        return  appInfos;
    }
}
