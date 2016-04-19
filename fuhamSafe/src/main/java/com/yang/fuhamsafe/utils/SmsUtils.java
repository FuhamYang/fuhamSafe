package com.yang.fuhamsafe.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by fuhamyang on 2015/12/20.
 */
public class SmsUtils {

    public interface BackupCallBackParameter{
        public void before(int count);
        public void onBackup(int progress);
    }

    public static boolean backUp(Context context,BackupCallBackParameter callBack){
        //判断sd卡是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日");//设置日期格式
            File file = new File(Environment.getExternalStorageDirectory(),"短信备份：" + df.format(new Date())+".xml");
            SharedPreferences sharedPreferences = context.getSharedPreferences("config",Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("latest_sms_backup",df.format(new Date())).commit();
            FileOutputStream fileOutputStream = null;
            ContentResolver contentResolver = context.getContentResolver();

            //获取短信的内容提供者的识别路径
            Uri uri = Uri.parse("content://sms/");
            //type = 1 接收短信
            //type = 2 发送短信
            Cursor cursor = contentResolver.query(uri,
                    new String[]{"address","date","type","body"},null,null,null);
            int count = cursor.getCount();
            callBack.before(count);
            try {
                fileOutputStream = new FileOutputStream(file);
                //获取xml序列化器
                XmlSerializer xmlSerializer = Xml.newSerializer();
                //设置序列化器写入的文件流，以及编码格式
                xmlSerializer.setOutput(fileOutputStream, "utf-8");
                //2、true表示独立文件，即该xml只有一文件，没有其他文件关联
                xmlSerializer.startDocument("utf-8", true);
                xmlSerializer.startTag(null, "smss");
                //设置smss中的参数
                xmlSerializer.attribute(null,"size",count+"");
                int progress = 0;
                while (cursor.moveToNext()){

                    xmlSerializer.startTag(null, "sms");

                    xmlSerializer.startTag(null, "address");
                    xmlSerializer.text(cursor.getString(cursor.getColumnIndex("address")));
                    xmlSerializer.endTag(null, "address");

                    xmlSerializer.startTag(null, "type");
                    xmlSerializer.text(cursor.getString(cursor.getColumnIndex("type")));
                    xmlSerializer.endTag(null, "type");

                    xmlSerializer.startTag(null, "body");
                    xmlSerializer.text(
                            AES.encrypt
                                    ("yang",cursor.getString(cursor.getColumnIndex("body")).
                                            replaceAll("[\\x00-\\x08\\x0b-\\x0c\\x0e-\\x1f]", "")));
                    xmlSerializer.endTag(null, "body");

                    xmlSerializer.startTag(null, "date");
                    xmlSerializer.text(cursor.getString(cursor.getColumnIndex("date")));
                    xmlSerializer.endTag(null,"date");

                    xmlSerializer.endTag(null,"sms");
                    progress++;

                    callBack.onBackup(progress);

                }
                xmlSerializer.endTag(null,"smss");
                xmlSerializer.endDocument();

                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {

                if (fileOutputStream != null){
                    try {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
        return false;
    }
}
