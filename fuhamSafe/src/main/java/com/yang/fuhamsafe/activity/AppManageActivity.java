package com.yang.fuhamsafe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.bean.AppInfo;
import com.yang.fuhamsafe.engine.AppInfos;
import com.yang.fuhamsafe.utils.DensityUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppManageActivity extends Activity implements View.OnClickListener {

   // private ListView listView;
    private List<AppInfo> list;
    private List<AppInfo> usrApps;
    private List<AppInfo> systemApps;
    private boolean flag = true;
    private Map<String,TextView> parentTexts;
    private TextView userAppTitle;
    private TextView viewLine ;
    private TextView systemAppTitle;
    private ExpandableListView expandableListView;
    private ProgressBar progressBar;
    private Map<String, List<AppInfo>> map;
    private List<String> parentList;
    private PopupWindow popupWindow;
    private AppInfo clickItemAppInfo;
    private List<AppInfo> parentClickItemAppInfo;
    private TextView rom;
    private TextView sd;
    private AppManageExpandableListAdapter appManageExpandableListAdapter;
    private UninstallReceiver receiver;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1){
                expandableListView.expandGroup(1);
                systemAppTitle.setText(parentList.get(1)+systemApps.size());
                systemAppTitle.bringToFront();
                systemAppTitle.setVisibility(View.VISIBLE);
            }else if (msg.what == 3){
                //卸载应用后从新更新，界面

                //更新存储显示
                initSpaceText();
                //更新ExpandableList
                appManageExpandableListAdapter.notifyDataSetChanged();
                //更新父条目的应用数显示
                userAppTitle.setText(parentList.get(0)+usrApps.size());
                systemAppTitle.setText(parentList.get(1) + systemApps.size());

                //注销广播
                unregisterReceiver(receiver);
            }
            else if (msg.what == 2){
                //将全部父条目展开
                for (int i = 0 ; i < parentList.size();i++)
                    expandableListView.expandGroup(i);
            }
            else if (msg.what == 0){

                //获取数据后，构建界面

                //移除进度条
                progressBar.setVisibility(View.GONE);
                //设置ExpandableList的适配器
                appManageExpandableListAdapter = new AppManageExpandableListAdapter();
                expandableListView.setAdapter(appManageExpandableListAdapter);
                //一开始就设置所有父条目栏的状态为展开
                for (int i = 0 ; i < parentList.size();i++)
                    expandableListView.expandGroup(i);

                //当点击父栏时，将窗口取消
                expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                    @Override
                    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                        dismissPopupWindow();
                        return false;
                    }
                });

                //设置ExpandableListView的子栏的点击事件，弹出popupwindow
                SetExpandableListViewOnChildClickListener();

                //设置ExpandableListView的滑动侦听事件
                SetExpandableListViewOnScrollListener();

            }


            /*listView.setAdapter(new AppManageAdapter());
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem != 0 && firstVisibleItem <= usrApps.size()) {
                        appTypeTitle.setText("下载应用:" + usrApps.size());
                        appTypeTitle.setVisibility(View.VISIBLE);
                    } else if (firstVisibleItem != usrApps.size() + 1 && firstVisibleItem > usrApps.size() + 1) {
                        appTypeTitle.setText("系统应用:" + systemApps.size());
                        appTypeTitle.setVisibility(View.VISIBLE);
                    }
                }
            });*/
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage);

        //初始化页面
        setInitView();

        //listView = (ListView) findViewById(R.id.lv_app_manage);*/

        //初始化数据
        initData();


    }

    //设置ExpandableListView的滑动侦听事件，处理虚拟条目
    private void SetExpandableListViewOnScrollListener() {
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                //直接显示虚拟的用户应用父条目
                if (firstVisibleItem == 0) {
                    userAppTitle.setVisibility(View.VISIBLE);
                    userAppTitle.setText(parentList.get(0) + usrApps.size());
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
                System.out.println("进来了！！！！###");
                if (firstVisibleItem == usrApps.size() && usrApps.size() != 0) {
                    //获取到已加载的真实系统应用父条目
                    TextView textView = parentTexts.get(parentList.get(1));
                    //当真实系统应用父条目，碰到虚拟用户应用父条目时
                    if (textView.getTop() < userAppTitle.getHeight()) {
                        //显示出虚拟系统应用父条目，制造出系统应用父条目置顶的效果

                        systemAppTitle.setText(parentList.get(1) + systemApps.size());
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

    //设置ExpandableListView的子栏的点击事件,弹出popupwindow
    private void SetExpandableListViewOnChildClickListener() {
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                dismissPopupWindow();

                //保存点击条目的信息，用于启动，卸载，分享，以及查看详情的操作
                //保存被点击的条目所在的父条目
                parentClickItemAppInfo = map.get(parentList.get(groupPosition));
                //保存被点击的子条目
                clickItemAppInfo = map.get(parentList.get(groupPosition)).get(childPosition);

                //设置弹出popupwindow

                //实例化要放置在popupwindow上的layout
                View popupView = View.inflate(AppManageActivity.this, R.layout.popup_item, null);
                LinearLayout runLinearLayout = (LinearLayout) popupView.findViewById(R.id.ll_run);
                LinearLayout removeLinearLayout = (LinearLayout) popupView.findViewById(R.id.ll_remove);
                LinearLayout shareLinearLayout = (LinearLayout) popupView.findViewById(R.id.ll_share);
                LinearLayout detailLinearLayout = (LinearLayout) popupView.findViewById(R.id.ll_detail);

                //为每个图标设置点击事件
                detailLinearLayout.setOnClickListener(AppManageActivity.this);
                runLinearLayout.setOnClickListener(AppManageActivity.this);
                shareLinearLayout.setOnClickListener(AppManageActivity.this);
                removeLinearLayout.setOnClickListener(AppManageActivity.this);

                //获取popupWindow
                popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //该数组用于存放返回的x,y
                int[] location = new int[2];
                //获取v展示到窗体的位置，存放在数组，
                // 数组长度必须是大于2，即至少返回x,y
                v.getLocationInWindow(location);
                //设置透明背景（popupwindow必须设置背景才能显示动画）
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                //1、要显示在那个view中；2、设置起始位置在左边加上边，即左上角；
                // 3、设置距离左边的距离；4、设置距离上边的距离
                popupWindow.showAtLocation(parent, Gravity.LEFT + Gravity.TOP,
                        DensityUtils.dpToPx(AppManageActivity.this, 80), location[1]);

                //设置动画
                //从0倍放大到到1倍；最后两个参数设置缩放的中心位置
                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                        popupView.getWidth() / 2,
                        popupView.getHeight() / 2);
                scaleAnimation.setDuration(500);
                popupView.startAnimation(scaleAnimation);
                return true;
            }
        });

    }



    //初始化数据
    private void initData(){

        new Thread(){
            @Override
            public void run() {
                //获取所有应用安装包信息
                list = AppInfos.getAppInfos(AppManageActivity.this);
                usrApps = new ArrayList<>();
                systemApps = new ArrayList<>();
                for(AppInfo appInfo:list){
                    if (appInfo.isUserApp())
                        usrApps.add(appInfo);
                    else
                        systemApps.add(appInfo);
                }
                //初始化父条目
                parentList = new ArrayList<>();
                parentList.add("用户应用：" );
                parentList.add("系统应用：" );

                //父条目与子条目集合对应存储在map中
                map = new HashMap<>();
                map.put(parentList.get(0), usrApps);
                map.put(parentList.get(1), systemApps);

                //子线程取完数据，让主线程更新UI
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    //关闭popupWindow
    private void dismissPopupWindow(){
        if (popupWindow != null && popupWindow.isShowing())
            popupWindow.dismiss();
            popupWindow = null;
    }

    //设置弹出窗口的点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_run:
                //启动应用
                Intent runIntent = getPackageManager().getLaunchIntentForPackage(clickItemAppInfo.getPackageName());
                startActivity(runIntent);
                dismissPopupWindow();
                break;
            case  R.id.ll_remove:
                //注册广播监听是否卸载，以便进行UI的更新
                receiver = new UninstallReceiver();
                //调用系统的卸载程序
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
                intentFilter.addDataScheme("package");
                //注册广播
                registerReceiver(receiver, intentFilter);
                //卸载应用
                Intent removeIntent = new Intent();
                removeIntent.setAction(Intent.ACTION_DELETE);
                removeIntent.setData(Uri.parse("package:" + clickItemAppInfo.getPackageName()));

                startActivityForResult(removeIntent, 123);
                dismissPopupWindow();
                break;
            case R.id.ll_share:
                //分享应用

                Intent shareIntent = new Intent();
                //跳出窗口，该窗口会显示所有可以分享的应用
                shareIntent.setAction("android.intent.action.SEND");
                //设置类型
                shareIntent.setType("text/plain");
                //设置主题
                shareIntent.putExtra("android.intent.extra.SUBJECT", "分享");
                //设置分享内容
                shareIntent.putExtra("android.intent.extra.TEXT",
                        clickItemAppInfo.getAppName()
                                + "这个应用好到爆！" +
                                //该地址是谷歌市场，只要在地址后加上包的名字就会跳转到该应用的下载页面
                                "下载地址:https://play.google.com/store/apps/details?id=" + clickItemAppInfo.getPackageName());
                //设置选择器，即弹出的窗口中标题为“分享”，不设置则是默认的“选择要使用的应用”
                startActivity(Intent.createChooser(shareIntent,"分享"));
                dismissPopupWindow();
                //startActivity(shareIntent);
                break;

            case R.id.ll_detail:
                //查看应用的详细信息
                Intent detailIntent = new Intent();
                //启动系统自带的查看应用详情的程序
                detailIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                detailIntent.setData(Uri.parse("package:" + clickItemAppInfo.getPackageName()));
                startActivity(detailIntent);
                dismissPopupWindow();
                break;
            default:
                break;
        }
    }

    //卸载广播
    class UninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //收到卸载的广播后将应用移除list列表
            parentClickItemAppInfo.remove(clickItemAppInfo);
            //更新UI
            handler.sendEmptyMessage(3);
        }
    }



    //设置适配器
    class  AppManageExpandableListAdapter extends BaseExpandableListAdapter{

        @Override
        public int getGroupCount() {
            //获取父条目的个数
            return parentList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            //获取对应父条目下，子条目的个数
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
            //当缓存不为空，且缓存对象是TextView类型(没有必要因为不是listview)
            if (convertView != null && convertView instanceof TextView){
                textView = (TextView) convertView;
            }else{

                //设置新的textView显示在父条目上
                textView = new TextView(AppManageActivity.this);
                int pad = DensityUtils.dpToPx(AppManageActivity.this,12);
                textView.setPadding(pad,pad,pad,pad);
                textView.setTextSize(20);
                textView.setBackgroundColor(getResources().getColor(R.color.blue));
                textView.setTextColor(getResources().getColor(R.color.white));
                convertView = textView;
            }
            //设置textView中的text
            if(groupPosition == 0)
            {
                textView.setText(parentList.get(groupPosition)+usrApps.size());
            }else{
                textView.setText(parentList.get(groupPosition)+systemApps.size());
            }
            //将父条目存储
            parentTexts.put(parentList.get(groupPosition),textView);
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //设置子条目的视图
            ViewHolder viewHolder;

            //当缓存不为空，且缓存对象是RelativeLayout类型(没有必要，因为不是listview)
            if (convertView != null && convertView instanceof RelativeLayout){
                viewHolder = (ViewHolder) convertView.getTag();
            }else{
                viewHolder = new ViewHolder();
                convertView = View.inflate(AppManageActivity.this,R.layout.app_manage_item,null);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                viewHolder.tvSpaceType = (TextView)convertView.findViewById(R.id.tv_space_type);
                viewHolder.tvAppSize = (TextView) convertView.findViewById(R.id.tv_app_size);

                convertView.setTag(viewHolder);
            }
            //从map中获取数据信息
            AppInfo appInfo = map.get(parentList.get(groupPosition)).get(childPosition);
            viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
            viewHolder.tvAppName.setText(appInfo.getAppName());
            viewHolder.tvAppSize.setText(Formatter.formatFileSize(AppManageActivity.this,appInfo.getAppSize()));
            if (appInfo.isInExternalStorage()) {
                viewHolder.tvSpaceType.setText("sd卡内存");
            }else{
                viewHolder.tvSpaceType.setText("手机内存");
            }
            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    //存储缓存数据
    private class ViewHolder{
        ImageView ivIcon;
        TextView tvAppName;
        TextView tvSpaceType;
        TextView tvAppSize;

    }

    private void setInitView(){
        //初始化内存显示
        initSpaceText();

        //获取对象
        viewLine = (TextView) findViewById(R.id.viewLine);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_app_manage);
        expandableListView = (ExpandableListView) findViewById(R.id.elv_app_manage);
        //用于存储父条目，在滑动时，将其取出，计算其距离顶部的距离，到达顶部则显示虚拟的父条目
        parentTexts = new HashMap<>();

        //为了让父条目置顶，自己设置了两个置顶的TextView,模拟父条目置顶，实际上父条目并没有置顶，
        // 而是与子条目一样滑动出屏幕
        userAppTitle = (TextView) findViewById(R.id.app_type_title);
        systemAppTitle = (TextView) findViewById(R.id.app_type_title2);

        //设置系统应用标题点击事件
        SetSystemAppTitleOnClickListener();
        //设置用户应用标题点击事件
        SetUserAppTitleOnClickListener();

    }

    //设置用户应用标题点击事件
    private void SetUserAppTitleOnClickListener() {
        userAppTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopupWindow();
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
                    dismissPopupWindow();
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

    //设置系统应用标题点击事件
    private void SetSystemAppTitleOnClickListener() {

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

    }

    //初始化内存显示
    private void initSpaceText() {
        //获取手机内存
        long romFreeSpace = Environment.getDataDirectory().getFreeSpace();
        //获取sd卡内存
        long sdFreeSpace = new File("/storage/sdcard1").getFreeSpace();

        /*System.out.println(Environment.getDataDirectory());
        System.out.println(Environment.getExternalStorageDirectory());*/

        rom = (TextView) findViewById(R.id.romSpaceTextView);
        sd = (TextView)findViewById(R.id.sdSpaceTextView);
        //格式化获取到的内存，显示到view上
        rom.setText("手机内存可用：" + Formatter.formatFileSize(this, romFreeSpace));
        sd.setText("sd内存可用：" + Formatter.formatFileSize(this, sdFreeSpace));
    }


    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }
}


   /* class AppManageAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size()+2;
        }

        @Override
        public View getView(int position, android.view.View convertView, ViewGroup parent) {
            //第一个条目显示"下载应用:"标题
            if(position == 0){

                TextView textView = new TextView(AppManageActivity.this);
                textView.setText("下载应用:" + usrApps.size());
                textView.setPadding(12,12,12,12);
                textView.setTextSize(20);
                textView.setTextColor(getResources().getColor(R.color.white));

                return textView;
            }else if (position == usrApps.size()+1){//显示完下载应用后的第一个条目。显示"系统应用:"标题
                TextView textView = new TextView(AppManageActivity.this);
                textView.setText("系统应用:" + systemApps.size());
                textView.setTextSize(20);
                textView.setPadding(12, 12, 12,12);
                textView.setTextColor(getResources().getColor(R.color.white));
                return textView;
            }else {
                ViewHolder viewHolder;
                //当缓存不为空，且缓存对象是RelativeLayout类型
                if (convertView != null && convertView instanceof RelativeLayout){
                    viewHolder = (ViewHolder) convertView.getTag();
                }else{
                    viewHolder = new ViewHolder();
                    convertView = View.inflate(AppManageActivity.this,R.layout.app_manage_item,null);
                    viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
                    viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                    viewHolder.tvSpaceType = (TextView)convertView.findViewById(R.id.tv_space_type);
                    viewHolder.tvAppSize = (TextView) convertView.findViewById(R.id.tv_app_size);

                    convertView.setTag(viewHolder);
                }
                AppInfo appInfo;
                //在两个标题之间显示下载应用
                if (position <= usrApps.size()){
                    appInfo = usrApps.get(position - 1);
                }else{//在其他位置显示系统应用
                    appInfo = systemApps.get(position - usrApps.size()-2);
                }
                viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
                viewHolder.tvAppName.setText(appInfo.getAppName());
                viewHolder.tvAppSize.setText(Formatter.formatFileSize(AppManageActivity.this,appInfo.getAppSize()));
                if (appInfo.isInExternalStorage()) {
                    viewHolder.tvSpaceType.setText("sd卡内存");
                }else{
                    viewHolder.tvSpaceType.setText("手机内存");
                }
                return convertView;
            }
        }
    }
*/