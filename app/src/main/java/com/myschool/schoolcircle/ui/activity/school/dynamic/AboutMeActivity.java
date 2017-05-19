package com.myschool.schoolcircle.ui.activity.school.dynamic;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.AboutMeRecyclerAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class AboutMeActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.rv_aboutme)
    RecyclerView rvAboutme;
    @Bind(R.id.srl_aboutme)
    SwipeRefreshLayout srlAboutme;
    @Bind(R.id.tb_aboutme)
    Toolbar tbAboutme;
    @Bind(R.id.cl_aboutme)
    CoordinatorLayout clAboutme;

    private List<Tb_dynamic> mDynamics;
    private Tb_dynamic mTbDynamic;

    private AboutMeRecyclerAdapter adapter;

    private int start = 0;
    private boolean isLoading;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlAboutme.setRefreshing(false);
                    break;

                case HandlerKey.REFRESH_FAIL:
                    srlAboutme.setRefreshing(false);
                    break;
            }
        }
    };


//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_about_me);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//
//        initView();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(tbAboutme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlAboutme, this);

        initData();

        initRecyclerView();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_about_me;
    }

    private void initData() {
        Intent intent = getIntent();
        mTbDynamic = (Tb_dynamic) intent.getSerializableExtra("dynamicData");
    }

    private void initRecyclerView() {

        rvAboutme.setLayoutManager(new LinearLayoutManager(this));
        rvAboutme.setItemAnimator(new DefaultItemAnimator());

        srlAboutme.setRefreshing(true);

        mDynamics = new ArrayList<>();

        adapter = new AboutMeRecyclerAdapter(this, mDynamics);

        adapter.setOnItemClickListener(new AboutMeRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {
                deleteDynamic();
            }
        });

        final LinearLayoutManager manager =
                (LinearLayoutManager) rvAboutme.getLayoutManager();

        rvAboutme.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 50) {
                    int lastItemIndex = manager.findLastVisibleItemPosition();
                    //滑动到最后一个并且状态不是加载中,执行加载更多，isLoading默认值false
                    if (lastItemIndex >= mDynamics.size() - 1 && !isLoading) {
                        loadMore();
                    }
                }
            }
        });

        rvAboutme.setAdapter(adapter);
        doRefresh();
    }

    //加载更多
    private void loadMore() {
        mDynamics.add(null);
        adapter.notifyItemInserted(mDynamics.size() - 1);
        isLoading = true;
        new Thread() {
            @Override
            public void run() {
                try {
//                    start += 5;
//                    Thread.sleep(600);
                    getMyPublishDynamic();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

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

    //刷新
    private void doRefresh() {
        start = 0;
        if (!srlAboutme.isRefreshing()) {
            srlAboutme.setRefreshing(true);
        }
        getMyPublishDynamic();
    }


    //请求 我的动态
    private void getMyPublishDynamic() {
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.addBodyParameter("type", "getMyPublishDynamic");
        params.addBodyParameter("start", start + "");
        params.addBodyParameter("username", application.getMyInfo().getUserName());

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                loadData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
            }
        });
    }

    //删除动态
    private void deleteDynamic() {
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteDynamic");
        params.addBodyParameter("dynamic_id", String.valueOf(mTbDynamic.get_id()));

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                    ToastUtil.showToast(AboutMeActivity.this, "删除动态成功", Toast.LENGTH_LONG);
                } else {
                    ToastUtil.showToast(AboutMeActivity.this, "删除动态失败", Toast.LENGTH_LONG);
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

            }
        });
    }


    //加载数据
    private void loadData(String result) {
        if (!"nothing".equals(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Tb_dynamic>>() {
            }.getType();
            List<Tb_dynamic> data = gson.fromJson(result, type);
            if (isLoading) {
                //如果是上拉加载
                mDynamics.remove(mDynamics.size() - 1);
                adapter.notifyItemRemoved(mDynamics.size());
                mDynamics.addAll(data);
                isLoading = false;
            } else {
                mDynamics.clear();
                mDynamics.addAll(data);
            }
            start = mDynamics.size();
            adapter.notifyDataSetChanged();

        } else {
            if (isLoading) {
                //如果是上拉加载
                mDynamics.remove(mDynamics.size() - 1);
                adapter.notifyItemRemoved(mDynamics.size());
                isLoading = false;
            } else {
                mDynamics.clear();
                adapter.notifyDataSetChanged();
            }

            showSnackBarLong(clAboutme, "暂无更多动态");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 825 && resultCode == RESULT_OK) {
            doRefresh();
        }
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
}