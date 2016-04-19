package com.yang.fuhamsafe.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fuhamyang on 2015/12/12.
 */
public class SafeOpenHelper extends SQLiteOpenHelper {

    public SafeOpenHelper(Context context) {
        //构造数据库
        super(context, "safe.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        db.execSQL("create table black_number(_id integer primary key autoincrement,number char(20),type char(1))");
        db.execSQL("create table locked_app(_id integer primary key autoincrement,name char(20))");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
