package com.myschool.schoolcircle.ui.activity.school.markets;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.myschool.schoolcircle.adapter.MyPutawayRecyclerAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_market;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HidingScrollListener;
import com.myschool.schoolcircle.utils.ViewVisibleManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

/**
 * 我的上架界面
 */
public class MyPutawayActivity extends BaseActivity {

    @Bind(R.id.rv_myputaway)
    RecyclerView mRvMyputaway;
    @Bind(R.id.tb_myputaway)
    Toolbar mTbMyputaway;

    private List<Tb_market> items;
    private MyPutawayRecyclerAdapter adapter;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_myputaway);
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

        setSupportActionBar(mTbMyputaway);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initRecyclerView();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_myputaway;
    }

    private void initRecyclerView() {
        mRvMyputaway.setLayoutManager(new LinearLayoutManager(this));
        items = new ArrayList<>();
//        for (int i = 0; i < 5; i++) {
//            items.add(new Tb_market("上架-名称", R.drawable.ic_activity, "15.00"));
//        }
        adapter = new MyPutawayRecyclerAdapter(items);
        adapter.setOnItemClickListener(new MyPutawayRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        mRvMyputaway.setAdapter(adapter);

        //上滑下滑列表隐藏显示
        mRvMyputaway.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                ViewVisibleManager.hideViews(mTbMyputaway, null);
            }

            @Override
            public void onShow() {
                ViewVisibleManager.showViews(mTbMyputaway, null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_myputaway_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_myputaway_publish:
                //发布商品
                //ToastUtil.showToast(this, "发布商品", Toast.LENGTH_SHORT);
                readyGo(PutAwayCommodityActivity.class);
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
}