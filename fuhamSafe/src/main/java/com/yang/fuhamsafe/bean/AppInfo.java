package com.yang.fuhamsafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by fuhamyang on 2015/12/16.
 */
public class AppInfo {

    private Drawable icon;
    private String appName;
    private long appSize;
    private String packageName;
    private boolean isUserApp;
    private boolean isInExternalStorage;
    private String  sourceDir;
    private String desc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public AppInfo(){

    }

    public String getSourceDir() {
        return sourceDir;
    }

    public void setSourceDir(String sourceDir) {
        this.sourceDir = sourceDir;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public AppInfo(String packageName,Drawable icon, String appName, long appSize, boolean isUserApp, boolean isInExternalStorage) {
        this.icon = icon;
        this.packageName = packageName;
        this.appName = appName;
        this.appSize = appSize;
        this.isUserApp = isUserApp;
        this.isInExternalStorage = isInExternalStorage;
    }
    public AppInfo(String packageName,Drawable icon, String appName, long appSize, boolean isUserApp, boolean isInExternalStorage,String path) {
        this.icon = icon;
        this.packageName = packageName;
        this.appName = appName;
        this.appSize = appSize;
        this.isUserApp = isUserApp;
        this.isInExternalStorage = isInExternalStorage;
        this.sourceDir = path;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getAppSize() {
        return appSize;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    public boolean isInExternalStorage() {
        return isInExternalStorage;
    }

    public void setIsInExternalStorage(boolean isInExternalStorage) {
        this.isInExternalStorage = isInExternalStorage;
    }
}
