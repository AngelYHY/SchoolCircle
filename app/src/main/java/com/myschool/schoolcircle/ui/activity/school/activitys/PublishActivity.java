package com.myschool.schoolcircle.ui.activity.school.activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.mine.ImageActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.QiniuCloudUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class PublishActivity extends BaseActivity {

    @Bind(R.id.tb_publish_activity)
    Toolbar tbPublishActivity;
    @Bind(R.id.et_activity_theme)
    EditText etActivityTheme;
    @Bind(R.id.et_activity_describe)
    EditText etActivityDescribe;
    @Bind(R.id.et_activity_number)
    EditText etActivityNumber;
    @Bind(R.id.et_activity_place)
    EditText etActivityPlace;
    @Bind(R.id.ll_activity_publish)
    RelativeLayout llActivityPublish;
    @Bind(R.id.iv_activity_picture)
    ImageView ivActivityPicture;
    @Bind(R.id.tv_date_begin)
    TextView tvDateBegin;
    @Bind(R.id.tv_time_begin)
    TextView tvTimeBegin;
    @Bind(R.id.tv_date_end)
    TextView tvDateEnd;
    @Bind(R.id.tv_time_end)
    TextView tvTimeEnd;
    @Bind(R.id.tv_num)
    TextView tvNum;

    private ProgressDialog mDialog;
    private MenuItem item;
    private Tb_user mUser;
    private String theme;
    private String describe;
    private String imageUrl = "";
    private String number;
    private String place;
    private String begin;
    private String end;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPLOAD_SUCCESS:
                    //图片上传成功
                    imageUrl = (String) msg.obj;
                    Tb_activity activity = makeActivityInfo();
                    //将信息提交到数据库
                    post2Server(activity);
                    break;
                case HandlerKey.UPLOAD_FAIL:
                    //图片上传失败
                    showSnackBarShort(llActivityPublish, R.string.publish_activity_fail);
                    mDialog.cancel();
                    item.setCheckable(true);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_publish);
//        ButterKnife.bind(this);
//
//        initView();
//    }

    //初始化控件
    protected void initView() {
        setSupportActionBar(tbPublishActivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("发布中...");
        mDialog.setCanceledOnTouchOutside(false);

        mUser = application.getUser();

        etActivityDescribe.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvNum.setText(charSequence.length()+"/75字");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_publish;
    }

    @OnClick({R.id.tv_date_begin, R.id.tv_time_begin, R.id.tv_date_end,
            R.id.tv_time_end, R.id.iv_activity_picture})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_activity_picture:
                Intent intent = new Intent(this, ImageActivity.class);
                startActivityForResult(intent, 1003);
                break;
            case R.id.tv_date_begin:
                showDatePicker(tvDateBegin);
                break;
            case R.id.tv_time_begin:
                showTimePicker(tvTimeBegin);
                break;
            case R.id.tv_date_end:
                showDatePicker(tvDateEnd);
                break;
            case R.id.tv_time_end:
                showTimePicker(tvTimeEnd);
                break;
        }
    }

    //显示日期选择器
    private void showDatePicker(final TextView tvDate) {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_layout_date, null);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatePicker datePicker = (DatePicker) dialogView
                                .findViewById(R.id.dp_date);
                        String date = datePicker.getYear() + "-" +
                                (datePicker.getMonth() + 1) + "-" +
                                datePicker.getDayOfMonth();
                        tvDate.setText(date);
                    }
                }).show();
    }

    //显示时间选择器
    private void showTimePicker(final TextView tvTime) {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_layout_time, null);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        TimePicker timePicker = (TimePicker) dialogView
                                .findViewById(R.id.timePicker);
                        String date = timePicker.getCurrentHour() + ":" +
                                timePicker.getCurrentMinute();
                        tvTime.setText(date);
                    }
                }).show();
    }

    //检查活动信息是否填写完整
    private boolean proveActivity(String theme, String describe, String number,
                                  String place, String begin, String end) {
        if (!theme.isEmpty() && !describe.isEmpty()
                && !number.isEmpty()
                && !place.isEmpty() && !begin.isEmpty()
                && !end.isEmpty()) {

            return true;

        } else {
            showSnackBarLong(llActivityPublish, R.string.publish_info_cannot_be_empty);
        }
        return false;
    }

    private Tb_activity makeActivityInfo() {
        Tb_activity activity = new Tb_activity();
        activity.setAvatar(mUser.getHeadView());
        activity.setUsername(mUser.getUsername());
        activity.setTheme(theme);
        activity.setActivityDescribe(describe);
        activity.setPicture(imageUrl);
        activity.setSponsor(mUser.getRealName());
        activity.setNumber(Integer.parseInt(number));
        activity.setPlace(place);
        activity.setActivityBegin(begin);
        activity.setActivityEnd(end);

        return activity;
    }

    //将要发布的活动信息提交到服务器
    private void post2Server(Tb_activity activity) {
        Gson gson = new Gson();
        RequestParams params = new RequestParams(URL + "Activity");
        params.setConnectTimeout(8000);
        String activityData = gson.toJson(activity);
        params.addBodyParameter("activityData", activityData);
        params.addBodyParameter("type", "addActivity");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    new AlertDialog.Builder(PublishActivity.this)
                            .setTitle("提示")
                            .setMessage("发布成功，快去看看最新发布的活动吧")
                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent();
                                    setResult(RESULT_OK, intent);
                                    PublishActivity.this.finish();
                                }
                            })
                            .show();
                } else {
                    showSnackBarShort(llActivityPublish, R.string.publish_activity_fail);
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
                item.setCheckable(true);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_activity_publish:
                new AlertDialog.Builder(this)
                        .setTitle("发布")
                        .setMessage("确定发布这个活动吗？")
                        .setNegativeButton("不是现在", null)
                        .setPositiveButton("发布",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        publishActivity(item);
                                    }
                                }).show();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void publishActivity(MenuItem item) {
        theme = etActivityTheme.getText().toString();
        describe = etActivityDescribe.getText().toString();
        number = etActivityNumber.getText().toString();
        place = etActivityPlace.getText().toString();
        begin = tvDateBegin.getText().toString() + " " + tvTimeBegin.getText().toString();
        end = tvDateEnd.getText().toString() + " " + tvTimeEnd.getText().toString();

        //判空
        if (proveActivity(theme, describe, number, place, begin, end)) {
            item.setCheckable(false);
            mDialog.show();
            if (!imageUrl.isEmpty()) {
                QiniuCloudUtil.upLoadImage(imageUrl, null, mHandler);
            } else {
                Tb_activity activity = makeActivityInfo();
                post2Server(activity);
            }
        }
    }

    //接收上一个界面返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1003 && resultCode == RESULT_OK) {
            //获取到上级界面传回的资源地址
            ArrayList<String> image = (ArrayList<String>) data.getSerializableExtra("image");
            imageUrl = image.get(0);
            setImage(imageUrl);
        }
    }

    //设置图片
    private void setImage(final String image) {
        ImageOptions options = new ImageOptions.Builder().build();
        x.image().bind(ivActivityPicture, image, options);
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

}
