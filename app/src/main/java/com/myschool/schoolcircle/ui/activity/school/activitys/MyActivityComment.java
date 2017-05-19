package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.CommentAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.entity.Tb_comment;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.widget.LoadListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class MyActivityComment extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.tb_activity_detail)
    Toolbar tbActivityDetail;
    @Bind(R.id.lv_comment)
    LoadListView lvComment;
    @Bind(R.id.srl_comment)
    SwipeRefreshLayout srlComment;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.rl_input)
    RelativeLayout rlInput;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;

    private Tb_activity mActivity;
    private List<Tb_comment> mCommentList;
    private List<String> mParticipators;
    private CommentAdapter mAdapter;

    private int childId = 0;
    private String targetName = "";

    private int start = 0;
    private boolean isLoading = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlComment.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlComment.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_comment);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event,this);
    }

    protected void initView() {
        setSupportActionBar(tbActivityDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlComment, this);
        srlComment.setRefreshing(true);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_comment;
    }

    private void initData() {
        Intent intent = getIntent();
        mActivity = (Tb_activity) intent.getSerializableExtra("activity");

        mCommentList = new ArrayList<>();
        mAdapter = new CommentAdapter(this,mCommentList,R.layout.item_comment);
        lvComment.setAdapter(mAdapter);

        //获取所有一级评论
        getAllOneComment();

        //点击评论
        lvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, final int i, long l) {
                //跳出对话框
                final Tb_comment comment = mCommentList.get(i);

                View dialogView = LayoutInflater
                        .from(MyActivityComment.this)
                        .inflate(R.layout.dialog_commet,null);

                TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
                TextView tvAnswer = (TextView) dialogView.findViewById(R.id.tv_answer);
                TextView tvLook = (TextView) dialogView.findViewById(R.id.tv_look);
                TextView tvDelete = (TextView) dialogView.findViewById(R.id.tv_delete);

                if (comment.getUsername().equals(application.getMyInfo().getUserName())) {
                    //如果点击的是自己的评论，则去掉回复一栏
                    tvAnswer.setVisibility(View.GONE);
                } else {
                    //如果点击的是别人的评论，则去掉删除一栏
                    tvDelete.setVisibility(View.GONE);
                }

                final AlertDialog dialog = new AlertDialog
                        .Builder(MyActivityComment.this)
                        .setView(dialogView).create();

                tvTitle.setText("请选择");

                //查看
                tvLook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        intent2Comment(comment);
                        dialog.cancel();
                    }
                });

                //回复
                tvAnswer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        etInput.setHint("回复 "+comment.getRealName()+"的评论");
                        childId = comment.get_id();
                        targetName = comment.getRealName();
                        dialog.cancel();

                        etInput.setFocusable(true);
                        etInput.setFocusableInTouchMode(true);
                        etInput.requestFocus();
                        etInput.requestFocusFromTouch();
                        //强制跳出输入法
                        showSoftInput(etInput);
                    }
                });

                //删除
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteComment(comment.get_id());
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    //跳转到查看评论回复界面
    public void intent2Comment(Tb_comment comment) {
        Intent intent = new Intent(this,
                CommentAnswer.class);
        intent.putExtra("comment",comment);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                //发表评论
                Tb_comment tbComment = makeComment();
                if (tbComment != null) {
                    addComment(tbComment);
                    etInput.setText("");
                    hideSoftInput(etInput);
                } else {
                    showSnackBarLong(rlComment, "需要输入内容才能评论");
                }
                break;
            default:
                break;
        }
    }

    //构造评论表
    private Tb_comment makeComment() {
        String textContent = etInput.getText().toString();
        if (!textContent.isEmpty()) {

            Tb_comment tbComment = new Tb_comment();
            Tb_user user = application.getUser();
            tbComment.setAvatar(user.getHeadView());
            tbComment.setRealName(user.getRealName());
            tbComment.setTargetName(targetName);
            tbComment.setUsername(user.getUsername());
            tbComment.setTextContent(textContent);
            tbComment.setCommentType("activity");
            tbComment.setParentId(Integer.parseInt(mActivity.getId()));
            tbComment.setChildId(childId);


            return tbComment;
        }

        return null;
    }

    //添加评论
    private void addComment(Tb_comment tb) {
        Gson gson = new Gson();
        RequestParams params = new RequestParams(URL + "Comment");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "addComment");
        String data = gson.toJson(tb);
        params.addBodyParameter("data", data);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    refreshData();
                    showSnackBarLong(rlComment, "评论成功");
                } else {
                    showSnackBarLong(rlComment, "评论失败");
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

    //获取所有评论
    private void getAllOneComment() {
        RequestParams params = new RequestParams(URL + "Comment");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "getAllOneComment");
        params.addBodyParameter("parentId", mActivity.getId());
        params.addBodyParameter("commentType", "activity");
        params.addBodyParameter("start",start+"");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"nothing".equals(result)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Tb_comment>>() {
                    }.getType();
                    List<Tb_comment> data = gson.fromJson(result, type);

                    if (!isLoading) {
                        mCommentList.clear();
                        mCommentList.addAll(data);
                    } else {
                        mCommentList.addAll(data);
                        isLoading = false;
                    }
                    mAdapter.notifyDataSetChanged();

                } else {
                    if (isLoading) {
                        isLoading = false;
                    }
                    lvComment.loadDone();
                    showSnackBarLong(rlComment,"暂无任何留言");
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
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS,600);
            }
        });
    }

    //评论
    private void deleteComment(int id) {
        RequestParams params = new RequestParams(URL + "Comment");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteComment");
        params.addBodyParameter("id", id+"");
        params.addBodyParameter("parentId",mActivity.getId());
        params.addBodyParameter("commentType", "activity");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    refreshData();
                    showSnackBarLong(rlComment,"删除成功");
                } else {
                    showSnackBarLong(rlComment,"删除失败");
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

    //刷新
    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {

                try {

                    getAllOneComment();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //刷新数据
    private void refreshData() {
        start = 0;
        srlComment.setRefreshing(true);
        getAllOneComment();
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

}
