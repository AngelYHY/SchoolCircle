package com.myschool.schoolcircle.widget;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/8/24.
 */
public class LoadListView extends ListView implements AbsListView.OnScrollListener{
    /** 底部显示正在加载的页面 */
    private View footerView = null;
    /** 存储上下文 */
    private Context context;
    /** 上拉刷新的ListView的回调监听 */
    private MyPullUpListViewCallBack myPullUpListViewCallBack;
    /** 记录第一行Item的数值 */
    private int firstVisibleItem;

    private ContentLoadingProgressBar clpLoading;
    private TextView tvLoading;

    public LoadListView(Context context) {
        super(context);
        this.context = context;
        initListView();
        initBottomView();
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initListView();
        initBottomView();
    }

    /**
     * 初始化ListView
     */
    private void initListView() {

        // 为ListView设置滑动监听
        setOnScrollListener(this);
        // 去掉底部分割线
        setFooterDividersEnabled(false);
    }

    /**
     * 初始化话底部页面
     */
    public void initBottomView() {
        if (footerView == null) {
            footerView = LayoutInflater.from(this.context).inflate(
                    R.layout.item_loading_list, null);
            clpLoading = (ContentLoadingProgressBar) footerView.findViewById(R.id.clp_loading);
            tvLoading = (TextView) footerView.findViewById(R.id.tv_loading);
            footerView.setFocusable(true);
        }
        addFooterView(footerView);
    }

    //完成加载
    public void loadDone() {
        clpLoading.setVisibility(GONE);
        tvLoading.setText("没有更多留言");
    }

    public void onScrollStateChanged(AbsListView view, int scrollState) {

        //当滑动到底部时
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                && firstVisibleItem != 0) {
            clpLoading.setVisibility(VISIBLE);
            tvLoading.setText("正在加载...");
            myPullUpListViewCallBack.scrollBottomState();
        }
    }

    //滑动监听事件
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;

        if (footerView != null) {
            //判断可视Item是否能在当前页面完全显示
            if (visibleItemCount == totalItemCount) {
                // removeFooterView(footerView);
//                footerView.setVisibility(View.GONE);//隐藏底部布局
            } else {
                // addFooterView(footerView);
                footerView.setVisibility(View.VISIBLE);//显示底部布局
            }
        }

    }

    public void setMyPullUpListViewCallBack(MyPullUpListViewCallBack myPullUpListViewCallBack) {
        this.myPullUpListViewCallBack = myPullUpListViewCallBack;
    }

    /**
     * 上拉刷新的ListView的回调监听
     */
    public interface MyPullUpListViewCallBack {
        void scrollBottomState();
    }
}
