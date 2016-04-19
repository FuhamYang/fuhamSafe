package com.yang.fuhamsafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yang.fuhamsafe.bean.BlackNumber;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuhamyang on 2015/12/12.
 */
public class BlackNumberDao {

    private final SafeOpenHelper safeOpenHelper;
    private SQLiteDatabase sqLiteDatabase;
    public BlackNumberDao(Context context) {
        safeOpenHelper = new SafeOpenHelper(context);

    }

    public boolean insertBlackNumber(BlackNumber blackNumber){

        sqLiteDatabase = safeOpenHelper.getWritableDatabase();

        //把要插入的数据全部封装在ContentValue中
        ContentValues values = new ContentValues();
        values.put("number", blackNumber.getNumber());
        values.put("type", blackNumber.getType());

        long result = sqLiteDatabase.insert("black_number", null, values);
        sqLiteDatabase.close();
        if(result != -1){
            return true;
        }else{
            return false;
        }

    }

    public boolean deleteBlackNumber(String number){

        sqLiteDatabase = safeOpenHelper.getWritableDatabase();

        int result = sqLiteDatabase.delete("black_number", "number = ?", new String[]{number});
        sqLiteDatabase.close();
        if (result > 0)
            return  true;
        else
            return false;

    }

    public BlackNumber selectByBlackNumebr(String number){
        sqLiteDatabase = safeOpenHelper.getWritableDatabase();

        BlackNumber blackNumber = null;
        Cursor cursor = sqLiteDatabase.query("black_number",null,"number = ?",new String[]{number},null,null,null);
        while(cursor.moveToNext()){
            String numberString = cursor.getString(cursor.getColumnIndex("number"));
            String typeString = cursor.getString(cursor.getColumnIndex("type"));
            blackNumber = new BlackNumber(numberString,typeString);

        }
        cursor.close();
        sqLiteDatabase.close();
        return blackNumber;
    }

    public List<BlackNumber> selectAllBlackNumebr(int pageNumber, int pageSize){

        sqLiteDatabase = safeOpenHelper.getWritableDatabase();
        List<BlackNumber> list = new ArrayList<BlackNumber>();
        //限制查询的数据条数，limit限制数据条数，offset跳过数据条数
        Cursor cursor = sqLiteDatabase.rawQuery("select number,type from black_number limit ? offset ? ", new String[]{pageSize + "", pageNumber * pageSize + ""});
        /*其中pageSize是传入的需要查询数据的条数，pageNumber是传入的当前查询的页数（从0页开始）。
        pageNumber * pageSize就是要跳过的数据条数，当其为0*20时，就是不用跳过数据数，即查询第0页的数据；
        当2*20时，就是要要跳过第0页，第1页的数据，即查询第2页的数据；
        pageNumber * pageSize就是要查询第pageNumber页的数据。*/
        while(cursor.moveToNext()){
            String numberString = cursor.getString(cursor.getColumnIndex("number"));
            String typeString = cursor.getString(cursor.getColumnIndex("type"));
            BlackNumber blackNumber = new BlackNumber(numberString,typeString);
            list.add(blackNumber);
        }
        cursor.close();
        sqLiteDatabase.close();
        return list;
    }

    //取得所有数据的条数
    public int getTotalSize(){
        sqLiteDatabase = safeOpenHelper.getWritableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select count(*) from black_number", null);
        cursor.moveToNext();
        int totalSize = cursor.getInt(0);
        cursor.close();
        sqLiteDatabase.close();
        return totalSize;
    }

    public boolean update(BlackNumber blackNumber){
        sqLiteDatabase = safeOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("type", blackNumber.getType());
        long result = sqLiteDatabase.update("black_number",values,"number = ?",new String[]{blackNumber.getNumber()});
        sqLiteDatabase.close();
        if(result > 0)
            return true;
        else
            return false;
    }
}
