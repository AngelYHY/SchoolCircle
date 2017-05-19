package com.myschool.schoolcircle.utils;

import android.support.v4.widget.SwipeRefreshLayout;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/7/24.
 */
public class RefreshUtil {

    //初始化下拉刷新控件
    public static void initRefreshView(SwipeRefreshLayout refreshView,
                                       SwipeRefreshLayout.OnRefreshListener listener) {
        //设置下拉进度条的四种颜色
        refreshView.setColorSchemeResources(R.color.colorPrimary,
                R.color.green_400,
                R.color.orange,
                R.color.red);
        refreshView.setSize(SwipeRefreshLayout.DEFAULT);//进度条大小
        refreshView.setProgressBackgroundColorSchemeResource(R.color.white);//进度条背景颜色
        refreshView.setProgressViewOffset(true,-80,220);
        refreshView.setOnRefreshListener(listener);
    }

    //初始化下拉刷新控件
    public static void initRefreshView(SwipeRefreshLayout refreshView) {
        //设置下拉进度条的四种颜色
        refreshView.setColorSchemeResources(R.color.colorPrimary,
                R.color.green_400,
                R.color.orange,
                R.color.red);
        refreshView.setSize(SwipeRefreshLayout.DEFAULT);//进度条大小
        refreshView.setProgressBackgroundColorSchemeResource(R.color.white);//进度条背景颜色
        refreshView.setProgressViewOffset(true,-80,220);
    }
}
