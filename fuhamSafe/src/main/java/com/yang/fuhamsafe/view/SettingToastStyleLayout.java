package com.yang.fuhamsafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
//设置自定义组合控件
public class SettingToastStyleLayout extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.yang.fuhamsafe";
	private TextView title;
	private TextView info;
	private String titleString;

	public SettingToastStyleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setItem();
		// TODO Auto-generated constructor stub
	}

	public SettingToastStyleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//获取自定义属性值
		titleString = attrs.getAttributeValue(NAMESPACE, "title");		
		setItem();
	}

	public SettingToastStyleLayout(Context context) {
		super(context);
		setItem();
		// TODO Auto-generated constructor stub
	}
	
	public void setInfo(String text) {
		info.setText(text);
	}
	
	private void setItem(){
		//将子控件与父控件绑定
		View.inflate(getContext(), R.layout.view_address_item, this);
		//获取子控件的view的值
		title = (TextView) findViewById(R.id.setting_item_tiltle);
		info = (TextView) findViewById(R.id.setting_item_info);
		title.setText(titleString);
	}

}
