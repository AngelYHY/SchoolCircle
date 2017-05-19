package com.myschool.schoolcircle.ui.activity.mine;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.myschool.schoolcircle.adapter.CommonAdapter;
import com.myschool.schoolcircle.adapter.ViewHolder;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.lib.CircleImageView;
import com.myschool.schoolcircle.ui.activity.MainActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.concact.single.SingleChat;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.QiniuCloudUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class MineActivity extends BaseActivity
        implements View.OnClickListener{

    @Bind(R.id.tb_mine)
    Toolbar tbMine;
    @Bind(R.id.cl_mine)
    CoordinatorLayout clMine;
    @Bind(R.id.tv_real_name)
    TextView tvRealName;
    @Bind(R.id.tv_birthday)
    TextView tvBirthday;
    @Bind(R.id.tv_school_name)
    TextView tvSchoolName;
    @Bind(R.id.tv_start_school_year)
    TextView tvStartSchoolYear;
    @Bind(R.id.civ_user_head)
    CircleImageView civUserHead;
    @Bind(R.id.tv_gender)
    TextView tvGender;
    @Bind(R.id.fab_action)
    FloatingActionButton fabAction;
    @Bind(R.id.tv_signature)
    TextView tvSignature;
    @Bind(R.id.rl_real_name)
    RelativeLayout rlRealName;
    @Bind(R.id.rl_gender)
    RelativeLayout rlGender;
    @Bind(R.id.rl_birthday)
    RelativeLayout rlBirthday;
    @Bind(R.id.rl_school_name)
    RelativeLayout rlSchoolName;
    @Bind(R.id.rl_start_school_year)
    RelativeLayout rlStartSchoolYear;
    @Bind(R.id.ll_signature)
    LinearLayout llSignature;

    private UserInfo mUserInfo;
    private Tb_user mUser;
    private ProgressDialog mProgressDialog;
    private String type;
    private ItemAdapter mAdapter;

    private class ItemAdapter extends CommonAdapter<String>{

        public ItemAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            holder.setText(R.id.tv_text,s);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.GET_SUCCESS:
                    refreshInformation();
                    break;
                case HandlerKey.GET_FAIL:

                    break;
                case HandlerKey.UPDATE_SUCCESS:
                    updateInformation();
                    mProgressDialog.cancel();
                    showSnackBarLong(clMine,"更新信息成功");
                    break;
                case HandlerKey.UPDATE_FAIL:
                    mProgressDialog.cancel();
                    showSnackBarLong(clMine,"更新信息失败");
                    break;
                case HandlerKey.UPLOAD_SUCCESS:
                    String url = (String) msg.obj;
                    postNewInfo2Server(url,"user_head");
                    break;
                case HandlerKey.UPLOAD_FAIL:

                    break;
                default:
                    break;
            }
        }
    };

//   @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        hideStateBar();
//        setContentView(R.layout.layout_activity_mine);
//        ButterKnife.bind(this);
//
//        init();
//    }


    @Override
    public void setContentView(int layoutResID) {
        hideStateBar();
        super.setContentView(layoutResID);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_mine;
    }

    //状态栏透明
    private void hideStateBar() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化数据
    protected void initView() {
        setSupportActionBar(tbMine);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("名片");

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this,"更新中...");
        initUserInfo();
        getUserInfo();
    }

    private void initUserInfo() {
        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (type.equals("mine")) {
            //进入个人中心
            mUserInfo = application.getMyInfo();
            refreshInformation();

            fabAction.setVisibility(View.GONE);
            civUserHead.setOnClickListener(this);
            rlRealName.setOnClickListener(this);
            rlGender.setOnClickListener(this);
            rlBirthday.setOnClickListener(this);
            rlSchoolName.setOnClickListener(this);
            rlStartSchoolYear.setOnClickListener(this);
            llSignature.setOnClickListener(this);
        } else {
            //进入好友的个人中心
            final String nickName = intent.getStringExtra("nickName");
            JMessageClient.getUserInfo(type,
                    new GetUserInfoCallback() {
                        @Override
                        public void gotResult(int i, String s, UserInfo userInfo) {
                            if (i == 0) {
                                mUserInfo = userInfo;
                                refreshInformation();
                            }
                        }
                    });
            fabAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //发消息
                    Intent intent = new Intent(MineActivity.this, SingleChat.class);
                    intent.putExtra("targetUsername", type);
                    intent.putExtra("nickName",nickName);
                    startActivity(intent);
                }
            });
        }
    }

    //初始化头像
    private void initAvatar() {
        mUserInfo.getAvatarBitmap(new GetAvatarBitmapCallback() {
            @Override
            public void gotResult(int i, String s, Bitmap bitmap) {
                if (i == 0) {
                    civUserHead.setImageBitmap(bitmap);
                    if (mUserInfo.getUserName()
                            .equals(application.getMyInfo().getUserName())){
                        MainActivity.ivHeadView.setImageBitmap(bitmap);
                    }
                }
            }
        });
    }

    //刷新数据
    private void refreshInformation() {
        initAvatar();
        String name = mUserInfo.getNickname();
        if (!name.isEmpty()) {
            tvRealName.setText(name);
        } else {
            tvRealName.setText(mUserInfo.getUserName());
        }

        if (mUserInfo.getGender() != null) {
            switch (mUserInfo.getGender()) {
                case male:
                    tvGender.setText("男");
                    break;
                case female:
                    tvGender.setText("女");
                    break;
            }
        } else {
            tvGender.setText("未设置");
        }

        if (mUserInfo.getBirthday() > 28800000) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mUserInfo.getBirthday());
            Log.i("MineActivity", "refreshInformation: MineActivity"+mUserInfo.getBirthday());
            String time = new SimpleDateFormat("yyyy年MM月dd日").format(calendar.getTime());
            tvBirthday.setText(time);
        } else {
            tvBirthday.setText("未设置");
        }

        if (!mUserInfo.getRegion().isEmpty()) {
            tvSchoolName.setText(mUserInfo.getRegion());
        }
        if (!mUserInfo.getSignature().isEmpty()) {
            tvSignature.setText(mUserInfo.getSignature());
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.civ_user_head:
                //设置头像
                Intent intent = new Intent(this,ImageActivity.class);
                startActivityForResult(intent,1);
                break;
            case R.id.rl_real_name:
                //设置昵称
                showEditDialog("设置昵称","请输入新昵称");
                break;
            case R.id.rl_gender:
                //设置性别
                showRadioDialog("设置性别");
                break;
            case R.id.rl_birthday:
                //设置性别
                showDatePicker();
                break;
            case R.id.rl_school_name:
                //设置学校名称
                showEditDialog("设置学校","请输入学校名称");
                break;
            case R.id.rl_start_school_year:
                //设置入学年份
                showYearList("入学年份");
                break;
            case R.id.ll_signature:
                //设置个性签名
                showEditDialog("个性签名","书写你的风格");
                break;
        }
    }

    //显示设置文字对话框
    private void showEditDialog(final String title, String hint) {
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_layout_edit,null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        final EditText editText = (EditText) dialogView.findViewById(R.id.et_edit);
        tvTitle.setText(title);
        editText.setHint(hint);

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String input = editText.getText().toString().trim();
                        switch (title) {
                            case "设置昵称":
                                setNickName(input);
                                break;
                            case "个性签名":
                                setSignature(input);
                                break;
                            case "设置学校":
                                setSchoolName(input);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    //显示单选对话框
    private void showRadioDialog(String title) {
        final View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_layout_select,null);

        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        final RadioButton rbMale = (RadioButton) dialogView.findViewById(R.id.rb_male);
        RadioButton rbFemale = (RadioButton) dialogView.findViewById(R.id.rb_female);
        tvTitle.setText(title);
        rbMale.setText("男");
        rbFemale.setText("女");

        new AlertDialog.Builder(this)
                .setView(dialogView)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (rbMale.isChecked()) {
                            setGender(1);
                        } else {
                            setGender(0);
                        }
                    }
                }).show();
    }

    //显示日期选择器
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
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth());
                        setBirthday(calendar.getTimeInMillis());
                    }
                }).show();
    }

    //显示入学年份列表
    private void showYearList(String title) {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_layout_text, null);

        Calendar calendar = Calendar.getInstance();
        final List<String> years = new ArrayList<>();
        for (int i = 2010; i < 2017; i++) {
            years.add(i+"");
        }
        mAdapter = new ItemAdapter(this,years,R.layout.item_text);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        tvTitle.setText(title);
        ListView listView = (ListView) dialogView.findViewById(R.id.lv_items);
        listView.setAdapter(mAdapter);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView).show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setStartSchoolYear(years.get(i));
                dialog.cancel();
            }
        });
    }

    //设置昵称
    private void setNickName(final String name) {
        if (!name.isEmpty()) {
            if (name.length() < 10) {
                mProgressDialog.show();
                mUserInfo.setNickname(name);
                JMessageClient.updateMyInfo(UserInfo.Field.nickname, mUserInfo, new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            postNewInfo2Server(name,"real_name");
                            mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                        }
                    }
                });
            } else {
                showSnackBarLong(clMine,"昵称不能超过十个字");
            }
        } else {
            showSnackBarLong(clMine,"你没有输入任何信息");
        }
    }

    //设置性别
    private void setGender(final int gender) {
        mProgressDialog.show();

        if (gender == 1) {
            mUserInfo.setGender(UserInfo.Gender.male);
        } else {
            mUserInfo.setGender(UserInfo.Gender.female);
        }

        JMessageClient.updateMyInfo(UserInfo.Field.gender, mUserInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    postNewInfo2Server(gender+"","sex");
//                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                }
            }
        });
    }

    //设置生日
    private void setBirthday(final long date) {
        mProgressDialog.show();
        mUserInfo.setBirthday(date);
        JMessageClient.updateMyInfo(UserInfo.Field.birthday, mUserInfo, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    postNewInfo2Server(date+"","birthday");
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                }
            }
        });
    }

    //设置个性签名
    private void setSignature(final String signature) {
        if (!signature.isEmpty()) {
            if (signature.length() < 100) {
                mProgressDialog.show();
                mUserInfo.setSignature(signature);
                JMessageClient.updateMyInfo(UserInfo.Field.signature, mUserInfo,
                        new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    postNewInfo2Server(signature,"signature");
                                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                                } else {
                                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                                }
                            }
                        });
            } else {
                showSnackBarLong(clMine,"输入信息过长");
            }
        } else {
            showSnackBarLong(clMine,"你没有输入任何信息");
        }
    }

    //设置学校名称
    private void setSchoolName(final String schoolName) {
        if (!schoolName.isEmpty()) {
            if (schoolName.length() < 30) {
                mProgressDialog.show();
                mUserInfo.setRegion(schoolName);
                JMessageClient.updateMyInfo(UserInfo.Field.region, mUserInfo,
                        new BasicCallback() {
                            @Override
                            public void gotResult(int i, String s) {
                                if (i == 0) {
                                    postNewInfo2Server(schoolName,"schoolName");
                                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                                } else {
                                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                                }
                            }
                        });
            } else {
                showSnackBarLong(clMine,"输入信息过长");
            }
        } else {
            showSnackBarLong(clMine,"你没有输入任何信息");
        }
    }

    //设置入学年份
    private void setStartSchoolYear(final String year) {
        postNewInfo2Server(year,"start_School_Year");
    }

    //设置头像
    private void setAvatar(final String path) {
        if (path != null && !path.isEmpty()) {
            mProgressDialog.show();
            JMessageClient.updateUserAvatar(new File(path), new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        QiniuCloudUtil.upLoadImage(path,null,mHandler);
                        mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                    } else {
                        mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                    }
                }
            });
        }
    }

    //从服务器获取用户信息
    private void getUserInfo() {
        RequestParams params = new RequestParams(URL+"QueryUser");
        params.setConnectTimeout(8000);
        if ("mine".equals(type)) {
            params.addBodyParameter("username",mUserInfo.getUserName());
        } else {
            params.addBodyParameter("username",type);
        }

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
//                if (!"fail".equals(result)) {
//                    Gson gson = new Gson();
//                    mUser = gson.fromJson(result,Tb_user.class);
//                    tvStartSchoolYear.setText(mUser.getStartSchoolYear());
//                }
                return false;
            }

            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Gson gson = new Gson();
                    mUser = gson.fromJson(result,Tb_user.class);
                    tvStartSchoolYear.setText(mUser.getStartSchoolYear());
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

    //将信息更新到服务器
    private void postNewInfo2Server(final String update, final String target) {
        RequestParams params = new RequestParams(URL+"Mine");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type","information");
        params.addBodyParameter("username",mUserInfo.getUserName());
        params.addBodyParameter("update",update);
        params.addBodyParameter("target",target);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    if (target.equals("start_School_Year")) {
                        tvStartSchoolYear.setText(update);
                        mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                    } else {
//                        mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                    }
                } else {
                    Log.i("MineActivity", "onSuccess: MineActivity更新失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if (target.equals("start_School_Year")) {
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
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

    //获取上个界面返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            List<String> list = (List<String>) data.getSerializableExtra("image");
            setAvatar(list.get(0));
        }
    }

    //更新个人信息
    private void updateInformation() {
        refreshInformation();
        MainActivity.tvRealName.setText(mUserInfo.getNickname());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!type.equals("mine")) {
            getMenuInflater().inflate(R.menu.menu_delete_friend,menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_delete_friend:
                new AlertDialog.Builder(this)
                        .setTitle("删除好友")
                        .setMessage("同时会将我从对方的好友列表中删除，确定继续这个操作？")
                        .setNegativeButton("取消",null)
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除好友
                        deleteFriend(mUserInfo.getUserName());
                    }
                }).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //删除好友关系
    public void deleteFriend(String friendName) {
        mProgressDialog.setMessage("删除好友中...");
        mProgressDialog.show();

        RequestParams params = new RequestParams(URL + "FriendsManager");
        params.addBodyParameter("type","deleteFriend");
        params.addBodyParameter("friend_name", friendName);
        params.addBodyParameter("user_name", application.getMyInfo().getUserName());
        params.setConnectTimeout(8000);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(clMine, "删除成功");
                } else {
                    showSnackBarLong(clMine, "删除失败");
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

}
