package com.yang.fuhamsafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtils {
	//将读取网络流，返回字符串信息
	public static String getFromStream(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		int len = 0;
		byte[] b = new byte[1024];
		while((len = inputStream.read(b)) > -1){
			outputStream.write(b,0,len);
		}
		
		String result = outputStream.toString();
		outputStream.close();
		inputStream.close();
		return result;
	}
}
