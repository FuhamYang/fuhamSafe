package com.yang.fuhamsafe.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.fragment.LockFragment;
import com.yang.fuhamsafe.fragment.UnlockFragment;

public class LockAppActivity extends FragmentActivity {

    private UnlockFragment unlockFragment;
    private LockFragment lockFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_app);

        //获取fragment管理器
        fragmentManager = getSupportFragmentManager();

        initView();

        lockFragment = new LockFragment();
        unlockFragment = new UnlockFragment();
        //用于存放fragment
        //FrameLayout frameLayout = (FrameLayout) findViewById(R.id.fragment_content);

        //开启事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content,unlockFragment).commit();

    }

    private void initView() {
        final TextView left = (TextView) findViewById(R.id.left);
        final TextView right = (TextView) findViewById(R.id.right);

        //点击不同的按钮，在同一个帧布局中，显示不同的fragrment
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                left.setBackgroundResource(R.drawable.tab_left_pressed);
                left.setTextColor(getResources().getColor(R.color.white));
                right.setBackgroundResource(R.drawable.tab_right_default);
                right.setTextColor(getResources().getColor(R.color.lowWhite));
                //1、存放fragment的布局;2、要放入的fragment
                fragmentTransaction.replace(R.id.fragment_content,unlockFragment).commit();

            }
        });
        //点击不同的按钮，在同一个帧布局中，显示不同的fragrment
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                right.setBackgroundResource(R.drawable.tab_right_pressed);
                right.setTextColor(getResources().getColor(R.color.white));
                left.setBackgroundResource(R.drawable.tab_left_default);
                left.setTextColor(getResources().getColor(R.color.lowWhite));
                fragmentTransaction.replace(R.id.fragment_content, lockFragment).commit();

            }
        });

    }
}
