package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.myschool.schoolcircle.adapter.CommentAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_comment;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.entity.Tb_like;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.ToastUtil;
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

public class DetailsItemActivity extends BaseActivity
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_dynamic_detail)
    Toolbar tbDynamicDetail;
    @Bind(R.id.lv_dynamic_detail)
    LoadListView lvDynamicDetail;
    @Bind(R.id.srl_detail_dy)
    SwipeRefreshLayout srlDetailDy;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.rl_input_dy)
    RelativeLayout rlInputDy;
    @Bind(R.id.fab_send)
    FloatingActionButton fabSend;
    @Bind(R.id.cl_dynamic_detail)
    CoordinatorLayout clDynamicDetail;
    @Bind(R.id.v_empty)
    View vEmpty;

    private ProgressDialog mProgressDialog;
    private View detailHead;
    public Tb_dynamic mdynamic;
    private String targetName = "";
    private List<Tb_comment> mComment;
    private Gson gson = new Gson();
    private CommentAdapter mAdapter;
    private int childId = 0;
    private static boolean isLikeChanged = false;
    private boolean isLoading = false;
    private int start = 0;
    private String fabType = "comment";
    private int commentNum = 0;

    ImageView imageHead;
    TextView tvRealName;
    TextView tvContent;
    NineGridView nineGridView;
    TextView tvTime;
    CheckBox cbDynamicComment;
    CheckBox cbRetransmission;
    CheckBox cbDynamicLike;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int i = (int) msg.obj;
                    lvDynamicDetail.smoothScrollToPosition(i);
                    break;
                case HandlerKey.TRUE:

                    break;
                case HandlerKey.FALSE:
                    showSnackBarLong(clDynamicDetail, "网络可能出了点问题，刷新失败");
                    break;
                case HandlerKey.REFRESH_SUCCESS:
                    srlDetailDy.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlDetailDy.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.details_dynamic_listviews);
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
        mdynamic = (Tb_dynamic) intent.getSerializableExtra("dynamicData");

        mComment = new ArrayList<>();
        mAdapter = new CommentAdapter(this, mComment, R.layout.item_comment);
        detailHead = LayoutInflater.from(this)
                .inflate(R.layout.details_dynamic_listview_header, lvDynamicDetail, false);
        lvDynamicDetail.addHeaderView(detailHead);
        lvDynamicDetail.setAdapter(mAdapter);
    }

    protected void initView() {
        initData();

        setSupportActionBar(tbDynamicDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "转发中...");

        imageHead = (ImageView) detailHead.findViewById(R.id.iv_head_dy);
        tvRealName = (TextView) detailHead.findViewById(R.id.tv_real_name_dy);
        tvContent = (TextView) detailHead.findViewById(R.id.tv_text_content_dy);
        nineGridView = (NineGridView) detailHead.findViewById(R.id.ngv_dynamic);
        tvTime = (TextView) detailHead.findViewById(R.id.tv_time_dy);
        cbDynamicComment = (CheckBox) detailHead.findViewById(R.id.cb_comment_dy);
        cbRetransmission = (CheckBox) detailHead.findViewById(R.id.cb_retransmission);
        cbDynamicLike = (CheckBox) detailHead.findViewById(R.id.cb_like_dy);

        fabSend.setOnClickListener(this);
        isLike();
        setAvatar(mdynamic.getAvatar());
        setImageList();
        tvRealName.setText(mdynamic.getRealName());
        tvContent.setText(mdynamic.getTextContent());
        tvTime.setText(mdynamic.getDatetime());
        cbDynamicLike.setText(mdynamic.getLikeNum() + "");
        commentNum = mdynamic.getCommentNum();
        cbDynamicComment.setText(commentNum + "");

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

        cbDynamicComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                childId = 0;
                targetName = "";
                etInput.setHint("说点什么...");
                showSoftInput(etInput);

            }


        });

        lvDynamicDetail.setMyPullUpListViewCallBack(new LoadListView.MyPullUpListViewCallBack() {
            @Override
            public void scrollBottomState() {
                isLoading = true;
                getAllOneComment();
            }
        });

        cbDynamicLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点赞，取消点赞
                Tb_like tbLike = new Tb_like();
                tbLike.setTargetId(Integer.parseInt(Integer.toString(mdynamic.get_id())));
                tbLike.setUsername(application.getUser().getUsername());
                int num = Integer.parseInt(cbDynamicLike.getText().toString());
                if (cbDynamicLike.isChecked()) {
                    like(tbLike, "like");
                    cbDynamicLike.setText((num + 1) + "");
                } else {
                    cbDynamicLike.setText((num - 1) + "");
                    like(tbLike, "unLike");
                }
                isLikeChanged = true;
            }
        });

        cbRetransmission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabType = "retransmission";
                etInput.setHint("[转发]说点什么...");
                vEmpty.setVisibility(View.VISIBLE);
                showSoftInput(etInput);
            }
        });

        //点击空白，隐藏输入法
        vEmpty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftInput(etInput);
                fabType = "comment";
                vEmpty.setVisibility(View.GONE);
                etInput.setText("");
                etInput.setHint("说点什么...");
                fabSend.hide();
            }
        });

        //点击评论
        lvDynamicDetail.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                if ((i - 1) >= lvDynamicDetail.getLastVisiblePosition()) {
                    return;
                }
                //跳出对话框
                final Tb_comment comment = mComment.get(i - 1);

                View dialogView = LayoutInflater
                        .from(DetailsItemActivity.this)
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
                        .Builder(DetailsItemActivity.this)
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

        RefreshUtil.initRefreshView(srlDetailDy, this);
        refreshData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.details_dynamic_listviews;
    }

    //点赞，取消点赞
    private void like(Tb_like tbLike, String type) {
        RequestParams params = new RequestParams(URL + "Dynamic");
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
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "isLike");
        Gson gson = new Gson();
        Tb_like tbLike = new Tb_like();
        tbLike.setTargetId(Integer.parseInt(Integer.toString(mdynamic.get_id())));
        tbLike.setUsername(application.getUser().getUsername());
        tbLike.setTargetType("dynamic");
        String data = gson.toJson(tbLike);
        params.addBodyParameter("data", data);

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                if ("true".equals(result)) {
                    cbDynamicLike.setChecked(true);
                } else {
                    cbDynamicLike.setChecked(false);
                }
                return !isLikeChanged;
            }

            @Override
            public void onSuccess(String result) {
                if (result != null) {
                    if ("true".equals(result)) {
                        cbDynamicLike.setChecked(true);
                    } else {
                        cbDynamicLike.setChecked(false);
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

    //跳转到查看评论回复界面
    public void intent2Comment(Tb_comment comment) {
        Intent intent = new Intent(
                DetailsItemActivity.this,
                CommentAnswer.class);
        intent.putExtra("comment", comment);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_send:
                switch (fabType) {
                    case "comment":
                        //留言
                        Tb_comment tbComment = makeComment();
                        if (tbComment != null) {
                            addComment(tbComment);
                            etInput.setText("");
                            hideSoftInput(etInput);
                        } else {
                            showSnackBarLong(rlInputDy, "需要输入内容才能留言");
                        }
                        break;
                    case "retransmission":
                        //转发
                        mProgressDialog.show();
                        retransmissionDynamic(etInput.getText().toString());
                        etInput.setText("");
                        fabType = "comment";
                        vEmpty.setVisibility(View.GONE);
                        etInput.setHint("说点什么...");
                        hideSoftInput(etInput);
                        break;
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
            tbComment.setCommentType("dynamic");
            tbComment.setParentId(Integer.parseInt(Integer.toString(mdynamic.get_id())));
            tbComment.setChildId(childId);

            return tbComment;
        }

        return null;
    }

    private void setAvatar(String avatar) {
        ImageOptions options = new ImageOptions.Builder()
                .setCircular(true).build();
        x.image().bind(imageHead, avatar, options);
    }

    private void setImageList() {
        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        List<String> images = mdynamic.getImages();
        if (images != null) {
            if (images.size() > 0) {
                for (String image : images) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(image);
                    info.setBigImageUrl(image);
                    imageInfo.add(info);
                }
            }
        }

        nineGridView.setAdapter(new NineGridViewClickAdapter(this, imageInfo));
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
                    commentNum++;
                    cbDynamicComment.setText(commentNum + "");
                    showSnackBarLong(rlInputDy, "留言成功");
                } else {
                    showSnackBarLong(rlInputDy, "留言失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showSnackBarLong(rlInputDy, "网络貌似出了点问题，留言失败");
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
        params.addBodyParameter("parentId", Integer.toString(mdynamic.get_id()));
        params.addBodyParameter("commentType", "dynamic");
        params.addBodyParameter("start", start + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
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
                        cbDynamicComment.setText("0");
                        mComment.clear();
                        mAdapter.notifyDataSetChanged();
                    } else {
                        isLoading = false;
                    }
                    lvDynamicDetail.loadDone();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showSnackBarLong(clDynamicDetail,"网络貌似出了点问题，刷新失败");
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

    //删除评论
    private void deleteComment(int id) {
        RequestParams params = new RequestParams(URL + "Comment");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteComment");
        params.addBodyParameter("id", id + "");
        params.addBodyParameter("parentId", Integer.toString(mdynamic.get_id()));
        params.addBodyParameter("commentType", "dynamic");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    refreshData();
                    commentNum--;
                    cbDynamicComment.setText(commentNum + "");
                    showSnackBarLong(clDynamicDetail, "删除成功");
                } else {
                    showSnackBarLong(clDynamicDetail, "删除失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (isOnCallback) {
                    showSnackBarLong(clDynamicDetail, "网络貌似出了点问题，删除留言失败");
                }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mdynamic.getUsername().equals(application.getMyInfo().getUserName())) {
            getMenuInflater().inflate(R.menu.menu_dynamic_publish_detail, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_dynamic_publish_delete:
                View dialogView = LayoutInflater.from(this)
                        .inflate(R.layout.dialog_layout_tip, null);
                TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
                tvTitle.setText("删除动态");
                tvMessage.setText("同时会将这条动态下的评论一起删除，确定继续？");
                new AlertDialog.Builder(this).setView(dialogView)
                        .setNegativeButton("不是现在", null)
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteDynamic();
                                    }
                                }).show();

                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //转发动态
    private void retransmissionDynamic(String text) {
        mProgressDialog.show();
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "retransmissionDynamic");
        params.addBodyParameter("username", application.getMyInfo().getUserName());
        params.addBodyParameter("retransmission", text);
        params.addBodyParameter("dynamicId", mdynamic.get_id() + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(clDynamicDetail, "转发成功");
                } else {
                    showSnackBarLong(clDynamicDetail, "转发失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showSnackBarLong(clDynamicDetail, "网络貌似出了点问题，转发失败");
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

    //删除动态
    private void deleteDynamic() {
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteDynamic");
        params.addBodyParameter("dynamicId", mdynamic.get_id() + "");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    ToastUtil.showToast(DetailsItemActivity.this, "删除成功", Toast.LENGTH_SHORT);
                    finish();
                } else {
                    showSnackBarLong(clDynamicDetail, "删除失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showSnackBarLong(clDynamicDetail, "网络貌似出了点问题，删除失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
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
        if (!srlDetailDy.isRefreshing()) {
            srlDetailDy.setRefreshing(true);
        }
        getAllOneComment();
    }
}
