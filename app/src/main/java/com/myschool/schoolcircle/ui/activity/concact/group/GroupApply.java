package com.myschool.schoolcircle.ui.activity.concact.group;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.GroupApplyAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.RefreshUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.api.BasicCallback;

public class GroupApply extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_group_apply)
    Toolbar tbGroupApply;
    @Bind(R.id.lv_group_apply)
    ListView lvGroupApply;
    @Bind(R.id.srl_group_apply)
    SwipeRefreshLayout srlGroupApply;
    @Bind(R.id.ll_group_apply)
    LinearLayout llGroupApply;

    private ProgressDialog mProgressDialog;
    private Tb_user mUser;
    private List<Tb_message_info> mApplys;
    private GroupApplyAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    srlGroupApply.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    showSnackBarLong(llGroupApply, "刷新失败");
                    srlGroupApply.setRefreshing(false);
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_group_apply);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化控件
    protected void initView() {
        setSupportActionBar(tbGroupApply);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlGroupApply, this);
        srlGroupApply.setRefreshing(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_group_apply;
    }

    //初始化数据
    private void initData() {
        mUser = application.getUser();
        mApplys = new ArrayList<>();
        mAdapter = new GroupApplyAdapter(this,mApplys, R.layout.item_apply);
        lvGroupApply.setAdapter(mAdapter);
        refreshView();
    }

    //更新界面
    private void refreshView() {
        getGroupApply();
    }

    //获取验证信息
    private void getGroupApply() {
        RequestParams params = new RequestParams(URL + "Message");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "getApply");
        params.addBodyParameter("username", mUser.getUsername());
        params.addBodyParameter("applyType","joinGroupApply");

        x.http().post(params, new Callback.CommonCallback<String>() {
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

    //处理服务器返回的结果信息
    private void doResult(String result) {
        if (!"nothing".equals(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tb_message_info>>() {}.getType();
            List<Tb_message_info> data = gson.fromJson(result, type);
            Log.i("GroupApply", "doResult: GroupApply"+result);
            mApplys.clear();
            mApplys.addAll(data);
        } else {
            mApplys.clear();
        }
        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
    }

    //向极光服务器的群组中添加群成员
    public void addGroupMember(final String id, final String username) {
        mProgressDialog.setMessage("添加群成员中...");
        mProgressDialog.show();

        long groupId = Integer.parseInt(id);
        List<String> user = new ArrayList<>();
        user.add(username);
        JMessageClient.addGroupMembers(groupId, user, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    //添加成功
                    doAdd(id,username);
                } else {
                    //添加失败
                    mProgressDialog.cancel();
                    showSnackBarLong(llGroupApply, R.string.add_fail);
                }
            }
        });
    }

    //向自己的服务器中添加数据
    private void doAdd(final String groupId, final String username) {

        RequestParams params = new RequestParams(URL + "Group");
        params.addBodyParameter("type","joinGroup");
        params.addBodyParameter("groupId", groupId);
        params.addBodyParameter("username", username);
        params.setConnectTimeout(8000);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(llGroupApply, R.string.add_success);
                } else {
                    //向自己的数据库中添加群成员失败，移除
                    removeGroupMember(groupId,username);
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

    //向自己的数据库中添加群组成员失败时，要移除极光服务器上已添加的那个群成员
    private void removeGroupMember(String id, String username) {
        long groupId = Integer.parseInt(id);
        List<String> user = new ArrayList<>();
        user.add(username);

        JMessageClient.removeGroupMembers(groupId, user, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    showSnackBarLong(llGroupApply, R.string.add_fail);
                } else {

                }
            }
        });
    }

    //删除好友申请
    public void deleteApply(final Tb_message_info tb) {
        new AlertDialog.Builder(this)
                .setTitle("提醒")
                .setMessage("确定要删除这条申请吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(tb);
                    }
                }).show();
    }

    //删除
    private void delete(final Tb_message_info tb) {
        mProgressDialog.setMessage("删除中...");
        mProgressDialog.show();
        RequestParams params = new RequestParams(URL + "Message");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteApply");
        params.addBodyParameter("id", tb.get_id() + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    mApplys.remove(tb);
                    mAdapter.notifyDataSetChanged();
                    showSnackBarLong(llGroupApply, "删除成功");
                } else {
                    showSnackBarLong(llGroupApply, "删除失败");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //刷新
    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {

                try {

                    refreshView();

                } catch (Exception e) {
                    mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 600);
                }
            }
        }.start();
    }
}
