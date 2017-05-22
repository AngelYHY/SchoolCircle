package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.CommentAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.entity.Tb_comment;
import com.myschool.schoolcircle.entity.Tb_like;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.widget.LoadListView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 活动详情
 */
public class ActivityDetailActivity extends BaseActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_activity_detail)
    Toolbar tbActivityDetail;
    @Bind(R.id.rl_activity_detail)
    RelativeLayout rlActivityDetail;
    @Bind(R.id.lv_activity_detail)
    LoadListView lvActivityDetail;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.srl_detail)
    SwipeRefreshLayout srlDetail;
    @Bind(R.id.pv_activity)
    PhotoView pvActivity;
    @Bind(R.id.rl_input)
    RelativeLayout rlInput;
    @Bind(R.id.fab_send)
    FloatingActionButton fabSend;

    TextView tvTheme;
    TextView tvTime;
    Button btnJoin;
    ImageView ivAvatar;
    TextView tvSponsorContent;
    TextView tvBegin;
    TextView tvEnd;
    TextView tvPlaceContent;
    ImageView ivActivityPicture;
    TextView tvDescribe;
    CheckBox cbComment;
    CheckBox cbLike;
    CheckBox cbJoin;
    TextView tvState;
    CardView cvState;

    private ProgressDialog mDialog;
    private ContentLoadingProgressBar clp_loading;
    private TextView tvLoading;
    private View detailHead;
    private ImageOptions options;

    private Gson gson = new Gson();
    public Tb_activity mActivity;
    private List<Tb_comment> mComment;
    private CommentAdapter mAdapter;
    private int childId = 0;
    private String targetName = "";
    private static boolean isLikeChanged = false;
    private int start = 0;
    private boolean isLoading = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int i = (int) msg.obj;
                    lvActivityDetail.smoothScrollToPosition(i);
                    break;
                case HandlerKey.TRUE:
                    setButtonNoFocusedStyle("已报名");
                    break;
                case HandlerKey.FALSE:

                    break;
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
//        setContentView(R.layout.layout_activity_activity_detail);
//        ButterKnife.bind(this);
//
//        initData();
//        initView();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        mActivity = (Tb_activity) intent.getSerializableExtra("activityData");

        mComment = new ArrayList<>();
        mAdapter = new CommentAdapter(this, mComment, R.layout.item_comment);

        detailHead = LayoutInflater.from(this)
                .inflate(R.layout.layout_head_activity_detail, lvActivityDetail, false);
        lvActivityDetail.addHeaderView(detailHead);
        lvActivityDetail.setAdapter(mAdapter);

        options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE).build();
    }

    //初始化界面
    protected void initView() {
        initData();

        setSupportActionBar(tbActivityDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ivActivityPicture = (ImageView) detailHead.findViewById(R.id.iv_activity_picture);
        btnJoin = (Button) detailHead.findViewById(R.id.btn_join);
        tvTheme = (TextView) detailHead.findViewById(R.id.tv_theme);
        tvTime = (TextView) detailHead.findViewById(R.id.tv_time);
        ivAvatar = (ImageView) detailHead.findViewById(R.id.iv_avatar);
        tvSponsorContent = (TextView) detailHead.findViewById(R.id.tv_sponsor_content);
        tvBegin = (TextView) detailHead.findViewById(R.id.tv_begin_content);
        tvEnd = (TextView) detailHead.findViewById(R.id.tv_end_content);
        tvPlaceContent = (TextView) detailHead.findViewById(R.id.tv_place_content);
        tvDescribe = (TextView) detailHead.findViewById(R.id.tv_describe);
        cbComment = (CheckBox) detailHead.findViewById(R.id.cb_comment);
        cbJoin = (CheckBox) detailHead.findViewById(R.id.cb_join);
        cbLike = (CheckBox) detailHead.findViewById(R.id.cb_like);
        cvState = (CardView) detailHead.findViewById(R.id.cv_state);
        tvState = (TextView) detailHead.findViewById(R.id.tv_state);

        isJoin();
        isLike();
        btnJoin.setOnClickListener(this);
        fabSend.setOnClickListener(this);
        setImage(mActivity.getPicture(), mActivity.getAvatar());
        tvTheme.setText(mActivity.getTheme());
        tvTime.setText(mActivity.getsDatetime());
        tvSponsorContent.setText(mActivity.getSponsor());
        tvBegin.setText(mActivity.getActivityBegin().substring(5, 16));
        tvEnd.setText(mActivity.getActivityEnd().substring(5, 16));
        tvPlaceContent.setText(mActivity.getPlace());
        tvDescribe.setText(mActivity.getActivityDescribe());
        cbJoin.setText(mActivity.getJoinNum() + "");
        cbLike.setText(mActivity.getLikeNum() + "");

        String state = mActivity.getState();
        tvState.setText(state);
        switch (state) {
            case "未开始":
                if (mActivity.getJoinNum() >= mActivity.getNumber()) {
                    setButtonNoFocusedStyle("已满");
                }
                cvState.setCardBackgroundColor(Color.parseColor("#ffab00"));
                break;
            case "进行中":
                setButtonNoFocusedStyle("报名");
                cvState.setCardBackgroundColor(Color.parseColor("#ff403c"));
                break;
            case "已结束":
                setButtonNoFocusedStyle("报名");
                cvState.setCardBackgroundColor(Color.parseColor("#cdcdcd"));
                break;
            default:
                break;
        }

        if (mActivity.getUsername().equals(application.getMyInfo().getUserName())) {
            setButtonNoFocusedStyle("报名");
        }

        ivActivityPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String image = mActivity.getPicture();
                pvActivity.setVisibility(View.VISIBLE);
                x.image().bind(pvActivity, image, options);
            }
        });

        pvActivity.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                pvActivity.setVisibility(View.GONE);
            }
        });

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    fabSend.show();
                } else {
                    fabSend.hide();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cbComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childId = 0;
                targetName = "";
                etInput.setHint("说点什么...");
                showSoftInput(etInput);
            }
        });

        cbLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点赞，取消点赞
                Tb_like tbLike = new Tb_like();
                tbLike.setTargetId(Integer.parseInt(mActivity.getId()));
                tbLike.setUsername(application.getUser().getUsername());
                int num = Integer.parseInt(cbLike.getText().toString());
                if (cbLike.isChecked()) {
                    like(tbLike, "like");
                    cbLike.setText((num + 1) + "");
                } else {
                    cbLike.setText((num - 1) + "");
                    like(tbLike, "unLike");
                }
                isLikeChanged = true;
            }
        });

        lvActivityDetail.setMyPullUpListViewCallBack(
                new LoadListView.MyPullUpListViewCallBack() {
                    @Override
                    public void scrollBottomState() {
                        if (!srlDetail.isRefreshing()) {
                            isLoading = true;
                            getAllOneComment();
                        }
                    }
                });

        //点击评论
        lvActivityDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if ((i - 1) >= lvActivityDetail.getLastVisiblePosition()) {
                    return;
                }
                //跳出对话框
                final Tb_comment comment = mComment.get(i - 1);

                View dialogView = LayoutInflater
                        .from(ActivityDetailActivity.this)
                        .inflate(R.layout.dialog_commet, null);

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
                        .Builder(ActivityDetailActivity.this)
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
                        etInput.setHint("回复 " + comment.getRealName() + "的留言");
                        childId = comment.get_id();
                        targetName = comment.getRealName();
                        dialog.cancel();

                        etInput.setFocusable(true);
                        etInput.setFocusableInTouchMode(true);
                        etInput.requestFocus();
                        etInput.requestFocusFromTouch();
                        //强制跳出输入法
                        showSoftInput(etInput);
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = i;
                        mHandler.sendMessageDelayed(msg, 100);
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

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("报名中...");
        mDialog.setCanceledOnTouchOutside(false);

        RefreshUtil.initRefreshView(srlDetail, this);
        refreshData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_activity_detail;
    }

    //跳转到查看评论回复界面
    public void intent2Comment(Tb_comment comment) {
        Intent intent = new Intent(
                ActivityDetailActivity.this,
                CommentAnswer.class);
        intent.putExtra("comment", comment);
        startActivity(intent);
    }

    //报名按钮
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_join:
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("报名活动需要提交个人信息，确定报名该活动吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                joinActivity();
                            }
                        }).show();
                break;
            case R.id.fab_send:
                //发表评论
                Tb_comment tbComment = makeComment();
                if (tbComment != null) {
                    addComment(tbComment);
                    etInput.setText("");
                    hideSoftInput(etInput);
                } else {
                    showSnackBarLong(rlActivityDetail, "需要输入内容才能留言");
                }
                break;
            default:
                break;
        }
    }

    //设置图片
    public void setImage(String mImage, String avatar) {
        ImageOptions options = new ImageOptions.Builder().build();
        x.image().bind(ivActivityPicture, mImage, options);
        if (!avatar.isEmpty()) {
            x.image().bind(ivAvatar, avatar,
                    new ImageOptions.Builder().setCircular(true).build());
        }
    }

    //参加活动
    public void joinActivity() {
        mDialog.show();
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "joinActivity");
        params.addBodyParameter("activity_id", mActivity.getId());
        params.addBodyParameter("username", application.getUser().getUsername());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    setButtonNoFocusedStyle("已报名");
                    showSnackBarLong(rlActivityDetail, "报名成功");
                } else if ("out_of".equals(result)) {
                    setButtonNoFocusedStyle("已满");
                    showSnackBarLong(rlActivityDetail, "报名人数已满");
                } else {
                    showSnackBarLong(rlActivityDetail, "报名失败，网络貌似出了点问题");
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
                mDialog.cancel();
            }
        });
    }

    //判断是否已参加该活动
    public void isJoin() {
        btnJoin.setVisibility(View.INVISIBLE);
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "isJoin");
        params.addBodyParameter("username", application.getMyInfo().getUserName());
        params.addBodyParameter("activity_id", mActivity.getId());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"false".equals(result)) {
                    mHandler.sendEmptyMessage(HandlerKey.TRUE);
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
                btnJoin.setVisibility(View.VISIBLE);
            }
        });
    }

    //点赞，取消点赞
    private void like(Tb_like tbLike, String type) {
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        if (type.equals("like")) {
            params.addBodyParameter("type", "like");
        } else {
            params.addBodyParameter("type", "unLike");
        }
        Gson gson = new Gson();
        String data = gson.toJson(tbLike);
        params.addBodyParameter("data", data);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
//                    showSnackBarShort(rlActivityDetail,"成功");
                } else {
//                    showSnackBarShort(rlActivityDetail,"失败");
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

    //判断是否点赞
    private void isLike() {
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "isLike");
        Gson gson = new Gson();
        Tb_like tbLike = new Tb_like();
        tbLike.setTargetId(Integer.parseInt(mActivity.getId()));
        tbLike.setUsername(application.getUser().getUsername());
        tbLike.setTargetType("activity");
        String data = gson.toJson(tbLike);
        params.addBodyParameter("data", data);

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                if ("true".equals(result)) {
                    cbLike.setChecked(true);
                } else {
                    cbLike.setChecked(false);
                }
                return !isLikeChanged;
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    if ("true".equals(result)) {
                        cbLike.setChecked(true);
                    } else {
                        cbLike.setChecked(false);
                    }
                }
                isLikeChanged = false;
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

    //设置按钮失去焦点
    public void setButtonNoFocusedStyle(String text) {
        btnJoin.setClickable(false);
        btnJoin.setText(text);
        btnJoin.setTextColor(Color.parseColor("#898989"));
        btnJoin.setBackgroundResource(R.drawable.no_focused_button_shape);
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
                    showSnackBarLong(rlActivityDetail, "留言成功");
                } else {
                    showSnackBarLong(rlActivityDetail, "留言失败，网络貌似除了点问题");
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
        params.addBodyParameter("start", start + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                loadData(result);
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

    //加载数据
    private void loadData(String result) {
        if (!"nothing".equals(result)) {
            Type type = new TypeToken<List<Tb_comment>>() {
            }.getType();
            List<Tb_comment> data = gson.fromJson(result, type);

            if (!isLoading) {
                //如果不是加载就是刷新，要先清空数据
                mComment.clear();
                mComment.addAll(data);
                mAdapter.notifyDataSetChanged();
            } else {
                mComment.addAll(data);
                mAdapter.notifyDataSetChanged();
                isLoading = false;
            }
            start = mComment.size();

        } else {
            if (!isLoading) {
                cbComment.setText("0");
                mComment.clear();
                mAdapter.notifyDataSetChanged();
            } else {
                isLoading = false;
            }
            lvActivityDetail.loadDone();
        }
    }

    //评论
    private void deleteComment(int id) {
        RequestParams params = new RequestParams(URL + "Comment");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteComment");
        params.addBodyParameter("id", id + "");
        params.addBodyParameter("parentId", mActivity.getId());
        params.addBodyParameter("commentType", "activity");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    refreshData();
                    showSnackBarLong(rlActivityDetail, "删除成功");
                } else {
                    showSnackBarLong(rlActivityDetail, "删除失败");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                try {

                    refreshData();

                } catch (Exception e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 600);
                }
            }
        }.start();
    }

    //刷新数据
    private void refreshData() {
        start = 0;
        srlDetail.setRefreshing(true);
        getAllOneComment();
        getActivityData();
    }

    //获取活动数据
    private void getActivityData() {
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "getActivity");
        params.addBodyParameter("activityId", mActivity.getId());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"nothing".equals(result)) {
                    Tb_activity activity = gson.fromJson(result, Tb_activity.class);
                    cbComment.setText(activity.getCommentNum() + "");
                    cbJoin.setText(activity.getJoinNum() + "");
                    cbLike.setText(activity.getLikeNum() + "");

                    String state = activity.getState();
                    tvState.setText(state);
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
}
