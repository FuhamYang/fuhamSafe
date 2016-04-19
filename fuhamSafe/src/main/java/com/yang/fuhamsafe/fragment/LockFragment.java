package com.yang.fuhamsafe.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yang.fuhamsafe.R;
import com.yang.fuhamsafe.bean.AppInfo;
import com.yang.fuhamsafe.dao.LockAppDao;
import com.yang.fuhamsafe.engine.AppInfos;
import com.yang.fuhamsafe.utils.DensityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class LockFragment extends Fragment {

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


    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1){
                expandableListView.expandGroup(1);
                systemAppTitle.setText(parentList.get(1)+systemApps.size());
                systemAppTitle.bringToFront();
                systemAppTitle.setVisibility(View.VISIBLE);
            }else if (msg.what == 3){
                //加锁应用后从新更新，界面

                appManageExpandableListAdapter.notifyDataSetChanged();
                userAppTitle.setText(parentList.get(0)+usrApps.size());
                systemAppTitle.setText(parentList.get(1) + systemApps.size());
            }
            else if (msg.what == 2){
                //将全部父条目展开
                for (int i = 0 ; i < parentList.size();i++)
                    expandableListView.expandGroup(i);
            }
            else if (msg.what == 0){
                //获取数据后，构建界面
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
                expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                    @Override
                    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                        parentClickItemAppInfo = map.get(parentList.get(groupPosition));
                        clickItemAppInfo = map.get(parentList.get(groupPosition)).get(childPosition);
                        TranslateAnimation oa = new TranslateAnimation(
                                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0
                        );
                        oa.setDuration(2000);
                        oa.setRepeatCount(0);
                        v.startAnimation(oa);
                        new Thread(){
                            @Override
                            public void run() {
                                SystemClock.sleep(2000);
                                parentClickItemAppInfo.remove(clickItemAppInfo);
                                LockAppDao lockAppDao = new LockAppDao(getActivity().getBaseContext());
                                lockAppDao.delete(clickItemAppInfo.getPackageName());
                                handler.sendEmptyMessage(3);
                            }
                        }.start();

                        return true;
                    }
                });

                expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {


                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        //直接显示虚拟的用户应用父条目
                        if (firstVisibleItem == 0) {
                            userAppTitle.setVisibility(View.VISIBLE);
                            userAppTitle.setText(parentList.get(0)+usrApps.size());
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
                        if (firstVisibleItem == usrApps.size()&&usrApps.size() != 0) {
                            //获取到已加载的真实系统应用父条目
                            TextView textView = parentTexts.get(parentList.get(1));
                            //当真实系统应用父条目，碰到虚拟用户应用父条目时

                           if (textView.getTop() < userAppTitle.getHeight()) {
                                //显示出虚拟系统应用父条目，制造出系统应用父条目置顶的效果
                                systemAppTitle.setText(parentList.get(1)+systemApps.size());
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



        }
    };





    public LockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_lock, container, false);
        setInitView(view);
        initData();
        return view;
    }


    private void initData(){

        new Thread(){
            @Override
            public void run() {

                list = new ArrayList<>();
                List<AppInfo> tempAppInfos = AppInfos.getAppInfos(getActivity().getBaseContext());
                List<String> temp = new LockAppDao(getActivity().getBaseContext()).selectAll();
                for (AppInfo appInfo:tempAppInfos) {
                    for (String name:temp){
                        if (appInfo.getPackageName().equals(name))
                            list.add(appInfo);
                    }
                }
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
                textView = new TextView(getActivity().getBaseContext());
                int pad = DensityUtils.dpToPx(getActivity().getBaseContext(), 12);
                textView.setPadding(pad,pad,pad,pad);
                textView.setTextSize(20);
                textView.setBackgroundColor(getResources().getColor(R.color.contentBackground));
                textView.setTextColor(getResources().getColor(R.color.white));
                convertView = textView;
            }else{
                textView = (TextView) convertView;
            }
            if(groupPosition == 0)
            {
                textView.setText(parentList.get(groupPosition)+usrApps.size());
            }else{
                textView.setText(parentList.get(groupPosition)+systemApps.size());
            }
            System.out.println(textView+"草泥马####1111");
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
                convertView = View.inflate(getActivity().getBaseContext(),R.layout.unlock_item,null);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.icon);
                viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
                viewHolder.ivUnlock = (ImageView) convertView.findViewById(R.id.iv_lock);
                convertView.setTag(viewHolder);
            }
            //从map中获取数据信息
            AppInfo appInfo = map.get(parentList.get(groupPosition)).get(childPosition);
            viewHolder.ivIcon.setImageDrawable(appInfo.getIcon());
            viewHolder.tvAppName.setText(appInfo.getAppName());
            viewHolder.ivUnlock.setImageResource(R.drawable.unlocked);

            return convertView;

        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    private class ViewHolder{
        ImageView ivIcon;
        TextView tvAppName;
        ImageView ivUnlock;
    }

    private void setInitView(View view){
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_app_manage);
        viewLine = (TextView) view.findViewById(R.id.viewLine);
        parentTexts = new HashMap<>();
        expandableListView = (ExpandableListView) view.findViewById(R.id.elv_app_manage);
        //为了让父条目置顶，自己设置了两个置顶的TextView,模拟父条目置顶，实际上父条目并没有置顶，
        // 而是与子条目一样滑动出屏幕
        userAppTitle = (TextView) view.findViewById(R.id.app_type_title);
        systemAppTitle = (TextView) view.findViewById(R.id.app_type_title2);
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


}
