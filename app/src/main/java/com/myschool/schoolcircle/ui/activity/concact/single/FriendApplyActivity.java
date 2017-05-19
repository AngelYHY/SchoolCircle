package com.myschool.schoolcircle.ui.activity.concact.single;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.FriendApplyAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_message_info;
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
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;

public class FriendApplyActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_friend_apply)
    Toolbar tbFriendApply;
    @Bind(R.id.lv_friend_apply)
    ListView lvFriendApply;
    @Bind(R.id.ll_friend_apply)
    LinearLayout llFriendApply;
    @Bind(R.id.srl_friend_apply)
    SwipeRefreshLayout srlFriendApply;

    private ProgressDialog mProgressDialog;
    public static FriendApplyActivity activity;
    private UserInfo mUser;
    private List<Tb_message_info> mApplys;
    private FriendApplyAdapter mAdapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    srlFriendApply.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    showSnackBarLong(llFriendApply, "刷新失败");
                    srlFriendApply.setRefreshing(false);
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_friend_apply);
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
        setSupportActionBar(tbFriendApply);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlFriendApply, this);
        srlFriendApply.setRefreshing(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_friend_apply;
    }

    //初始化数据
    private void initData() {
        activity = this;
        mUser = application.getMyInfo();
        mApplys = new ArrayList<>();
        mAdapter = new FriendApplyAdapter(this, mApplys, R.layout.item_apply);
        lvFriendApply.setAdapter(mAdapter);
        refreshView();
    }

    //更新界面
    private void refreshView() {
        getFriendApply();
    }

    //获取好友的申请信息
    private void getFriendApply() {
        RequestParams params = new RequestParams(URL + "Message");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "getApply");
        params.addBodyParameter("username", mUser.getUserName());
        params.addBodyParameter("applyType", "friendApply");

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
            Type type = new TypeToken<List<Tb_message_info>>() {
            }.getType();
            List<Tb_message_info> data = gson.fromJson(result, type);
            mApplys.clear();
            mApplys.addAll(data);
        } else {
            mApplys.clear();
        }
        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
    }

    //同意好友申请，添加好友
    public void doAdd(String friendName) {
        mProgressDialog.setMessage("添加好友中...");
        mProgressDialog.show();

        RequestParams params = new RequestParams(URL + "FriendsManager");
        params.addBodyParameter("type", "addFriend");
        params.addBodyParameter("friend_name", friendName);
        params.addBodyParameter("user_name", mUser.getUserName());
        params.setConnectTimeout(8000);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(llFriendApply, R.string.add_success);
                } else {
                    showSnackBarLong(llFriendApply, R.string.add_fail);
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

    //删除好友申请
    public void deleteFriendApply(final Tb_message_info tb) {
        new AlertDialog.Builder(this)
                .setTitle("提醒")
                .setMessage("确定要删除这条好友申请吗？")
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
                    showSnackBarLong(llFriendApply, "删除成功");
                } else {
                    showSnackBarLong(llFriendApply, "删除失败");
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_add:
                intentToActivity(AddFriendsActivity.class);
                break;
            default:
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
