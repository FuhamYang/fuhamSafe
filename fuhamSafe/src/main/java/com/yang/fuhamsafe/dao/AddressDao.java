package com.yang.fuhamsafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {
	private static final String PATH = 
			"data/data/com.yang.fuhamsafe/files/address.db";
	
	public static String getAddress(String number) {
		String address = "未知号码";
		
		//获取数据库对象
		SQLiteDatabase database = SQLiteDatabase.  
				openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = null;
		//匹配手机号，^1表示以1开头，[3-8]表示第二个字符范围，\\d{9}表示9个字符都是数字，$表示结束
		if (number.matches("^1[3-8]\\d{9}$")) {
			cursor = database.
					rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)"
					, new String[]{number.substring(0, 7)});
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			//匹配数字,^\\d+有多个数字
		}else if (number.matches("^\\d+$")) {
			//通过号码的位数来进行判断
			switch (number.length()) {
			case 3:
				address = "报警电话";
				break;
			case 4:
				address = "模拟器";
				break;
			case 5:
				address = "客服电话";
				break;
			case 7:
			case 8:
				address = "本地电话";
				break;
			
			default:
				//有可能是长途电话
				if (number.startsWith("0") && number.length()>10) {
					//有些区号是4位，有些区号是3位（包括0）
					cursor = database.rawQuery("select location from data2 where area = ?"
							, new String[]{number.substring(1, 4)});
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}else {
						cursor.close();
						cursor = database.rawQuery("select location from data2 where area = ?"
								, new String[]{number.substring(1, 3)});
						
					}
				}
				
				break;
			}
		}
		//关闭cursor
		if (cursor != null) {
			cursor.close();
		}
				
		database.close();
		return address;
	}
	
}
