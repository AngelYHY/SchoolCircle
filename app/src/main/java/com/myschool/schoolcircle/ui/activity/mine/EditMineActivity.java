package com.myschool.schoolcircle.ui.activity.mine;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class EditMineActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.tb_edit_mine)
    Toolbar tbEditMine;
    @Bind(R.id.btn_edit_head_view)
    Button btnEditHeadView;
    EditText etRealName;
    @Bind(R.id.til_real_name)
    TextInputLayout tilRealName;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.rg_gender)
    RadioGroup rgGender;
    @Bind(R.id.ll_birthday)
    LinearLayout llBirthday;
    EditText etSchoolName;
    @Bind(R.id.til_school_name)
    TextInputLayout tilSchoolName;
    EditText etSignature;
    @Bind(R.id.til_signature)
    TextInputLayout tilSignature;
    @Bind(R.id.iv_head_view)
    ImageView ivHeadView;
    @Bind(R.id.tv_birthday)
    TextView tvBirthday;
    @Bind(R.id.tv_start_school_year)
    TextView tvStartSchoolYear;
    @Bind(R.id.ll_edit_mine)
    LinearLayout llEditMine;

    private ProgressDialog mDialog;
    private Tb_user mUser;
    private UserInfo jUser;
    private String realName, headViewUrl,headViewUri, birthday,
            schoolName, startSchoolYear, signature;
    private UserInfo.Gender gender;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_edit_mine);
//        ButterKnife.bind(this);
//
//        init();
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化
    private void init() {
        Intent intent = getIntent();
        mUser = (Tb_user) intent.getSerializableExtra("user");
    }

    //初始化界面
    protected void initView() {
        init();

        //初始化控件
        setSupportActionBar(tbEditMine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDialog = new ProgressDialog(this);
        mDialog.setMessage("更新信息中...");
        mDialog.setCanceledOnTouchOutside(false);

        rgGender.setOnCheckedChangeListener(this);

        etRealName = tilRealName.getEditText();
        etSchoolName = tilSchoolName.getEditText();
        etSignature = tilSignature.getEditText();

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_edit_mine;
    }

    //初始化
    private void initData() {
        //初始化用户信息
        mUser = application.getUser();
        jUser = JMessageClient.getMyInfo();

        realName = jUser.getNickname();
        gender = jUser.getGender();
        signature = jUser.getSignature();

        startSchoolYear = mUser.getStartSchoolYear();
        birthday = mUser.getBirthday();

        //初始化界面信息
        etRealName.setText(realName);
        tvBirthday.setText(birthday);
        tvStartSchoolYear.setText(startSchoolYear);
        etSignature.setText(signature);

        if (gender == UserInfo.Gender.female) {
            rbGirl.setChecked(true);
        } else {
            rbBoy.setChecked(true);
        }
        setAvatar();
    }

    //设置头像
    private void setAvatar() {
        jUser.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    ivHeadView.setImageBitmap(bitmap);
                }
            }
        });
    }

    @OnClick({R.id.btn_edit_head_view, R.id.ll_birthday})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_edit_head_view:
                Intent intent = new Intent(this,ImageActivity.class);
                startActivityForResult(intent,1002);
                break;
            case R.id.ll_birthday:
                showDatePicker();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_boy:
                gender = UserInfo.Gender.male;
                break;
            case R.id.rb_girl:
                gender = UserInfo.Gender.female;
                break;
        }
    }

    //设置日期
    private void showDatePicker() {
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
                        String date = datePicker.getYear() +"-"
                                + (datePicker.getMonth()+1) +"-"
                                + datePicker.getDayOfMonth();
                        tvBirthday.setText(date);
                    }
                }).show();
    }

    //确定修改
    private void finishEdit() {
        realName = etRealName.getText().toString();
        birthday = tvBirthday.getText().toString();
        schoolName = etSchoolName.getText().toString();
        startSchoolYear = tvStartSchoolYear.getText().toString();
        signature = etSignature.getText().toString();

        Tb_user user = new Tb_user();
        user.setUsername(mUser.getUsername());
        user.setBirthday(birthday);
        user.setStartSchoolYear(startSchoolYear);

        UserInfo jUser = JMessageClient.getMyInfo();
        jUser.setNickname(realName);
        jUser.setGender(UserInfo.Gender.male);

        JMessageClient.updateMyInfo(UserInfo.Field.nickname, jUser,null);
        JMessageClient.updateMyInfo(UserInfo.Field.gender,jUser,null);

        JMessageClient.updateUserAvatar(new File(headViewUri), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    Log.i("EditMineActivity", "gotResult: 图片上传成功");
                } else {
                    Log.i("EditMineActivity", "gotResult: 图片上传失败");
                }
            }
        });

        //将信息提交到服务器进行处理
//        doPost(user);
    }

//    //post请求
//    private void doPost(Tb_user user) {
//        RequestParams params = new RequestParams(URL + "Mine");
//        params.addBodyParameter("username", user.getUsername());
//        params.addBodyParameter("headView", user.getHeadView());
//        params.addBodyParameter("realName", user.getRealName());
//        params.addBodyParameter("gender", user.getGender());
//        params.addBodyParameter("birthday", user.getBirthday());
//        params.addBodyParameter("startSchoolYear", user.getStartSchoolYear());
//        params.addBodyParameter("signature", user.getSignature());
//        params.setConnectTimeout(8000);
//
//        x.http().post(params, new Callback.CommonCallback<String>() {
//            @Override
//            public void onSuccess(String result) {
//                setNewUserInfo(result);
//            }
//
//            @Override
//            public void onError(Throwable ex, boolean isOnCallback) {
//                alterFail();
//            }
//
//            @Override
//            public void onCancelled(CancelledException cex) {
//
//            }
//
//            @Override
//            public void onFinished() {
//                mDialog.cancel();
//            }
//        });
//    }

//    //数据处理
//    private void setNewUserInfo(String result) {
//        if (!"fail".equals(result)) {
//            Gson gson = new Gson();
//            Tb_user user = gson.fromJson(result, Tb_user.class);
//            mUser.setHeadView(user.getHeadView());
//            mUser.setRealName(user.getRealName());
//            mUser.setGender(user.getGender());
//            mUser.setBirthday(user.getBirthday());
//            mUser.setStartSchoolYear(user.getStartSchoolYear());
//            mUser.setSignature(user.getSignature());
//
//            Intent intent = new Intent();
//            setResult(RESULT_OK, intent);
//            finish();
//        } else {
//            alterFail();
//        }
//    }

//    //修改失败提示
//    private void alterFail() {
//        showSnackBarLong(llEditMine,R.string.alter_fail);
//    }

    //解析标题栏布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checked_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //标题栏菜单按钮的监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_checked:
                mDialog.show();
                finishEdit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //接收上一个界面返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK) {
            //获取到上级界面传回的资源地址
            ArrayList<String> image = (ArrayList<String>) data.getSerializableExtra("image");
            headViewUri = image.get(0);
            setHeadView(headViewUri);
        }
    }

    //设置临时头像
    private void setHeadView(String uri) {
        final ImageOptions options = new ImageOptions.Builder()
                .setLoadingDrawableId(R.mipmap.ic_head)
                .setFailureDrawableId(R.mipmap.ic_head).build();
        x.image().bind(ivHeadView, uri, options);
    }
}
