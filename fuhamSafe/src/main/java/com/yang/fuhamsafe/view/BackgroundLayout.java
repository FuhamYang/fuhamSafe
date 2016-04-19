package com.yang.fuhamsafe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yang.fuhamsafe.R;

import java.io.File;

public class BackgroundLayout extends RelativeLayout {

	private ImageView imageView;
	public static Bitmap bitmap; 
	
	public BackgroundLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setBackground();
	}

	public BackgroundLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setBackground();		
	}

	public BackgroundLayout(Context context) {
		super(context);
		setBackground();
	}
	
	//设置背景图片
	private void setBackground(){
		//将background放入BackgroundLayout中
		View.inflate(getContext(), R.layout.background, this);	
		//读取背景图片
		if(bitmap == null)
		{
			File imagefile = new File(getContext().getFilesDir(), "background.png");
			bitmap = BitmapFactory.decodeFile(imagefile.toString());		
		}
		imageView = (ImageView) findViewById(R.id.background);
		imageView.setImageBitmap(bitmap);

	}
	

}
