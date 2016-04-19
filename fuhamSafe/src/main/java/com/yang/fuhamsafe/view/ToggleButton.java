package com.yang.fuhamsafe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by fuhamyang on 2016/3/23.
 */
public class ToggleButton extends View {
    private Bitmap swichBackgroundOpen;
    private Bitmap swichBackgroundClose;
    private Bitmap slideBackground;
    private int currentX;

    public boolean isOpen() {
        return isOpen;
    }

    public void setIsOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }

    private boolean isOpen;
    private boolean isFirst = true;
    private int BitmapZise = 2 ;
    private boolean isUp = false;

    public ToggleButton(Context context) {
        super(context);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    //设置滑动开关的背景图片
    public void setSwitchBackgroundResource(int switchBackgroundOpenResource,
                                            int switchBackgroundCloseResource){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = BitmapZise;
        swichBackgroundOpen = BitmapFactory.decodeResource(getResources(),switchBackgroundOpenResource,options);
        swichBackgroundClose = BitmapFactory.decodeResource(getResources(),switchBackgroundCloseResource,options);

    }

    //设置滑动块的背景图片
    public void setSlideBackgroundResource(int slideBackgroundResource){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = BitmapZise;
        slideBackground = BitmapFactory.decodeResource(getResources(),slideBackgroundResource,options);

    }
    //设置开关的状态
    public void setToggleState(boolean state){
        isOpen = state;
    }

    //重写onMeasure方法，传入滑动开关背景图片的宽高,设置控件的宽高；
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(swichBackgroundOpen.getWidth(), swichBackgroundOpen.getHeight());

    }

    //绘制出在屏幕中的样子
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //第一次进入时，初始化位置；或则，如果是用户抬起时，滑动超过一半改变滑块位置
        if (isFirst || isUp){
            //在在该view的画板上画滑动块背景图片
            if (isOpen){
                //在该view的画板上画滑动开关背景图片，第二与第三个参数是距离该View的左边和上边的距离（而不是整个屏幕）
                canvas.drawBitmap(swichBackgroundOpen,0,0,null);
                //如果是开的，将它显示到最最左边
                canvas.drawBitmap(slideBackground,0,0,null);
            }else{
                //在该view的画板上画滑动开关背景图片，第二与第三个参数是距离该View的左边和上边的距离（而不是整个屏幕）
                canvas.drawBitmap(swichBackgroundClose,0,0,null);
                //如果是关的，将它显示到最右边
                canvas.drawBitmap(slideBackground, swichBackgroundOpen.getWidth()-slideBackground.getWidth(),0,null);
            }
            isFirst = false;
            isUp = false;
        }else{
            //滑块左边距离View左边的长度
            int left = currentX - slideBackground.getWidth()/2;
            // 即currentX<slideBackground.getWidth()/2时，此时滑块已到最左边，不允许再滑动
            //如果滑块左边小于0，不允许滑动，设置左边停留在0处
            if (left < 0 ) left = 0;
            //如果滑块右边距离越界，不允许滑动，设置为固定值
            if (left + slideBackground.getWidth()>swichBackgroundClose.getWidth())
                left = swichBackgroundClose.getWidth()-slideBackground.getWidth();
            if (currentX >swichBackgroundClose.getWidth()/2){
                //在该view的画板上画滑动开关背景图片，第二与第三个参数是距离该View的左边和上边的距离（而不是整个屏幕）
                canvas.drawBitmap(swichBackgroundClose,0,0,null);
                //如果是关的，将它显示到最右边
                canvas.drawBitmap(slideBackground, left,0,null);

            }else {
                //在该view的画板上画滑动开关背景图片，第二与第三个参数是距离该View的左边和上边的距离（而不是整个屏幕）
                canvas.drawBitmap(swichBackgroundOpen,0,0,null);
                //如果是关的，将它显示到最右边
                canvas.drawBitmap(slideBackground, left,0,null);
            }

        }


    }

    //设置滑动块的滑动点击事件
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //每次动作都获取当前x坐标
        //在外面写的效果，相当于在每个CASE中都执行一遍
        currentX = (int)event.getX();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MASK:
                break;
            case MotionEvent.ACTION_UP:
                //用户抬起手指后，如果滑动超过一半，则改变滑动开关的状态
                isUp = true;

                if (currentX > swichBackgroundClose.getWidth()/2){
                    //当滑动 超过一半，并且状态发生改变的时候才调用该监听器的方法
                    if (isOpen != false){
                        isOpen = false;
                        //每次用户触摸滑动块时，调用onToggleStateChange方法；
                        // 其他类只要实现了这个接口侦听类，里面的代码就可以被调用
                        if (listener != null){
                            listener.onToggleStateChange(isOpen);
                        }

                    }

                }else{
                    if (isOpen != true){
                        isOpen = true;
                        //每次用户触摸滑动块时，调用onToggleStateChange方法；
                        // 其他类只要实现了这个接口侦听类，里面的代码就可以被调用
                        if (listener != null){
                            listener.onToggleStateChange(isOpen);
                        }
                    }

                }

                break;
        }
        //该方法会调用onDaw方法重新绘制控件（更新控件），不能直接调用onDaw方法，它是由系统调用的
        invalidate();
        return true;
    }

    private OnToggleStateChangeListener listener;

    //对接口对象进行赋值
    public  void setOnToggleStateChangeListener(OnToggleStateChangeListener listener){
        this.listener = listener;
    }
    //定义一个接口监听事件，当其他类实现这个监听事件时，可以获得开关的状态
    public interface OnToggleStateChangeListener{
        void onToggleStateChange(boolean isOpen);
    }



}
