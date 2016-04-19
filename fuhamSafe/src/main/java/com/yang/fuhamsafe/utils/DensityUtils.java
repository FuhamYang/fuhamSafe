package com.yang.fuhamsafe.utils;


import android.content.Context;
//px与dp之间的转换
public class DensityUtils {
	

	public static int dpToPx(Context context, float dp) {
		//获取屏幕密度
		float density = context.getResources().getDisplayMetrics().density;
		//计算出像素，并四舍五入
		int px = (int)(dp * density + 0.5);
		return px;
		
	}
	
	public static float pxToDp(Context context, int px) {
		
		//获取屏幕密度
		float density = context.getResources().getDisplayMetrics().density;
		//计算出dp
		float dp = px/density;
		return dp;
	}
}
