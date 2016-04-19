package com.yang.fuhamsafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RelativeLayout;

import com.yang.fuhamsafe.R;

/**
 * Created by fuhamyang on 2016/3/22.
 */
public class WindowLayout extends RelativeLayout {
    private WindowManager smokeWindowManager;
    private View smokeView;

    public WindowLayout(Context context) {
        super(context);
    }

    public WindowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WindowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void showSmoke(Context context) {
        // 获取窗口管理器
        smokeWindowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);


        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.windowAnimations = R.style.my;
        // 将重心位置设置为左上方，即（0，0）从左上方开始，而不是默认的中心位置
        layoutParams.gravity = Gravity.LEFT + Gravity.TOP;

        // 设置已设置的位置
        layoutParams.x = 0;
        layoutParams.y = 0;

        layoutParams.format = PixelFormat.TRANSLUCENT;
        // 设置窗口不能获取焦点，
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // 设置窗口的权限,需要在配置清单文件添加权限SYSTEM_ALERT_WINDOW
        layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;

        layoutParams.setTitle("Toast");
        layoutParams.windowAnimations = R.anim.window_anim;
        smokeView = View.inflate(context, R.layout.activity_smoke, null);
        // 将view添加到屏幕上
        smokeWindowManager.addView(smokeView, layoutParams);

        AlphaAnimation aa = new AlphaAnimation(0.3f, 1);
        aa.setDuration(5000);
        aa.setRepeatCount(5);
        //设置循环模式，现在是方向循环
        aa.setRepeatMode(Animation.REVERSE);
        //启动
        smokeView.startAnimation(aa);


    }
}
