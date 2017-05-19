package com.myschool.schoolcircle.ui.activity.concact.group;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_group;
import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.api.BasicCallback;

public class GroupDetail extends BaseActivity {

    @Bind(R.id.tb_group)
    Toolbar tbGroup;
    @Bind(R.id.iv_group_head)
    ImageView ivGroupHead;
    @Bind(R.id.tv_label)
    TextView tvLabel;
    @Bind(R.id.cv_label)
    CardView cvLabel;
    @Bind(R.id.tv_create_time)
    TextView tvCreateTime;
    @Bind(R.id.tv_group_describe)
    TextView tvGroupDescribe;
    @Bind(R.id.tv_group_owner)
    TextView tvGroupOwner;
    @Bind(R.id.tv_group_member)
    TextView tvGroupMember;
    @Bind(R.id.fab_action)
    FloatingActionButton fabAction;
    @Bind(R.id.cl_group)
    CoordinatorLayout clGroup;
    @Bind(R.id.rl_group_owner)
    RelativeLayout rlGroupOwner;
    @Bind(R.id.rl_group_member)
    RelativeLayout rlGroupMember;

    private ProgressDialog mProgressDialog;
    private Tb_group mGroup;
    private Tb_user mGroupOwner;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_group_detail);
//        ButterKnife.bind(this);
//
//        JMessageClient.registerEventReceiver(this);
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(tbGroup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "发送中...");

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_group_detail;
    }

    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        mGroup = (Tb_group) intent.getSerializableExtra("group");
        String type = intent.getStringExtra("type");

        getSupportActionBar().setTitle(mGroup.getGroupName());
        setGroupHead(mGroup.getGroupImage());
        //设置群组标签
        String label = mGroup.getLabel();
        tvLabel.setText(label);
        switch (label) {
            case "私人":
                cvLabel.setCardBackgroundColor(Color.parseColor("#ffab00"));
                break;
            case "专业":
                cvLabel.setCardBackgroundColor(Color.parseColor("#0288d1"));
                break;
            case "社团":
                cvLabel.setCardBackgroundColor(Color.parseColor("#ff403c"));
                break;
            default:
                break;
        }
        //设置信息
        tvCreateTime.setText(mGroup.getCreateTime());
        tvGroupDescribe.setText(mGroup.getGroupDescribe());
        setGroupOwner();
        tvGroupMember.setText(mGroup.getJoinNum() + "人");
        //设置悬浮按钮
        setFloatingActionButton(type);
    }

    //设置群组头像
    private void setGroupHead(String url) {
        ImageOptions options = new ImageOptions.Builder()
                .setCircular(true)
                .setFailureDrawableId(R.mipmap.ic_group_head)
                .setLoadingDrawableId(R.mipmap.ic_group_head)
                .build();
        x.image().bind(ivGroupHead, url, options);
    }

    //设置群主信息
    private void setGroupOwner() {
        RequestParams params = new RequestParams(URL + "QueryUser");
        params.setConnectTimeout(8000);
        params.addBodyParameter("username", mGroup.getCreatorUsername());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Gson gson = new Gson();
                    mGroupOwner = gson.fromJson(result, Tb_user.class);
                    tvGroupOwner.setText(mGroupOwner.getRealName());
                } else {

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

            }
        });
    }

    //根据不同用户不同场景设置悬浮按钮样式和功能
    private void setFloatingActionButton(String type) {

        switch (type) {
            case "searchGroup":
                fabAction.setImageResource(R.mipmap.ic_add_white_48dp);
                fabAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //申请加入群组
                        applyJoinGroup();
                    }
                });
                break;
            case "ownerGroup":
                fabAction.setImageResource(R.mipmap.ic_mode_edit_white_48dp);
                fabAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mProgressDialog.setMessage("更新中...");
                        mProgressDialog.show();
                        View dialogView = LayoutInflater.from(GroupDetail.this)
                                .inflate(R.layout.dialog_layout_edit,null);
                        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
                        tvTitle.setText("修改描述");
                        final EditText etEdit = (EditText) dialogView.findViewById(R.id.et_edit);

                        new AlertDialog.Builder(GroupDetail.this).setView(dialogView).setNegativeButton("取消",null).setPositiveButton("修改", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String text = etEdit.getText().toString().trim();
                                if (!text.isEmpty()) {
                                    setGroupDescribe(text);
                                }
                            }
                        }).show();

//                        showSnackBarLong(clGroup, "编辑群组信息");
                    }
                });
                break;
            case "memberGroup":
                fabAction.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    //修改群组信息
    private void setGroupDescribe(String text) {
        RequestParams params = new RequestParams(URL+"Group");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type","setGroup");
        params.addBodyParameter("groupId",mGroup.getGroupId()+"");
        params.addBodyParameter("groupDescribe",text);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(clGroup,"更新成功");
                } else {
                    showSnackBarLong(clGroup,"更新失败");
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

    //申请加入群组
    private void applyJoinGroup() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_apply, null);
        TextView tvTitle = (TextView) dialogView
                .findViewById(R.id.tv_title);
        TextView tvMessage = (TextView) dialogView
                .findViewById(R.id.tv_message);
        final EditText etInput = (EditText) dialogView
                .findViewById(R.id.et_input);

        tvTitle.setText("加入群组");
        tvMessage.setText("你需要发送验证申请，等对方通过");

        new AlertDialog.Builder(this).setView(dialogView)
                .setNegativeButton("不是现在", null)
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String apply = etInput.getText().toString();
                        if (!apply.isEmpty()) {
                            saveApply(makeApplyData(apply));
                        } else {
                            showSnackBarLong(clGroup, "你需要填写验证信息才能发送");
                        }
                    }
                }).show();
    }

    //将申请信息构造成json
    private String makeApplyData(String applyInfo) {
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Gson gson = new Gson();

        //构造信息表
        Tb_message_info tb = new Tb_message_info();
        tb.setReceiverUsername(mGroup.getCreatorUsername());
        tb.setTargetId(mGroup.getGroupId());
        tb.setSender(application.getUser());
        tb.setContent(applyInfo);
        tb.setType("joinGroupApply");
        tb.setTime(time);
        //转换成json
        return gson.toJson(tb);
    }

    //发送申请到服务器去保存
    private void saveApply(String applyData) {
        RequestParams params = new RequestParams(URL + "Message");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "doApply");
        params.addBodyParameter("applyData", applyData);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(clGroup, "发送成功");
                } else {
                    showSnackBarLong(clGroup, "发送失败");
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

    @OnClick({R.id.rl_group_owner, R.id.rl_group_member})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_group_owner:

                break;
            case R.id.rl_group_member:
                Intent intent = new Intent(this,GroupMember.class);
                intent.putExtra("groupId",mGroup.getGroupId()+"");
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_delete_exit_group,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_exit_group:
                //退出群组
                View dialogView = LayoutInflater.from(this)
                        .inflate(R.layout.dialog_layout_tip,null);
                TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
                tvTitle.setText("退出群组");
                tvMessage.setText("你将不再收到群组里的任何信息，确定继续？");

                new AlertDialog.Builder(this)
                        .setView(dialogView)
                        .setNegativeButton("不是现在",null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mProgressDialog.setMessage("正在和群组告别...");
                                mProgressDialog.show();
                                //退出群组
                                JMessageClient.exitGroup(mGroup.getGroupId(),
                                        new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
                                            exitGroup();
                                        }
                                    }
                                });
                            }
                        }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //退出群组
    private void exitGroup() {
        RequestParams params = new RequestParams(URL+"Group");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type","exitGroup");
        params.addBodyParameter("groupId",mGroup.getGroupId()+"");
        params.addBodyParameter("username",application.getMyInfo().getUserName());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    ToastUtil.showToast(GroupDetail.this,"已退出群组", Toast.LENGTH_LONG);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    ToastUtil.showToast(GroupDetail.this,"退出群组失败", Toast.LENGTH_LONG);
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
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }
}
