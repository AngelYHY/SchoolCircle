package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.CommentAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_comment;
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

public class CommentAnswer extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_activity_detail)
    Toolbar tbActivityDetail;
    @Bind(R.id.lv_activity_detail)
    ListView lvActivityDetail;
    @Bind(R.id.srl_detail)
    SwipeRefreshLayout srlDetail;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.rl_input)
    RelativeLayout rlInput;
    @Bind(R.id.rl_comment_answer)
    RelativeLayout rlCommentAnswer;

    private Tb_comment mComment;
    private List<Tb_comment> mCommentList;
    private CommentAdapter mAdapter;

    private String targetName = "";
    private String targetUserName = "";

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlDetail.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlDetail.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_comment_answer);
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
        setSupportActionBar(tbActivityDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlDetail, this);
        srlDetail.setRefreshing(true);

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_comment_answer;
    }

    private void initData() {
        Intent intent = getIntent();
        mComment = (Tb_comment) intent.getSerializableExtra("comment");
        List<Tb_comment> commentList = mComment.getChildComments();
        if (commentList.size() > 0) {
            mCommentList = commentList;
        } else {
            mCommentList = new ArrayList<>();
        }
        mAdapter = new CommentAdapter(this, mCommentList, R.layout.item_comment);
        lvActivityDetail.setAdapter(mAdapter);
        lvActivityDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Tb_comment comment = mCommentList.get(i);
                if (!comment.getUsername().equals(application.getMyInfo().getUserName())) {
                    rlInput.setVisibility(View.VISIBLE);
                    etInput.setHint("回复 "+comment.getRealName()+"的留言");
                    etInput.setFocusable(true);
                    etInput.setFocusableInTouchMode(true);
                    etInput.requestFocus();
                    etInput.requestFocusFromTouch();
                    //强制跳出输入法
                    showSoftInput(etInput);

                    targetName = comment.getRealName();
                    targetUserName = comment.getUsername();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Tb_comment tbComment = makeComment();
                addComment(tbComment);
                etInput.setText("");
                hideSoftInput(etInput);
                rlInput.setVisibility(View.GONE);
            }
        });
        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
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
            tbComment.setTargetUserName(targetUserName);
            tbComment.setUsername(user.getUsername());
            tbComment.setTextContent(textContent);
            tbComment.setCommentType("activity");
            tbComment.setParentId(mComment.getParentId());
            tbComment.setChildId(mComment.get_id());

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
                    showSnackBarLong(rlCommentAnswer, "回复成功");
                } else {
                    showSnackBarLong(rlCommentAnswer, "回复失败，网络貌似出了点问题");
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

    //获取所有二级评论
    private void getAllTwoComment() {
        RequestParams params = new RequestParams(URL + "Comment");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "getAllTwoComment");
        params.addBodyParameter("parentId", mComment.getParentId()+"");
        params.addBodyParameter("childId", mComment.get_id()+"");
        params.addBodyParameter("commentType", "activity");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"nothing".equals(result)) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<Tb_comment>>() {
                    }.getType();
                    List<Tb_comment> data = gson.fromJson(result, type);

                    mCommentList.clear();
                    mCommentList.addAll(data);
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
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS,600);
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

                    getAllTwoComment();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    //刷新数据
    private void refreshData() {
        srlDetail.setRefreshing(true);
        getAllTwoComment();
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
