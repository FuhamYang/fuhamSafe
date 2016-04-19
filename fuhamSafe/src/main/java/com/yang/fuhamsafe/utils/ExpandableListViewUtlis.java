package com.yang.fuhamsafe.utils;

import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.yang.fuhamsafe.bean.AppInfo;

import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

/**
 * Created by fuhamyang on 2016/3/17.
 */
public class ExpandableListViewUtlis {

    //设置ExpandableListView的滑动侦听事件，处理虚拟条目
    public static void SetExpandableListViewOnScrollListener(
            ExpandableListView expandableListView,  final Map<String,TextView> parentTexts,
            final TextView userAppTitle, final TextView viewLine , final TextView systemAppTitle,
            final List<AppInfo> usrApps , final List<AppInfo> systemApps, final List<String> parentList

    ) {
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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


    //设置用户应用标题点击事件
    public static void SetUserAppTitleOnClickListener(
            final ExpandableListView expandableListView,
            final TextView userAppTitle,  final TextView systemAppTitle,
            final List<String> parentList


    ) {
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
                        expandableListView.expandGroup(1);

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
                        //将全部父条目展开
                        for (int i = 0; i < parentList.size(); i++)
                            expandableListView.expandGroup(i);
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
    public static void SetSystemAppTitleOnClickListener(
            final ExpandableListView expandableListView,
            final TextView systemAppTitle
    ) {

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

}
