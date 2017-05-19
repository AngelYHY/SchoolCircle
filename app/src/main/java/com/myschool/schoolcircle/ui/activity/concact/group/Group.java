package com.myschool.schoolcircle.ui.activity.concact.group;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.GroupAdapter;
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
import butterknife.ButterKnife;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class Group extends BaseActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_group)
    Toolbar tbGroup;
    @Bind(R.id.lv_group)
    ListView lvGroup;
    @Bind(R.id.srl_group)
    SwipeRefreshLayout srlGroup;
    @Bind(R.id.ll_group)
    LinearLayout llGroup;

    private GroupAdapter mAdapter;
    private List<Tb_group> mGroups;
    private AlertDialog mAlertDialog;
    private Callback.Cancelable post;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlGroup.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    showSnackBarLong(llGroup,"刷新失败");
                    srlGroup.setRefreshing(false);
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_group);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化数据
    private void initData() {
        mGroups = new ArrayList<>();
        mAdapter = new GroupAdapter(this, mGroups, R.layout.item_group);
        lvGroup.setAdapter(mAdapter);
        srlGroup.setRefreshing(true);
        postMyGroupInformation();
    }

    protected void initView() {
        setSupportActionBar(tbGroup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlGroup, this);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_group;
    }

    //进入群聊
    public void enterGroupChat(Tb_group group) {
        Intent intent = new Intent(this,GroupChat.class);
        intent.putExtra("group",group);
        startActivityForResult(intent,2);
//        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                try {

                    postMyGroupInformation();

                } catch (Exception e) {
                    mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 600);
                }
            }
        }.start();
    }

    //向服务器请求获取与我相关的群组信息
    private void postMyGroupInformation() {
        RequestParams params = new RequestParams(URL + "Group");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "postMyGroupInformation");
        params.addBodyParameter("username", application.getMyInfo().getUserName());

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
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL,600);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS,600);
            }
        });
    }

    //处理结果信息
    private void doResult(String result) {
        if (!"nothing".equals(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tb_group>>() {}.getType();
            List<Tb_group> data = gson.fromJson(result, type);
            mGroups.clear();
            mGroups.addAll(data);
            mAdapter.notifyDataSetChanged();
            mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
        } else {
            mGroups.clear();
            mAdapter.notifyDataSetChanged();
            mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_join_group:
                intentToActivity(SearchGroup.class);
                break;
            case R.id.menu_create_group:
                showAlertDialog();
                break;
            case R.id.menu_group_apply:
                intentToActivity(GroupApply.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //显示对话框
    private void showAlertDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_layout_select_group_type, null);

        LinearLayout llPersonal = (LinearLayout) dialogView
                .findViewById(R.id.ll_personal);
        LinearLayout llSpecialty = (LinearLayout) dialogView
                .findViewById(R.id.ll_specialty);
        LinearLayout llAssociation = (LinearLayout) dialogView
                .findViewById(R.id.ll_association);

        llPersonal.setOnClickListener(this);
        llSpecialty.setOnClickListener(this);
        llAssociation.setOnClickListener(this);

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setLayout(900, 800);
    }

    //学校，年级，专业选择点击事件
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, CreateGroup.class);
        switch (view.getId()) {
            case R.id.ll_personal:
                intent.putExtra("type", "personal");
                startActivityForResult(intent, 1);
                mAlertDialog.cancel();
                break;
            case R.id.ll_specialty:
                intent.putExtra("type", "specialty");
                startActivityForResult(intent, 1);
                mAlertDialog.cancel();
                break;
            case R.id.ll_association:
                intent.putExtra("type", "association");
                startActivityForResult(intent, 1);
                mAlertDialog.cancel();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            srlGroup.setRefreshing(true);
            postMyGroupInformation();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (post != null && !post.isCancelled()) {
            //结束请求
            post.cancel();
        }
    }
}
