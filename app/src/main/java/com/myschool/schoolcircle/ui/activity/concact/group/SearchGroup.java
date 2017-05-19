package com.myschool.schoolcircle.ui.activity.concact.group;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.SearchGroupAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_group;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class SearchGroup extends BaseActivity
        implements SearchView.OnQueryTextListener {

    @Bind(R.id.sv_group)
    SearchView svGroup;
    @Bind(R.id.tb_search_group)
    Toolbar tbSearchGroup;
    @Bind(R.id.ib_back)
    ImageView ibBack;
    @Bind(R.id.lv_group)
    ListView lvGroup;
    @Bind(R.id.srl_search_group)
    SwipeRefreshLayout srlSearchGroup;
    @Bind(R.id.ll_search_group)
    LinearLayout llSearchGroup;
    @Bind(R.id.tv_tip)
    TextView tvTip;

    private List<Tb_group> mGroups;
    private SearchGroupAdapter mAdapter;
    private Callback.Cancelable post;
    private boolean flag;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlSearchGroup.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlSearchGroup.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_search_group);
//        ButterKnife.bind(this);
//
//        initData();
//        initView();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化控件
    protected void initView() {
        RefreshUtil.initRefreshView(srlSearchGroup);
        svGroup.setOnQueryTextListener(this);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_search_group;
    }

    //初始化数据
    private void initData() {
        mGroups = new ArrayList<>();
        mAdapter = new SearchGroupAdapter(this, application.getMyInfo(),
                mGroups, R.layout.item_group);
        lvGroup.setAdapter(mAdapter);
        searchGroup("1");
    }

    @OnClick(R.id.ib_back)
    public void onClick() {
        finish();
    }

    //提交搜索
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!TextUtils.isEmpty(query)) {
            if (post != null && !post.isCancelled()) {
                post.cancel();
            }
            searchGroup(query);
        }
        return true;
    }

    //搜索栏文字改变的触发的事件
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!TextUtils.isEmpty(newText)) {
            if (!flag) {
                flag = true;
            }
            if (post != null && !post.isCancelled()) {
                post.cancel();
            }
            searchGroup(newText);
        } else {
            flag = false;
            tvTip.setText("你可能想要加入");
            searchGroup("1");
//            mGroups.clear();
//            mAdapter.notifyDataSetChanged();

        }
        return true;
    }

    //向服务器请求获取所有群组信息
    private void searchGroup(String newText) {
        srlSearchGroup.setRefreshing(true);
        RequestParams params = new RequestParams(URL + "Group");
        params.setConnectTimeout(8000);
        String schoolName = application.getMyInfo().getRegion();
        if (schoolName.isEmpty()) {
            //如果没有设置所属学校，则查找所有数据
            params.addBodyParameter("type", "searchGroup");
        } else {
            //如果设置了所属学校，则按所属学校查找数据
            params.addBodyParameter("type", "searchGroupInMySchool");
            params.addBodyParameter("school", schoolName);
        }
        params.addBodyParameter("search", newText);

        post = x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                doResult(result);
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

    //处理结果信息
    private void doResult(String result) {
        if (!"nothing".equals(result)) {
            if (flag) {
                tvTip.setText("以下是为你搜索到的群组");
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tb_group>>() {
            }.getType();
            List<Tb_group> data = gson.fromJson(result, type);
            mGroups.clear();
            mGroups.addAll(data);
            mAdapter.notifyDataSetChanged();
            mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
        } else {
            if (flag) {
                tvTip.setText("没有搜索到符合条件的群组");
            }
            mGroups.clear();
            mAdapter.notifyDataSetChanged();
            mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 600);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (post != null && !post.isCancelled()) {
            post.cancel();
        }
    }
}
