package com.yang.fuhamsafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
//设置自定义组合控件
public class SettingItemLayout extends RelativeLayout {

	//
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.yang.fuhamsafe";
	private TextView title;
	private TextView info;
	private CheckBox checkBox;
	private String titleString;
	private String infoOn;
	private String infoOff;
	
	public boolean isCheckBox() {
		return checkBox.isChecked();
	}

	public void setCheckBox(boolean status) {
		//根据状态设置文本
		if (status) {
			info.setText(infoOn);
		} else {
			info.setText(infoOff);
		}
		checkBox.setChecked(status);
	}

	public SettingItemLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setItem();
		// TODO Auto-generated constructor stub
	}
	//当有自定义属性时会调用该构造方法
	public SettingItemLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		/*通过自定义命名空间获取自定义属性值（该值是在使用该自定义控件时设置的值：MyAttrs:info_off="SIM卡未绑定"
		MyAttrs:info_on="SIM卡已绑定"
		MyAttrs:title="点击绑定SIM卡"）*/
		titleString = attrs.getAttributeValue(NAMESPACE, "title");
		infoOn = attrs.getAttributeValue(NAMESPACE, "info_on");
		infoOff = attrs.getAttributeValue(NAMESPACE, "info_off");		
		setItem();
	}

	public SettingItemLayout(Context context) {
		super(context);
		setItem();
		// TODO Auto-generated constructor stub
	}
	
	private void setItem(){
		//将子控件与父控件绑定
		View.inflate(getContext(), R.layout.view_setting_item, this);
		//获取子控件的view的值
		title = (TextView) findViewById(R.id.setting_item_tiltle);
		info = (TextView) findViewById(R.id.setting_item_info);
		checkBox = (CheckBox) findViewById(R.id.setting_item_checkBox);
	
		title.setText(titleString);
	}

}
