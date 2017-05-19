package com.myschool.schoolcircle.utils;

import android.support.v7.widget.RecyclerView;

/**
 * 用来监听上滑下滑事件以及控件是否可见
 * Created by Mr.R on 2016/7/12.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {
    private static final int HIDE_THRESHOLD = 20;//触发回调的偏移距离
    private int scrolledDistance = 0;//偏移距离（下滑是负数，上滑是正数）
    private boolean controlsVisible = true;//控件是否可见
    private int firstVisibleItem;

    //回调方法
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

//        firstVisibleItem = ((LinearLayoutManager) recyclerView
//                .getLayoutManager()).findFirstVisibleItemPosition();
        //show views if first item is first visible position and views are hidden
        //如果是第一次显示并且view是隐藏的

        super.onScrolled(recyclerView, dx, dy);
        //如果滑动偏移距离大于触发距离并且控件是可见的就隐藏
        if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
            onHide();
            controlsVisible = false;
            scrolledDistance = 0;
        } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
            onShow();
            controlsVisible = true;
            scrolledDistance = 0;
        }

//        if (firstVisibleItem == 0) {
//
//            if(!controlsVisible) {
//                onShow();
//                controlsVisible = true;
//            }
//
//        } else {
//
//        }

        //如果是可见并且是上滑 或者 不可见并且是下滑
        if((controlsVisible && dy>0) || (!controlsVisible && dy<0)) {

            scrolledDistance += dy;

        }
    }

    //使用时重写以下方法
    public abstract void onHide();//隐藏

    public abstract void onShow();//显示

}
