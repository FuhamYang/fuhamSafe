package com.yang.fuhamsafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {
	private static Toast mToast;

	// 如果Toast不为空，则只改变信息
	public static void showToast(Context context, String text) {
		if (mToast == null) {
			mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	// 取消Toast
	public static void cancelToast() {
		if (mToast != null) {
			mToast.cancel();
		}
	}

}
