package com.myschool.schoolcircle.ui.activity.concact.single;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.StringRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.FriendsAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.SnackbarUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class AddFriendsActivity extends BaseActivity
        implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    @Bind(R.id.ib_back)
    ImageView ibBack;
    @Bind(R.id.sv_friends)
    SearchView svFriends;
    @Bind(R.id.tb_add_friends)
    Toolbar tbAddFriends;
    @Bind(R.id.lv_friends)
    ListView lvFriends;
    @Bind(R.id.srl_search_friends)
    SwipeRefreshLayout srlSearchFriends;
    @Bind(R.id.ll_add_friends)
    LinearLayout llAddFriends;
    @Bind(R.id.tv_tip)
    TextView tvTip;

    private ProgressDialog mProgressDialog;
    private List<Tb_user> mUsers;
    private FriendsAdapter mAdapter;
    private Tb_user mUser;
    private Callback.Cancelable post;
    private boolean flag;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    srlSearchFriends.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    showSnackBarLong(llAddFriends, "刷新失败");
                    srlSearchFriends.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_add_friends);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化控件
    protected void initView() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        RefreshUtil.initRefreshView(srlSearchFriends);
        svFriends.setOnQueryTextListener(this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("发送中...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_add_friends;
    }

    //初始化数据
    private void initData() {
        mUser = application.getUser();
        mUsers = new ArrayList<>();
        mAdapter = new FriendsAdapter(this, mUsers, R.layout.item_contact_classmate);
        lvFriends.setAdapter(mAdapter);
        lvFriends.setOnItemClickListener(this);
        initFriends();
    }

    //提交搜索
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {
            if (post != null && !post.isCancelled()) {
                post.cancel();
            }
            searchFriends(query);
        }
        return true;
    }

    //搜索栏文字改变的触发的事件
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            flag = true;
            if (post != null && !post.isCancelled()) {
                post.cancel();
            }
            searchFriends(newText);
        } else {
            initFriends();
//            mUsers.clear();
//            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    //搜索好友
    private void searchFriends(String strSearch) {
        srlSearchFriends.setRefreshing(true);
        RequestParams params = new RequestParams(URL + "SearchFriends");
        params.addBodyParameter("type", "search");
        params.addBodyParameter("search", strSearch);
        params.setConnectTimeout(8000);

        post = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                updateList(result);
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

    //初始化推荐好友
    private void initFriends() {
        flag = false;
        tvTip.setText("你可能想要寻找他们");
        srlSearchFriends.setRefreshing(true);
        RequestParams params = new RequestParams(URL + "SearchFriends");
        params.addBodyParameter("type", "init");
        params.addBodyParameter("username", mUser.getUsername());
        params.setConnectTimeout(8000);

        post = x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                updateList(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL,500);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //更新列表
    private void updateList(String result) {

        if (!"nothing".equals(result)) {
            if (flag) {
                tvTip.setText("为你搜索到他们");
            }
            Gson gson = new Gson();
            Type type = new TypeToken<List<Tb_user>>() {
            }.getType();

            List<Tb_user> data = gson.fromJson(result, type);
            mUsers.clear();
            mUsers.addAll(data);
            mHandler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);

        } else {
            if (flag) {
                tvTip.setText("没有搜索到符合条件的用户");
            } else {
                tvTip.setText("完善个人信息可以帮助你更快地寻找好友哦");
            }
            mUsers.clear();
            mHandler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        add(i);
    }

    private void add(final int i) {
        String targetUsername = mUsers.get(i).getUsername();
        showAlertDialog(R.string.add_friends, targetUsername);
    }

    //显示填写验证信息对话框
    private void showAlertDialog(@StringRes final int resId, final String targetUsername) {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_layout_add_friend, null);

        new AlertDialog.Builder(this)
                .setTitle(resId)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        EditText etInput = (EditText) dialogView.findViewById(R.id.et_input);
                        String input = etInput.getText().toString();
                        if (!input.isEmpty()) {
                            //发送好友申请
                            mProgressDialog.show();
                            saveMessageInfo(makeApplyData(targetUsername, input));
                        } else {
                            SnackbarUtil.showSnackBarLong(llAddFriends,
                                    R.string.prove_info_cannot_be_empty);
                        }
                    }
                }).show();
    }

    //将申请信息构造成json
    private String makeApplyData(String targetUser, String applyInfo) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Gson gson = new Gson();

        //构造信息表
        Tb_message_info tb = new Tb_message_info();
        tb.setReceiverUsername(targetUser);
        tb.setSender(mUser);
        tb.setContent(applyInfo);
        tb.setType("friendApply");
        tb.setTime(time);
        //转换成json
        return gson.toJson(tb);
    }

    //将信息保存到自己的服务器
    private void saveMessageInfo(String applyData) {
        RequestParams params = new RequestParams(URL + "Message");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "doApply");
        params.addBodyParameter("applyData", applyData);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    SnackbarUtil.showSnackBarShort(llAddFriends, R.string.send_success);
                } else {
                    SnackbarUtil.showSnackBarShort(llAddFriends, "发送失败");
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

    @Override
    protected void onDestroy() {
        //取消事件接收类的注册
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

    @OnClick(R.id.ib_back)
    public void onClick() {
        finish();
    }
}
