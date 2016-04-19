package com.yang.fuhamsafe.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.yang.fuhamsafe.bean.Virus;

/**
 * Created by fuhamyang on 2015/12/29.
 */
public class AntivirusDao {
    private static final String PATH =
            "data/data/com.yang.fuhamsafe/files/antivirus.db";

    public static String check(String md5) {
        //获取数据库对象
        SQLiteDatabase database = SQLiteDatabase.
                openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
        Cursor cursor = null;

        cursor = database.query("datable",new String[]{"desc"},"md5 = ?",new String[]{md5},null,null,null);

        if (cursor.moveToNext())
            return cursor.getString(0);
        else
            return "扫描安全";

    }

    public static void add(Virus virus){
        System.out.println("进来add##");
        //获取数据库对象
        SQLiteDatabase database = SQLiteDatabase.
                openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);

        ContentValues contentValues = new ContentValues();
        contentValues.put("md5",virus.getMd5());
        contentValues.put("type",7);
        contentValues.put("name",virus.getName());
        contentValues.put("desc",virus.getDes());

        database.insert("datable", null,contentValues);
        database.close();
    }

    public static void update(){
        System.out.println("进来udate##");
        HttpUtils httpUtils = new HttpUtils();

        String url = "http://10.0.2.2:8080/virus.json";

        httpUtils.send(HttpRequest.HttpMethod.GET, url, new RequestCallBack<Object>() {
            @Override
            public void onSuccess(ResponseInfo<Object> responseInfo) {
                System.out.println("进来success##");
                System.out.println(String.valueOf(responseInfo.result)+"###");
                Gson gson = new Gson();
                Virus virus = gson.fromJson(String.valueOf(responseInfo.result), Virus.class);
                add(virus);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                System.out.println(e + "########" + s);
            }
        });

    }
}