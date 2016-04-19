package com.yang.fuhamsafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuhamyang on 2015/12/30.
 */
public class LockAppDao {
    private final SafeOpenHelper safeOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    public LockAppDao(Context context) {
        safeOpenHelper = new SafeOpenHelper(context);
    }

    public boolean add(String name){
        sqLiteDatabase = safeOpenHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",name);

        long result = sqLiteDatabase.insert("locked_app", null, contentValues);

        sqLiteDatabase.close();
        if(result != -1){
            return true;
        }else{
            return false;
        }
    }

    public boolean delete(String name){
        sqLiteDatabase = safeOpenHelper.getWritableDatabase();

        long result = sqLiteDatabase.delete("locked_app", "name = ?", new String[]{name});

        sqLiteDatabase.close();
        if(result != -1){
            return true;
        }else{
            return false;
        }
    }

    public List<String> selectAll(){

        sqLiteDatabase = safeOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("locked_app", new String[]{"name"}, null, null, null, null, null);
        List<String> list = new ArrayList<>();
        while(cursor.moveToNext()){
            list.add(cursor.getString(0));
        }
        sqLiteDatabase.close();

        return list;
    }

    public boolean selectByName(String name){

        sqLiteDatabase = safeOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.query("locked_app",new String[]{"name"},"name = ?",new String[]{name},null,null,null);
        List<String> list = new ArrayList<>();
       if(cursor.moveToNext()){
           sqLiteDatabase.close();
           return true;
        }else{
           sqLiteDatabase.close();
           return false;
       }



    }

}
