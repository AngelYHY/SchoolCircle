package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.myschool.schoolcircle.adapter.FriendsAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
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
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.api.BasicCallback;

public class ParticipatorActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.lv_participator)
    ListView lvParticipator;
    @Bind(R.id.srl_participator)
    SwipeRefreshLayout srlParticipator;
    @Bind(R.id.tb_participator)
    Toolbar tbParticipator;
    @Bind(R.id.ll_Participator)
    LinearLayout llParticipator;

    private String activity_id;
    private String activityTheme;
    private List<Tb_user> mParticipators;
    private List<String> mParticipatorUsernames;
    private FriendsAdapter mAdapter;

    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlParticipator.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlParticipator.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_participator);
//        ButterKnife.bind(this);
//        JMessageClient.registerEventReceiver(this);
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        activity_id = intent.getStringExtra("activity_id");
        activityTheme = intent.getStringExtra("activityTheme");
        mParticipators = new ArrayList<>();
        mParticipatorUsernames = new ArrayList<>();
        mAdapter = new FriendsAdapter(this, mParticipators, R.layout.item_contact_classmate);
        lvParticipator.setAdapter(mAdapter);
        postAllParticipators();
    }

    //初始化界面
    protected void initView() {
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(tbParticipator);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this);
        RefreshUtil.initRefreshView(srlParticipator, this);
        srlParticipator.setRefreshing(true);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_participator;
    }

    //向服务器请求获取所有参与者数据
    private void postAllParticipators() {
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "getAllParticipators");
        params.addBodyParameter("activity_id", activity_id);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"nothing".equals(result)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Tb_user>>() {
                    }.getType();
                    List<Tb_user> data = gson.fromJson(result, type);

                    mParticipators.clear();
                    mParticipators.addAll(data);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mParticipators.clear();
                    mAdapter.notifyDataSetChanged();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_group_chat_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_group:
                //创建临时群组
                new AlertDialog.Builder(this)
                        .setTitle("提醒")
                        .setMessage("建议在确认群组成员后再创建临时群组，是否现在创建？")
                        .setNegativeButton("不是现在", null)
                        .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //创建临时群组
                                createTempGroup();
                            }
                        }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //创建临时群组
    private void createTempGroup() {
        if (mParticipators.size() > 0) {
            mProgressDialog.setMessage("正在创建临时群组...");
            mProgressDialog.show();
            //如果有参与者，初始化参与者用户名集合
            for (Tb_user mParticipator : mParticipators) {
                mParticipatorUsernames.add(mParticipator.getUsername());
            }

            //创建群组
            JMessageClient.createGroup(activityTheme+"(临时群)", "temp", new CreateGroupCallback() {
                @Override
                public void gotResult(int i, String s, final long l) {
                    if (i == 0) {
                        //如果创建成功，就将参与者添加到群组中
                        JMessageClient.addGroupMembers(l, mParticipatorUsernames,
                                new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
                                            //如果添加成员成功,就发送第一条通知
                                            sendFirstMessage(l);
                                            mProgressDialog.cancel();
                                            showSnackBarLong(llParticipator,
                                                    "创建成功，快去看看小伙伴们吧");
                                        } else {
                                            mProgressDialog.cancel();
                                            showSnackBarLong(llParticipator,
                                                    "添加成员失败");
                                        }
                                    }
                                });
                    } else {
                        mProgressDialog.cancel();
                        showSnackBarLong(llParticipator, "创建临时群组失败");
                    }
                }
            });
        } else {
            showSnackBarLong(llParticipator,"必须要有参与者才能创建临时群组");
        }
    }

    //发送第一条通知
    private void sendFirstMessage(long id) {
        Conversation conversation = Conversation.createGroupConversation(id);

        cn.jpush.im.android.api.model.Message message = conversation
                .createSendTextMessage("欢迎大家参与本次活动！", "活动通知");

        JMessageClient.sendMessage(message);
    }

    //刷新
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    postAllParticipators();
//                    Thread.sleep(500);//休眠0.5秒

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
}
