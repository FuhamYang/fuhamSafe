package com.yang.fuhamsafe.engine;


import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;
import android.text.format.Formatter;

import com.yang.fuhamsafe.bean.AppProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuhamyang on 2015/12/24.
 */
public class AppProcessInfos {


    public static int count = 0;
    public static String totalMem = "";
    public static long availMem = 0;


    public static List<AppProcessInfo> getAppProcessInfos(Context context){
        //获取进程管理者
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //获取手机上所有正在运行的进程信息
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos =
                activityManager.getRunningAppProcesses();
        //获取运行进程的总数
        count = runningAppProcessInfos.size();
        //用于存储获取到的RAM信息
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        //获取RAM的基本信息
        activityManager.getMemoryInfo(memoryInfo);
        //获取剩余内存
        availMem = memoryInfo.availMem;
        //获取总内存,但是这个不兼容低版本
        totalMem = Formatter.formatFileSize(context, memoryInfo.totalMem);

        //为了兼容低版本，直接到/proc/meminfo
        // 配置文件路径读取配置文件的信息，获取总RAM
        /*File file = new File("/proc/meminfo");
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String readLine = bufferedReader.readLine();
            StringBuffer stringBuffer = new StringBuffer();
            for (char c: readLine.toCharArray()){
                if (c >= '0' && c <= '9')
                    stringBuffer.append(c);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        List<AppProcessInfo> list = new ArrayList<>();

        PackageManager packageManager = context.getPackageManager();
        int sum = 0;
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos
        ){



            //获取进程占用的RAM
            Debug.MemoryInfo[] memoryInfos =
                    activityManager.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
            //脏数据，即占用内存的大小
            long size = memoryInfos[0].dalvikPrivateDirty;

            try {

                String packageName  = runningAppProcessInfo.processName;
                PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
                //其中有一些系统核心进程是用c语言写的，没有图标与程序名，会出错,所以需要捕获
                //出错后，后面的代码都不会执行
                String processName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
                Drawable icon = packageInfo.applicationInfo.loadIcon(packageManager);


                int flags = packageInfo.applicationInfo.flags;

                boolean isUserApp;
                if ((flags & ApplicationInfo.FLAG_SYSTEM) !=0 ){
                    //表示系统app
                    isUserApp = false;
                }else {
                    //表示用户APP
                    isUserApp = true;
                }

                AppProcessInfo appProcessInfo = new AppProcessInfo(icon,packageName,processName,isUserApp,size);
                //不显示程序自身，防止被自身杀死
                if (!context.getPackageName().equals(packageName))
                {
                    list.add(appProcessInfo);
                    sum++;
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                System.out.println("出错了！！没名字！！");
            }

        }
        count = list.size();
        return list;
    }

}
