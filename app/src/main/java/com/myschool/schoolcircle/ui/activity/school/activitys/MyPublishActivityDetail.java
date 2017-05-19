package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class MyPublishActivityDetail extends BaseActivity {

    @Bind(R.id.tb_activity_detail)
    Toolbar tbActivityDetail;
    @Bind(R.id.tv_theme)
    TextView tvTheme;
    @Bind(R.id.tv_time)
    TextView tvTime;
    @Bind(R.id.tv_sponsor_content)
    TextView tvSponsorContent;
    @Bind(R.id.tv_place_content)
    TextView tvPlaceContent;
    @Bind(R.id.iv_activity_picture)
    ImageView ivActivityPicture;
    @Bind(R.id.tv_describe)
    TextView tvDescribe;
    @Bind(R.id.ll_my_publish_activity_detail)
    LinearLayout llMyPublishActivityDetail;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.rl_participator)
    RelativeLayout rlParticipator;
    @Bind(R.id.tv_comment_num)
    TextView tvCommentNum;
    @Bind(R.id.tv_participator_num)
    TextView tvParticipatorNum;
    @Bind(R.id.cb_like)
    CheckBox cbLike;
    @Bind(R.id.tv_state)
    TextView tvState;
    @Bind(R.id.cv_state)
    CardView cvState;
    @Bind(R.id.tv_begin_content)
    TextView tvBeginContent;
    @Bind(R.id.tv_end_content)
    TextView tvEndContent;

    private Tb_activity mActivity;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_my_publish_activity_detail);
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
    }

    //初始化界面
    protected void initView() {
        initData();

        setSupportActionBar(tbActivityDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setImage(mActivity.getPicture());
        tvTheme.setText(mActivity.getTheme());
        tvTime.setText(mActivity.getsDatetime());
        tvSponsorContent.setText(mActivity.getSponsor());
        tvBeginContent.setText(mActivity.getActivityBegin().substring(5,16));
        tvEndContent.setText( mActivity.getActivityEnd().substring(5,16));
        tvPlaceContent.setText(mActivity.getPlace());
        tvDescribe.setText(mActivity.getActivityDescribe());
        cbLike.setText(mActivity.getLikeNum() + "");
//        tvCommentNum.setText("0");//评论次数
        tvParticipatorNum.setText(mActivity.getJoinNum() + "");
        getActivityData();

        String state = mActivity.getState();
        tvState.setText(state);
        switch (state) {
            case "未开始":
                cvState.setCardBackgroundColor(Color.parseColor("#ffab00"));
                break;
            case "进行中":
                cvState.setCardBackgroundColor(Color.parseColor("#ff403c"));
                break;
            case "已结束":
                cvState.setCardBackgroundColor(Color.parseColor("#cdcdcd"));
                break;
            default:
                break;
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_my_publish_activity_detail;
    }

    //设置图片
    private void setImage(String mImage) {
        ImageOptions options = new ImageOptions.Builder().build();
        x.image().bind(ivActivityPicture, mImage, options);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish_detail, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            case R.id.menu_activity_publish_delete:
                //删除活动
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定删除这个活动吗？")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteActivity();
                            }
                        }).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //删除活动
    private void deleteActivity() {
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "deleteActivity");
        params.addBodyParameter("activity_id", mActivity.getId());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    setResult(RESULT_OK,new Intent());
                    finish();
                    ToastUtil.showToast(MyPublishActivityDetail.this,
                            "删除活动成功", Toast.LENGTH_LONG);
                } else {
                    ToastUtil.showToast(MyPublishActivityDetail.this,
                            "删除活动失败", Toast.LENGTH_LONG);
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

    //获取活动数据
    private void getActivityData() {
        RequestParams params = new RequestParams(URL+"Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type","getActivity");
        params.addBodyParameter("activityId",mActivity.getId());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"nothing".equals(result)) {
                    Gson gson = new Gson();
                    Tb_activity activity = gson.fromJson(result,Tb_activity.class);
                    tvCommentNum.setText(activity.getCommentNum() + "");
                    cbLike.setText(activity.getLikeNum() + "");
//                    cbJoin.setText(activity.getJoinNum() + "");

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

    @OnClick({R.id.rl_comment, R.id.rl_participator})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_comment:
                Intent intent1 = new Intent(this, MyActivityComment.class);
                intent1.putExtra("activity", mActivity);
                startActivity(intent1);
                break;
            case R.id.rl_participator:
                Intent intent2 = new Intent(this, ParticipatorActivity.class);
                intent2.putExtra("activity_id", mActivity.getId());
                intent2.putExtra("activityTheme",mActivity.getTheme());
                startActivity(intent2);
                break;
            default:
                break;
        }
    }
}
