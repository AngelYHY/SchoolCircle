package com.myschool.schoolcircle.ui.activity.school.markets;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.MyInformRecyclerAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_order;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.HidingScrollListener;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.ToastUtil;
import com.myschool.schoolcircle.utils.ViewVisibleManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

/**
 * 下单通知界面
 */
public class MyInformActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.rv_inform)
    RecyclerView mRvInform;//商品列表
    @Bind(R.id.srl_myinform)
    SwipeRefreshLayout mSrlMyinform;//刷新控件
    @Bind(R.id.tb_inform)
    Toolbar mTbInform;//下单通知

    private List<Tb_order> orders;
    private MyInformRecyclerAdapter adapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    mSrlMyinform.setRefreshing(false);
                    break;

                case HandlerKey.REFRESH_FAIL:
                    mSrlMyinform.setRefreshing(false);
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_myinform);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        initView();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(mTbInform);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //初始化下拉刷新控件
        RefreshUtil.initRefreshView(mSrlMyinform, this);
        initRecyclerView();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_myinform;
    }

    //初始化列表
    private void initRecyclerView() {
        mRvInform.setLayoutManager(new LinearLayoutManager(this));
        orders = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            orders.add(new Tb_order("R.drawable.ic_activity","名称","正在处理","25.00","苏州"));
//        }

        adapter = new MyInformRecyclerAdapter(orders);
        mRvInform.setAdapter(adapter);

        //上滑下滑列表隐藏显示
        mRvInform.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                ViewVisibleManager.hideViews(mTbInform, null);
            }

            @Override
            public void onShow() {
                ViewVisibleManager.showViews(mTbInform, null);
            }
        });

        mRvInform.setItemAnimator(new DefaultItemAnimator());

//        mSrlMyorder.setRefreshing(true);
//        postAllActivityData();
    }

    //向服务器请求获取所有活动信息
    private void postAllActivityData() {
        RequestParams params = new RequestParams(URL + "Activity");
        params.addBodyParameter("type", "getAllActivity");

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Tb_order>>() {
                    }.getType();
                    List<Tb_order> data = gson.fromJson(result, type);
                    orders.clear();
                    orders.addAll(data);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showToast(MyInformActivity.this, "数据请求失败", Toast.LENGTH_LONG);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mSrlMyinform.setRefreshing(false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        //取消事件接收类的注册
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

    //下拉刷新事件
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doRefresh();
                    Thread.sleep(500);//休眠0.5秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);
            }
        }).start();
    }

    //刷新数据
    private void doRefresh() {
        postAllActivityData();
    }
}
