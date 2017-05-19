package com.myschool.schoolcircle.ui.activity.school.markets;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.main.R;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

/**
 * 我的商品界面
 */
public class MyCommodityActivity extends BaseActivity {


    @Bind(R.id.tb_mycommodity)
    Toolbar mTbMycommodity;
    @Bind(R.id.ib_order)
    ImageButton mIbOrder;
    @Bind(R.id.rl_order)
    RelativeLayout mRlOrder;
    @Bind(R.id.ib_putaway)
    ImageButton mIbPutaway;
    @Bind(R.id.rl_putaway)
    RelativeLayout mRlPutaway;
    @Bind(R.id.ib_inform)
    ImageButton mIbInform;
    @Bind(R.id.rl_inform)
    RelativeLayout mRlInform;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_mycommodity);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        initView();
//    }

    protected void initView() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(mTbMycommodity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_mycommodity;
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //返回上一级
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.rl_order, R.id.rl_putaway, R.id.rl_inform})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_order:
                //点击进入我的订单
                //ToastUtil.showToast(this, "我的订单", Toast.LENGTH_SHORT);
                intentToActivity(MyOrderActivity.class);
                break;

            case R.id.rl_putaway:
                //点击进入我的上架
                //ToastUtil.showToast(this, "我的上架", Toast.LENGTH_SHORT);
                intentToActivity(MyPutawayActivity.class);
                break;

            case R.id.rl_inform:
                //点击进入下单通知
                //ToastUtil.showToast(this, "下单通知", Toast.LENGTH_SHORT);
                intentToActivity(MyInformActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //取消事件接收类的注册
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }
}