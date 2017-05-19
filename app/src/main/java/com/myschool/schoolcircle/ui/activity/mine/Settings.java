package com.myschool.schoolcircle.ui.activity.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.main.R;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class Settings extends BaseActivity implements RadioGroup.OnCheckedChangeListener {

    @Bind(R.id.tb_settings)
    Toolbar tbSettings;
    @Bind(R.id.swh_notification)
    Switch swhNotification;
    @Bind(R.id.swh_voice)
    Switch swhVoice;
    @Bind(R.id.swh_vibrate)
    Switch swhVibrate;
    @Bind(R.id.swh_shack)
    Switch swhShack;
    @Bind(R.id.rl_settings)
    LinearLayout rlSettings;
    @Bind(R.id.rl_phone)
    RelativeLayout rlPhone;
    @Bind(R.id.rl_clear)
    RelativeLayout rlClear;
    @Bind(R.id.rl_password)
    RelativeLayout rlPassword;
    @Bind(R.id.tv_memory)
    TextView tvMemory;

    //实例化SharedPreferences对象（第一步）
    private SharedPreferences mySharedPreferences;
    //实例化SharedPreferences.Editor对象（第二步）
    private SharedPreferences.Editor editor;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_settings);
//        ButterKnife.bind(this);
//
//        JMessageClient.registerEventReceiver(this);
//
//        initEvent();
//        initView();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {

        JMessageClient.registerEventReceiver(this);

        initEvent();

        setSupportActionBar(tbSettings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //保存，读取数据，先实例化对象
        mySharedPreferences = getSharedPreferences("save", MODE_PRIVATE);
        editor = mySharedPreferences.edit();

        saveSettings();

        initData();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_settings;
    }

    private void saveSettings() {
        //使用getBoolean获取方式获取value，第二个参数是value 的默认值
        //通知 默认打开
        Boolean swhNotification1 = mySharedPreferences.getBoolean("swhNotification", true);
        //声音 默认打开
        Boolean swhVoice1 = mySharedPreferences.getBoolean("swhVoice", true);
        //震动 默认打开
        Boolean swhVibrate1 = mySharedPreferences.getBoolean("swhVibrate", true);
        //摇一摇 默认关闭
        Boolean swhShack1 = mySharedPreferences.getBoolean("swhShack", true);
        //设置选中状态
        swhNotification.setChecked(swhNotification1);
        swhVoice.setChecked(swhVoice1);
        swhVibrate.setChecked(swhVibrate1);
        swhShack.setChecked(swhShack1);
    }

    private void initData() {
        try {
            //查看缓存的大小 直接显示
            tvMemory.setText(DataCleanManager.getTotalCacheSize(Settings.this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //初始化监听事件
    private void initEvent() {
        //通知
        swhNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    //打开通知
                    JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
                    swhVoice.setClickable(true);
                    swhVibrate.setClickable(true);
                    //这边是开关，只有两个状态，用putBoolean 的方式保存数据
                    editor.putBoolean("swhNotification", true);
                    //保存完了，要记得提交
                    //提交当前数据
                    editor.commit();
                } else {
                    //关闭通知
                    JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_NOTIFICATION);
                    swhVoice.setClickable(false);
                    swhVibrate.setClickable(false);
                    editor.putBoolean("swhNotification", false);
                    editor.commit();
                }
            }
        });

        //声音
        swhVoice.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    //打开声音
                    if (swhVibrate.isChecked()) {
                        //有震动
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
                        Log.i("Settings", "onCheckedChanged: Settings有声音有震动");
                    } else {
                        //无震动
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_VIBRATE);
                        Log.i("Settings", "onCheckedChanged: Settings有声音无震动");
                    }
                    editor.putBoolean("swhVoice", true);
                    editor.commit();
                } else {
                    //关闭声音
                    if (swhVibrate.isChecked()) {
                        //有震动
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_SOUND);
                        Log.i("Settings", "onCheckedChanged: Settings无声音有震动");
                    } else {
                        //无震动
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_SILENCE);
                        Log.i("Settings", "onCheckedChanged: Settings无声音无震动");
                    }
                    editor.putBoolean("swhVoice", false);
                    editor.commit();
                }
            }
        });

        //震动
        swhVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {

                    //打开震动
                    if (swhVoice.isChecked()) {
                        //有声音
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
                        Log.i("Settings", "onCheckedChanged: Settings有声音有震动");
                    } else {
                        //无声音
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_SOUND);
                        Log.i("Settings", "onCheckedChanged: Settings无声音有震动");
                    }
                    editor.putBoolean("swhVibrate", true);
                    editor.commit();
                } else {

                    //关闭震动
                    if (swhVoice.isChecked()) {
                        //有声音
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_NO_VIBRATE);
                        Log.i("Settings", "onCheckedChanged: Settings有声音无震动");
                    } else {
                        //无声音
                        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_SILENCE);
                        Log.i("Settings", "onCheckedChanged: Settings无声音无震动");
                    }
                    editor.putBoolean("swhVibrate", false);
                    editor.commit();
                }
            }
        });

        //摇一摇刷新开关
        swhShack.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    editor.putBoolean("swhShack", true);
                    editor.commit();
                    application.getSettingConfig().setShack(0);
                    //开启摇一摇刷新
                    Log.i("Settings", "onCheckedChanged: Settings开启摇一摇刷新");
                } else {
                    //关闭摇一摇刷新
                    editor.putBoolean("swhShack", false);
                    editor.commit();
                    application.getSettingConfig().setShack(-1);
                    Log.i("Settings", "onCheckedChanged: Settings关闭摇一摇刷新");
                }
            }
        });
    }

    //点击手机号，修改密码，清除缓存
    @OnClick({R.id.rl_phone, R.id.rl_clear, R.id.rl_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_phone:
                //修改手机号码
                Intent phone = new Intent(this, ModifyPhoneActivity.class);
                startActivity(phone);

                break;
            case R.id.rl_clear:
                //清除缓存，跳出提示框
                showClearDialog();

                break;
            case R.id.rl_password:
                //修改密码
                Intent password = new Intent(this, ModifyPasswordActivity.class);
                startActivity(password);

                break;
            default:
                break;
        }
    }

    //显示清除缓存对话框
    private void showClearDialog() {
        new AlertDialog.Builder(this)
                .setMessage("确定清除缓存吗？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //清除操作  这边清除的是只有图片缓存
                        DataCleanManager.clearAllCache(Settings.this);
                        //设置清除之后显示的缓存  显示的也是图片缓存
                        try {
                            tvMemory.setText(DataCleanManager.getTotalCacheSize(Settings.this));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JMessageClient.unRegisterEventReceiver(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

    }
}