package com.yang.fuhamsafe.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;



public class MD5Utils {

	/**MD5加密
	 *
	 */
	public static String encode(String password) {
		
		try {
			//获取MD5加密算法
			MessageDigest instance = MessageDigest.getInstance("MD5");
			StringBuffer sb = new StringBuffer();
			//对字符串加密，返回字节数组
			byte[] digest = instance.digest(password.getBytes());
			for (byte b : digest) {
				//获取字节的低八位有效值
				int i = b & 0xff;
				//将整数转化成16进制
				String hexString = Integer.toHexString(i);
				//如果只有一位，则补0
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			//没有找到"MD5"算法时，抛出异常
			e.printStackTrace();
		}
		return "";
	}

	public static String getFileMd5(String sourceDir) {

		try {
			//获取MD5加密算法
			MessageDigest instance = MessageDigest.getInstance("MD5");
			File file = new File(sourceDir);

			FileInputStream fileInputStream = new FileInputStream(file);
			byte []buff = new byte[24];
			int len ;
			while((len = fileInputStream.read(buff)) != -1){
				//累积文摘更新
				instance.update(buff,0,len);
			}
			StringBuffer sb = new StringBuffer();
			//对字符串加密，返回字节数组
			byte[] digest = instance.digest();
			for (byte b : digest) {
				//获取字节的低八位有效值
				int i = b & 0xff;
				//将整数转化成16进制
				String hexString = Integer.toHexString(i);
				//如果只有一位，则补0
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


}
