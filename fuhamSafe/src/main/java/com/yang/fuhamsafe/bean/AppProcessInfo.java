package com.yang.fuhamsafe.bean;

import android.graphics.drawable.Drawable;

/**
 * Created by fuhamyang on 2015/12/24.
 */
public class AppProcessInfo {

    private Drawable icon;
    private String packageName;
    private String processName;
    private  boolean isUserApp;
    private long size;
    private boolean isCheck;

    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public AppProcessInfo(){

    }

    @Override
    public String toString() {
        return "AppProcessInfo{" +
                "icon=" + icon +
                ", packageName='" + packageName + '\'' +
                ", processName='" + processName + '\'' +
                ", isUserApp=" + isUserApp +
                ", size='" + size + '\'' +
                '}';
    }

    public AppProcessInfo(Drawable icon, String packageName, String processName, boolean isUserApp, long size) {
        this.icon = icon;
        this.packageName = packageName;
        this.processName = processName;
        this.isUserApp = isUserApp;
        this.size = size;
        this.isCheck = false;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setIsUserApp(boolean isUserApp) {
        this.isUserApp = isUserApp;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
