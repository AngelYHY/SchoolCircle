package com.myschool.schoolcircle.ui.activity.school.markets;

import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_market;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

/**
 * 上架商品界面
 */
public class PutAwayCommodityActivity extends BaseActivity {

    @Bind(R.id.tb_commodity_publish)
    Toolbar mTbCommodityPublish;
    @Bind(R.id.iv_commodity_picture)
    ImageView mIvCommodityPicture;
    @Bind(R.id.ll_commodity)
    LinearLayout mLlCommodity;
    @Bind(R.id.tv_commodity_describe)
    TextView mTvCommodityDescribe;
    @Bind(R.id.et_conmodity_describe)
    EditText mEtConmodityDescribe;
    @Bind(R.id.tv_commodity_seal)
    TextView mTvCommoditySeal;
    @Bind(R.id.et_commodity_seal)
    EditText mEtCommoditySeal;
    @Bind(R.id.tv_commodity_cost)
    TextView mTvCommodityCost;
    @Bind(R.id.et_commodity_cost)
    EditText mEtCommodityCost;
    @Bind(R.id.tv_commodity_contant)
    TextView mTvCommodityContant;
    @Bind(R.id.et_commodity_contant)
    EditText mEtCommodityContant;
    @Bind(R.id.ll_commodity_publish)
    LinearLayout llCommodityPublish;

    private Tb_market mMarket;
    private ProgressDialog mProgressDialog;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_putawaycommodity);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        initView();
////        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(mTbCommodityPublish);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "发布商品中...");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_putawaycommodity;
    }

    private void initData() {
//        mMarket = application.getUser();
    }

    //解析标题栏布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //初始化标题栏item点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_activity_publish:
                //发布商品
                showSnackBarShort(llCommodityPublish, "发布商品");
                Tb_market tbMarket = makeTbMarket();
                if (tbMarket != null) {
                    publishMarket(tbMarket);
                } else {
                    showSnackBarLong(llCommodityPublish, "需要输入内容后才能发布");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //构造动态表
    private Tb_market makeTbMarket() {
        String conmoditydescribe = mEtConmodityDescribe.getText().toString();
        if (prove(conmoditydescribe)) {

            List<String> list = new ArrayList<>();
            Gson gson = new Gson();
            String imageList = gson.toJson(list);

            Tb_market tbMarket = new Tb_market();
            tbMarket.setMarketImage(R.drawable.ic_activity);
            tbMarket.setMarketDescribe(mMarket.getMarketDescribe());
            tbMarket.setMarketPrice(mMarket.getMarketPrice());
            tbMarket.setMarketTotal(mMarket.getMarketTotal());
            tbMarket.setMarketPrice(mMarket.getMarketPrice());
            tbMarket.setUserPhone(mMarket.getUserPhone());
            return tbMarket;
        }
        return null;
    }

    //验证发布内容
    private boolean prove(String text) {
        if (!text.isEmpty()) {
            return true;
        }
        return false;
    }

    //发布商品
    private void publishMarket(Tb_market tb) {
        mProgressDialog.show();
        RequestParams params = new RequestParams(URL + "Market");
        params.setConnectTimeout(8000);
        Gson gson = new Gson();
        String data = gson.toJson(tb);
        params.addBodyParameter("type", "publishMarket");
        params.addBodyParameter("data", data);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(llCommodityPublish, "发布成功");
                } else {
                    showSnackBarLong(llCommodityPublish, "发布失败");
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
                mProgressDialog.cancel();
            }
        });
    }
}
