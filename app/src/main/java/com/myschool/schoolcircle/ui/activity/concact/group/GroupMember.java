package com.myschool.schoolcircle.ui.activity.concact.group;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.FriendsAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_user;
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
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class GroupMember extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.tb_group_member)
    Toolbar tbGroupMember;
    @Bind(R.id.lv_group_member)
    ListView lvGroupMember;
    @Bind(R.id.srl_group_member)
    SwipeRefreshLayout srlGroupMember;
    @Bind(R.id.ll_group_member)
    LinearLayout llGroupMember;

    private List<Tb_user> mMembers;
    private FriendsAdapter mAdapter;
    private String groupId;
    private Callback.Cancelable post;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    srlGroupMember.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlGroupMember.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_group_member);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        setSupportActionBar(tbGroupMember);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlGroupMember,this);
        srlGroupMember.setRefreshing(true);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_group_member;
    }

    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");
        mMembers = new ArrayList<>();
        mAdapter = new FriendsAdapter(this,mMembers,R.layout.item_contact_classmate);
        lvGroupMember.setAdapter(mAdapter);
        postAllGroupMember();
    }

    //刷新
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    postAllGroupMember();

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL,600);
                }
            }
        }).start();
    }

    //向服务器请求获取该群组的所有成员
    private void postAllGroupMember() {
        RequestParams params = new RequestParams(URL+"Group");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type","getAllGroupMember");
        params.addBodyParameter("groupId",groupId);

        post = x.http().post(params, new Callback.CommonCallback<String>() {
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

    //处理服务器返回的结果
    private void doResult(String result) {
        if (!"nothing".equals(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tb_user>>() {}.getType();
            List<Tb_user> data = gson.fromJson(result,type);
            mMembers.clear();
            mMembers.addAll(data);
            mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS,600);
        } else {
            mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS,600);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (post != null && !post.isCancelled()) {
            post.cancel();
        }
    }
}
