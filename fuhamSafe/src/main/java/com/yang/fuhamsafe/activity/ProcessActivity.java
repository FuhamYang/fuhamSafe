package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.bean.AppProcessInfo;
import com.yang.fuhamsafe.engine.AppProcessInfos;
import com.yang.fuhamsafe.utils.DensityUtils;
import com.yang.fuhamsafe.utils.SuUtil;
import com.yang.fuhamsafe.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessActivity extends Activity {


    private List<AppProcessInfo> list;
    private List<AppProcessInfo> usrApps;
    private List<AppProcessInfo> systemApps;
    private Map<String,TextView> parentTexts;
    private TextView userAppTitle;
    private TextView viewLine ;
    private TextView systemAppTitle;
    private ExpandableListView expandableListView;
    private ProgressBar progressBar;
    private Map<String, List<AppProcessInfo>> map;
    private List<String> parentList;
    private TextView processCountTextView;
    private TextView ramDetailTextView;
    private LinearLayout buttonLayout;
    private AppManageExpandableListAdapter appManageExpandableListAdapter;
    
    
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1){
                expandableListView.expandGroup(1);
                systemAppTitle.setText(parentList.get(1));
                systemAppTitle.bringToFront();
                systemAppTitle.setVisibility(View.VISIBLE);
            }else if (msg.what == 2){
                for (int i = 0 ; i < parentList.size();i++)
                    expandableListView.expandGroup(i);
            }else if (msg.what == 3){
                ramDetailTextView.setText("剩余/总RAM：" + Formatter.formatFileSize(ProcessActivity.this, AppProcessInfos.availMem) + "/" + AppProcessInfos.totalMem);
                processCountTextView.setText("运行进程：" + AppProcessInfos.count);

                appManageExpandableListAdapter.notifyDataSetChanged();
            }
            else if (msg.what == 0){
                processCountTextView.setText("运行进程：" + AppProcessInfos.count);

                ramDetailTextView.setText("剩余/总RAM：" + Formatter.formatFileSize(ProcessActivity.this, AppProcessInfos.availMem) + "/" + AppProcessInfos.totalMem);
                progressBar.setVisibility(View.GONE);
                appManageExpandableListAdapter = new AppManageExpandableListAdapter();
                expandableListView.setAdapter(appManageExpandableListAdapter);
                for (int i = 0 ; i < parentList.size();i++)
                    expandableListView.expandGroup(i);
                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                        return false;
                    }
                });

                SetExpandableListViewOnChildClickListener();

                SetExpandableListViewOnScrollListener();


            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);


        setInitView();
        initData();


    }


    private void SetExpandableListViewOnChildClickListener() {
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                AppProcessInfo appProcessInfo = map.get(parentList.get(groupPosition)).get(childPosition);
                if (appProcessInfo.getIsCheck())
                    appProcessInfo.setIsCheck(false);
                else
                    appProcessInfo.setIsCheck(true);
                appManageExpandableListAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    private void SetExpandableListViewOnScrollListener() {
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {


            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                //直接显示虚拟的用户应用父条目
                if (firstVisibleItem == 0) {
                    userAppTitle.setVisibility(View.VISIBLE);
                    userAppTitle.setText(parentList.get(0));
                    userAppTitle.bringToFront();
                    viewLine.bringToFront();
                    viewLine.setVisibility(View.VISIBLE);
                }
                //当第一条可见的条目即将接近，真实系统应用父条目时
                //此处注意,firstVisibleItem从0开始，firstVisibleItem == usrApps.size()即可见
                //条目为用户应用的最后一条时，开始计算虚拟用户应用父条目与 真实系统应用父条目的距离
                //当用户应用的子条目为0时，即usrApps.size()== 0，当第一次进入页面时，条件firstVisibleItem == usrApps.size()
                //成立，会进入方法使用textView.getTop()；但是由于是先调用OnScrollListener的onScroll方法，
                // 再调用BaseExpandableListAdapter的getGroupView，textView即真实系统应用父条目还未初始化，因此会出现空指针异常
                //因此还要再加一个条件：usrApps.size() != 0

                if (firstVisibleItem == usrApps.size() && usrApps.size() != 0) {
                    //获取到已加载的真实系统应用父条目
                    TextView textView = parentTexts.get(parentList.get(1));
                    //当真实系统应用父条目，碰到虚拟用户应用父条目时
                    if (textView.getTop() < userAppTitle.getHeight()) {
                        //显示出虚拟系统应用父条目，制造出系统应用父条目置顶的效果
                        systemAppTitle.setText(parentList.get(1));
                        systemAppTitle.bringToFront();
                        systemAppTitle.setVisibility(View.VISIBLE);
                    }
                    //当真实系统应用父条目，离开虚拟用户应用父条目时
                    if (textView.getTop() > userAppTitle.getHeight()) {
                        //隐藏虚拟用户应用父条目
                        systemAppTitle.setVisibility(View.GONE);
                    }
                }

            }
        });
    }



    private void initData(){

        new Thread(){
            @Override
            public void run() {
                //获取所有运行进程的信息
                list = AppProcessInfos.getAppProcessInfos(ProcessActivity.this);
                usrApps = new ArrayList<>();
                systemApps = new ArrayList<>();
                for(AppProcessInfo appProcessInfo:list){
                    if (appProcessInfo.isUserApp())
                        usrApps.add(appProcessInfo);
                    else
                        systemApps.add(appProcessInfo);
                }
                //初始化父条目
                parentList = new ArrayList<>();
                parentList.add("用户进程：" + usrApps.size());
                parentList.add("系统进程：" + systemApps.size());
                //父条目与子条目集合对应存储在map中
                map = new HashMap<>();
                map.put(parentList.get(0), usrApps);
                map.put(parentList.get(1), systemApps);
                //子线程取完数据，让主线程更新UI
                handler.sendEmptyMessage(0);
            }
        }.start();

    }
    

    //设置适配器
    class  AppManageExpandableListAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return parentList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {

            return map.get(parentList.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return parentList.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return map.get(parentList.get(groupPosition)).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            //设置父条目的视图
            TextView textView;
            if (convertView == null){
                textView = new TextView(ProcessActivity.this);
                int pad = DensityUtils.dpToPx(ProcessActivity.this,12);
                textView.setPadding(pad, pad, pad, pad);
                textView.setTextSize(20);
                textView.setBackgroundResource(R.color.contentBackground);
                textView.setTextColor(getResources().getColor(R.color.white));
                convertView = textView;
            }else{
                textView = (TextView) convertView;
            }

            textView.setText(parentList.get(groupPosition));
            parentTexts.put(parentList.get(groupPosition),textView);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //设置子条目的视图
            ViewHolder viewHolder;
            //当缓存不为空，且缓存对象是RelativeLayout类型
            if (convertView != null && convertView instanceof RelativeLayout){
                viewHolder = (ViewHolder) convertView.getTag();
            }else{
                viewHolder = new ViewHolder();
                convertView = View.inflate(ProcessActivity.this,R.layout.process_manage_item,null);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.tvProcessName = (TextView) convertView.findViewById(R.id.tv_process_name);
                viewHolder.tvProcessSize = (TextView)convertView.findViewById(R.id.tv_process_size);
                viewHolder.cbItem = (CheckBox) convertView.findViewById(R.id.cb_item);

                convertView.setTag(viewHolder);
            }
            //从map中获取数据信息
            AppProcessInfo appProcessInfo = map.get(parentList.get(groupPosition)).get(childPosition);
            viewHolder.ivIcon.setImageDrawable(appProcessInfo.getIcon());
            viewHolder.tvProcessName.setText(appProcessInfo.getProcessName());
            viewHolder.tvProcessSize.setText("占用内存：" + Formatter.formatFileSize(ProcessActivity.this, appProcessInfo.getSize()*1024));
            viewHolder.cbItem.setChecked(appProcessInfo.getIsCheck());

            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class ViewHolder{
        ImageView ivIcon;
        TextView tvProcessName;
        CheckBox cbItem;
        TextView tvProcessSize;

    }

    private void setInitView(){


        processCountTextView = (TextView) findViewById(R.id.processCountTextView);
        ramDetailTextView = (TextView) findViewById(R.id.ramDetailTextView);
        buttonLayout = (LinearLayout) findViewById(R.id.button_layout);
        viewLine = (TextView) findViewById(R.id.viewLine);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_app_manage);
        parentTexts = new HashMap<>();


        expandableListView = (ExpandableListView) findViewById(R.id.elv_app_manage);
        //为了让父条目置顶，自己设置了两个置顶的TextView,模拟父条目置顶，实际上父条目并没有置顶，
        // 而是与子条目一样滑动出屏幕
        userAppTitle = (TextView) findViewById(R.id.app_type_title);
        systemAppTitle = (TextView) findViewById(R.id.app_type_title2);

        systemAppTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当系统应用父条目展开时
                if (expandableListView.isGroupExpanded(1)) {
                    //收起系统应用的子条目
                    expandableListView.collapseGroup(1);
                    //此时如果用户应用父条目已经展开，真正的系统应用条目会显示在最底下，所以要将自己模拟出来的系统应用父条目隐藏
                    systemAppTitle.setVisibility(View.GONE);
                    //如果用户应用父条目没有展开，则必须要将模拟的系统应用条目显示出来，否则真正的系统应用条目并不会置顶
                    if (!expandableListView.isGroupExpanded(0))
                        systemAppTitle.setVisibility(View.VISIBLE);
                } else {
                    expandableListView.expandGroup(1);
                }
            }
        });

        userAppTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果用户应用父条目已经展开
                if (expandableListView.isGroupExpanded(0)) {
                    //收起用户应用子条目
                    expandableListView.collapseGroup(0);
                    //此时真实的系统应用父条目会置顶，由于其不会固定置顶，所以必须将模拟的系统应用子条目显示出来
                    systemAppTitle.setVisibility(View.VISIBLE);
                    //如果系统应用父条目此时已展开，那么当收起用户应用子条目时，
                    // 系统应用的子条目会全部上移，直接超过模拟系统应用子条目的位置，导致条目错乱
                    if (expandableListView.isGroupExpanded(1)) {
                        //所以，将系统应用的子条目也一起收起
                        expandableListView.collapseGroup(1);
                        //通过发送消息，让子条目的顺序重置，来再次展开系统应用的父条目（如果立刻展开，效果是一样的，即依然会导师条目错乱），
                        // 这样就不会造成系统应用的子条目，直接超过模拟系统应用子条目的位置
                        handler.sendEmptyMessage(1);
                    }
                } else {
                    //如果系统应用父条目展开，用户应用父条目收起
                    if (expandableListView.isGroupExpanded(1)) {
                        //让模拟系统应用父条目消失
                        systemAppTitle.setVisibility(View.GONE);
                        //收起系统应用父条目，（如果立刻展开用户应用父条目，
                        // 则由于系统应用子条目可能已滑动了一部分，假设是5个子条目，当展开时用户父条目时，
                        // 部分用户应用的子条目不会显示出来，即前5个子条目在屏幕上面之外，需要向下滑动才能显示）
                        expandableListView.collapseGroup(1);
                        //通过发送消息，让子条目的顺序重置，再展开所有父条目
                        handler.sendEmptyMessage(2);
                    } else {
                        //如果系统应用父条目没有展开，则直接展开用户应用子条目即可
                        expandableListView.expandGroup(0);
                        systemAppTitle.setVisibility(View.GONE);
                    }
                }
            }
        });
    }
    //全选所有进程
    public void selectAll(View view){
        //将所有进程打勾
        for (AppProcessInfo process:
                list) {
            process.setIsCheck(true);
        }
        //更新ui
        appManageExpandableListAdapter.notifyDataSetChanged();
    }
    //反选所有进程
    public void reverseSelect(View view){
        for (AppProcessInfo process:
                list) {
             if (process.getIsCheck())
                 process.setIsCheck(false);
            else
                 process.setIsCheck(true);
        }
        appManageExpandableListAdapter.notifyDataSetChanged();
    }
    //杀死进程
    public void clean(View view){

        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<AppProcessInfo> listRemove = new ArrayList<>();
        for (AppProcessInfo process:
                list) {
            if (process.getIsCheck()){
                //只能清除后台运行的进程
                //activityManager.killBackgroundProcesses(process.getPackageName());
                //直接调用cmd的命令，使用root用户来杀进程
                SuUtil.kill(process.getPackageName());
                /// /由于集合在迭代的时候不能立刻删除其中的元素
                //因此把集合要删除的元素保存在另一个集合中，迭代结束后再进行删除
                listRemove.add(process);
            }
        }

        long releaseRAM = 0;

        for (AppProcessInfo process:listRemove
             ) {
            //计算清楚掉的RAM
            releaseRAM +=  process.getSize();
            list.remove(process);
            if (process.isUserApp())
                usrApps.remove(process);
            else
                systemApps.remove(process);
        }
        AppProcessInfos.count -= listRemove.size();
        AppProcessInfos.availMem += releaseRAM*1024;
        ToastUtils.showToast(ProcessActivity.this,"清除"+listRemove.size()+"个进程"+",释放"+Formatter.formatFileSize(ProcessActivity.this, releaseRAM*1024)+"内存");

        //更新UI界面
        handler.sendEmptyMessage(3);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
