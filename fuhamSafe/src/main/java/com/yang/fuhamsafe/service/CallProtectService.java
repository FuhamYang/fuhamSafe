package com.yang.fuhamsafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.yang.fuhamsafe.bean.BlackNumber;
import com.yang.fuhamsafe.dao.BlackNumberDao;

import java.lang.reflect.Method;

public class CallProtectService extends Service {

    private TelephonyManager telephonyManager;
    private SharedPreferences sharedPreferences;
    private BlackSmsReceiver blackSmsReceiver;
    private MyListener myListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);

        blackSmsReceiver = new BlackSmsReceiver();

        // 设置要监听广播的action
        IntentFilter intentFilter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");

        intentFilter.setPriority(Integer.MAX_VALUE);

        // 注册广播
        registerReceiver(blackSmsReceiver, intentFilter);

        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        myListener = new MyListener();
        // 监听电话的状态
        telephonyManager.listen(myListener,
                PhoneStateListener.LISTEN_CALL_STATE);



    }

    class BlackSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //获取短信数组（短信过长时，会将短信分成几段进行发送，所以是一个数组）
            Object[] objects = (Object[]) intent.getExtras().get("pdus");

            for (Object object : objects) {
                //把对象转成字节数组，再转成短信
                SmsMessage message = SmsMessage.createFromPdu((byte[]) object);
                //获取发送的号码
                String sendNumber = message.getOriginatingAddress();

                BlackNumber blackNumber = new BlackNumberDao(CallProtectService.this).
                        selectByBlackNumebr(sendNumber);
               if (blackNumber != null){
                    String type = blackNumber.getType();
                    if (type.equals("1") || type.equals("3")) {
                        System.out.println(type+"#########################进来了#####################");
                        abortBroadcast();//中断短信的广播
                    }
                    System.out.println(type+"#########################没进来#####################");
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        // 注销短信广播
        unregisterReceiver(blackSmsReceiver);

        // 取消电话管理器的监听
        telephonyManager.listen(myListener, PhoneStateListener.LISTEN_NONE);
    }
    //挂断电话
    //（由于挂断电话的方法被谷歌屏蔽了，但是在包中依然存在该方法，
    // 为了得到该方法，必须通过反射获取包中的方法来使用）
    private void endCall(){

        try {
            //通过类加载器加载ServiceManager类
            Class<?> clazz = getClassLoader().loadClass("android.os.ServiceManager");
            //通过反射获得当前的方法
            Method method = clazz.getDeclaredMethod("getService",String.class);
            IBinder iBinder = (IBinder)method.invoke(null,TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);

            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    BlackNumber blackNumber = new BlackNumberDao(CallProtectService.this).
                            selectByBlackNumebr(incomingNumber);
                    if (blackNumber != null){
                        String type = blackNumber.getType();
                        if (type.equals("2") || type.equals("3")) {
                            endCall();

                            Uri uri = Uri.parse("content://call_log/calls");
                            //注册内容观察者
                            getContentResolver().registerContentObserver(uri,true,new MyContentObserver(new Handler(),incomingNumber));

                        }

                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:

                    break;
                default:

                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
    }

    private class MyContentObserver extends ContentObserver{
        private String number;
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler,String number) {
            super(handler);
            this.number = number;
        }
        //当数据改变时调用此方法
        @Override
        public void onChange(boolean selfChange) {

            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(number);
            super.onChange(selfChange);
        }
    }
    //在通话记录中删除该号码
    private void deleteCallLog(String number) {
        Uri uri = Uri.parse("content://call_log/calls");
        getContentResolver().delete(uri,"number = ?",new String[]{number});
    }

}
