package com.yang.fuhamsafe.activity;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.bean.AppInfo;
import com.yang.fuhamsafe.dao.AntivirusDao;
import com.yang.fuhamsafe.engine.AppInfos;
import com.yang.fuhamsafe.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;

public class AntivirusMainActivity extends Activity {
    private LinearLayout contentView;
    private ProgressBar progressBar;
    private TextView tvDec;
    private ScrollView scrollView;
    private ObjectAnimator oa;
    private int progress;
    private int completedThread = 0;
    private int threadCount;
    private ImageView imageView;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0) {
                imageView.setVisibility(View.INVISIBLE);
                tvDec.setText("扫描完成");
                oa.cancel();
            }else if (msg.what == 2){
                tvDec.setText("正在扫描");
                AppInfo info = (AppInfo)msg.obj;
                View view = View.inflate(AntivirusMainActivity.this,R.layout.antivirus_item,null);
                TextView tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
                TextView tvDesc = (TextView) view.findViewById(R.id.tv_desc);
                ImageView icon = (ImageView) view.findViewById(R.id.icon);
                ImageView check = (ImageView) view.findViewById(R.id.check_image);

                if (!info.getDesc().equals("扫描安全"))
                    check.setBackgroundResource(R.drawable.danger);
                icon.setImageDrawable(info.getIcon());
                tvAppName.setText(info.getAppName());
                tvDesc.setText(info.getDesc());

                contentView.addView(view);

                //自动滚动
                scrollView.post(new Runnable() {

                    @Override
                    public void run() {
                        //一直往下面进行滚动
                        scrollView.fullScroll(scrollView.FOCUS_DOWN);
                    }
                });
            }else if(msg.what == 1){
                completedThread++;
                if (completedThread == threadCount)
                    sendEmptyMessage(0);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus_main);

        initView();
        initData();

    }



    private void initData() {
        new Thread(){
            @Override
            public void run() {
                List<AppInfo> appInfos = AppInfos.getAppInfos(AntivirusMainActivity.this);
                List<AppInfo> userApps = new ArrayList<>();
                for(AppInfo appInfo:appInfos){
                    if (appInfo.isUserApp())
                        userApps.add(appInfo);
                }

                progressBar.setMax(userApps.size());
                progress = 0;
                threadCount = 7;
                int length =userApps.size();
                //计算出每个线程应该查询的应用数
                int size = length/ threadCount;
                //计算每个线程查询的应用索引的开始与结束位置
                for(int i = 0 ; i < threadCount; i++){
                    int startIndex = i * size;
                    int endIndex = ( i + 1 ) * size - 1;
                    if( i == threadCount -1 ){
                        endIndex = length-1;
                    }
                    new MyThread(userApps,startIndex, endIndex, i).start();
                }

            }
        }.start();
    }

    class MyThread extends Thread{
        private List<AppInfo> appInfos ;
        private int startIndex;
        private int endIndex;
        private int threadId;

        public MyThread(List<AppInfo> appInfos, int startIndex, int endIndex, int threadId) {
            this.appInfos = appInfos;
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.threadId = threadId;
        }

        @Override
        public void run() {
            for (int i = startIndex ; i <= endIndex ;i++ ) {

                //获取文件的检查结果
                String md5 = MD5Utils.getFileMd5(appInfos.get(i).getSourceDir());
                //i包含了从0到最大的用户应用数
                appInfos.get(i).setDesc(AntivirusDao.check(md5));
                Message msg = Message.obtain();
                msg.obj = appInfos.get(i);
                msg.what = 2;
                //不能是progress++，不然就是先设置进度条长度之后再++
                progressBar.setProgress(++progress);
                //发送消息，在屏幕弹出检查应用的消息
                handler.sendMessage(msg);
            }
            //发送消息，提示已完成该线程的扫描任务
            handler.sendEmptyMessage(1);
        }
    }

    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scroll);
        contentView = (LinearLayout) findViewById(R.id.content);
        tvDec = (TextView) findViewById(R.id.tv_dec);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        imageView = (ImageView) findViewById(R.id.imageScanning1);

        //最后一个参数设置旋转角度的变化
        oa = ObjectAnimator.ofFloat(
                imageView, "rotation", 0, 360);
        oa.setDuration(2000);
        oa.setRepeatCount(-1);
        oa.start();
    }
}
