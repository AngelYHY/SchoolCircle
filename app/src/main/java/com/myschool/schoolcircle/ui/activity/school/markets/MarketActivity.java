package com.myschool.schoolcircle.ui.activity.school.markets;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.MarketRecyclerAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_market;
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
 * 跳蚤市场界面
 */
public class MarketActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.rv_market)
    RecyclerView rvMarket;
    @Bind(R.id.srl_market)
    SwipeRefreshLayout srlMarket;
    @Bind(R.id.tb_market)
    Toolbar tbMarket;

    private List<Tb_market> mMarkets;
    private MarketRecyclerAdapter adapter;
    private int start = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlMarket.setRefreshing(false);
                    break;

                case HandlerKey.REFRESH_FAIL:
                    srlMarket.setRefreshing(false);
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_market);
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

        setSupportActionBar(tbMarket);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlMarket, this);
        initRecyclerView();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_market;
    }

    private void initRecyclerView() {
        rvMarket.setLayoutManager(new LinearLayoutManager(this));
        mMarkets = new ArrayList<>();

        adapter = new MarketRecyclerAdapter(mMarkets);
        adapter.setOnItemClickListener(new MarketRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtil.showToast(MarketActivity.this, position + "你点击了", Toast.LENGTH_SHORT);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        rvMarket.setAdapter(adapter);
        rvMarket.setLayoutManager(new GridLayoutManager(this, 2));

        //上滑下滑列表隐藏显示
        rvMarket.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                ViewVisibleManager.hideViews(tbMarket, null);
            }

            @Override
            public void onShow() {
                ViewVisibleManager.showViews(tbMarket, null);
            }
        });

        //获取所有商品
        getAllMarket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_market_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //返回上一级
                finish();
                break;

            case R.id.menu_my_market:
                //进入我的商品
                //ToastUtil.showToast(this,"我的商品",Toast.LENGTH_SHORT);
                intentToActivity(MyCommodityActivity.class);
                break;

            case R.id.menu_market_list:
                //列表展示
                rvMarket.setLayoutManager(new LinearLayoutManager(this));
                break;

            case R.id.menu_market_grid:
                //网格展示
                rvMarket.setLayoutManager(new GridLayoutManager(this, 2));
                break;

            default:
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

    private void doRefresh() {
        getAllMarket();
    }

    private void getAllMarket() {
        RequestParams params = new RequestParams(URL + "Market");
        params.addBodyParameter("type", "getAllMarket");
        params.addBodyParameter("start", start + "");

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ArrayList<Tb_market>>() {
                    }.getType();
                    List<Tb_market> data = gson.fromJson(result, type);
                    mMarkets.clear();
                    mMarkets.addAll(data);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showToast(MarketActivity.this, "数据请求失败", Toast.LENGTH_LONG);
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
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
            }
        });
    }
}