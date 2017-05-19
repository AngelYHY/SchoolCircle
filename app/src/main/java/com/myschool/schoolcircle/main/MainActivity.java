package com.myschool.schoolcircle.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.lib.CircleImageView;
import com.myschool.schoolcircle.ui.activity.mine.About;
import com.myschool.schoolcircle.ui.activity.mine.MineActivity;
import com.myschool.schoolcircle.ui.activity.mine.Notification;
import com.myschool.schoolcircle.ui.activity.mine.Settings;
import com.myschool.schoolcircle.ui.activity.school.activitys.ActivityActivity;
import com.myschool.schoolcircle.ui.activity.school.dynamic.AboutMeActivity;
import com.myschool.schoolcircle.ui.activity.welcome.WelcomeActivity;
import com.myschool.schoolcircle.ui.fragment.MessageFragment;
import com.myschool.schoolcircle.utils.FragmentController;
import com.myschool.schoolcircle.utils.ToastUtil;

import java.io.File;
import java.util.Set;

import butterknife.Bind;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.GetUserInfoCallback;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageBaseEvent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

public class MainActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener,
        NavigationView.OnNavigationItemSelectedListener {

    @Bind(R.id.layout_main)
    DrawerLayout layoutMain;
    @Bind(R.id.nv_menu)
    NavigationView nvMenu;
    @Bind(R.id.layout_main_temp)
    FrameLayout layoutMainTemp;
    @Bind(R.id.rb_message)
    RadioButton rbMessage;
    @Bind(R.id.rb_contact)
    RadioButton rbContact;
    @Bind(R.id.rb_school)
    RadioButton rbSchool;
    @Bind(R.id.rg_main)
    RadioGroup rgMain;
    public static LinearLayout layoutRadio;

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private FragmentController fragmentController;
    private FragmentManager fragmentManager;

    private UserInfo mMyInfo;
    private File avatarFile;
    public static CircleImageView ivHeadView;
    public static TextView tvRealName;

    private long exitTime = 0;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_main);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        init();
//    }

    //初始化
    protected void initView() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        mMyInfo = application.getMyInfo();
        layoutRadio = (LinearLayout) findViewById(R.id.layout_radio);

        //初始化主界面三个fragment
        fragmentManager = getSupportFragmentManager();
        fragmentController = FragmentController
                .getInstance(R.id.layout_main_temp, fragmentManager);

        rgMain.setOnCheckedChangeListener(this);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, layoutMain,
                R.string.draw_open, R.string.draw_close);
        layoutMain.addDrawerListener(actionBarDrawerToggle);

        nvMenu.setNavigationItemSelectedListener(this);
        initHeaderView();
    }

    //底部导航栏点击事件
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.rb_message:
                fragmentController.showFragment(0);
                break;
            case R.id.rb_contact:
                fragmentController.showFragment(1);
                break;
            case R.id.rb_school:
                fragmentController.showFragment(2);
                break;
            case R.id.rb_activity:
                fragmentController.showFragment(3);
                break;
            default:
                break;
        }
    }

    //初始化侧滑栏头部
    private void initHeaderView() {
        View view = nvMenu.inflateHeaderView(R.layout.layout_main_menu_head);
        ivHeadView = (CircleImageView) view.findViewById(R.id.iv_user_head);
        tvRealName = (TextView) view.findViewById(R.id.tv_user_name);

        avatarFile = mMyInfo.getAvatarFile();
        if (avatarFile != null) {
            ivHeadView.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getPath()));
        } else {
            //重新下载用户信息
            JMessageClient.getUserInfo(mMyInfo.getUserName(), new GetUserInfoCallback() {
                @Override
                public void gotResult(int i, String s, UserInfo userInfo) {
                    if (i == 0) {
                        avatarFile = userInfo.getAvatarFile();
                        ivHeadView.setImageBitmap(BitmapFactory.decodeFile(avatarFile.getPath()));
                        tvRealName.setText(userInfo.getNickname());
                    }
                }
            });
        }

        if (!mMyInfo.getNickname().isEmpty()) {
            tvRealName.setText(mMyInfo.getNickname());
        } else {
            tvRealName.setText(mMyInfo.getUserName());
        }

        ivHeadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MineActivity.class);
                intent.putExtra("type", "mine");
                startActivity(intent);

            }
        });
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //    消息接收事件监听
    public void onEventMainThread(MessageBaseEvent event) {
        Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case text:
            case image:
            case voice:
                //通知消息界面刷新UI
                doRefreshData(MessageFragment.getMessageFragment());
                break;
            case custom:
                //处理自定义消息

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.menu_toolbar_notifications:
                intentToActivity(Notification.class);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //侧滑栏菜单点击事件
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_my_dynamic:
                //我的动态
                Intent intent1 = new Intent(this, AboutMeActivity.class);
                startActivityForResult(intent1, 1);
                break;

            case R.id.menu_my_activity:
                //我的活动
                Intent intent2 = new Intent(this, ActivityActivity.class);
                intent2.putExtra("activityType", "myActivity");
                startActivity(intent2);
                break;

            case R.id.menu_settings:
                //设置
                intentToActivity(Settings.class);
                break;

            case R.id.menu_about:
                //关于
                intentToActivity(About.class);
                break;

            case R.id.menu_exit:
                //注销
                View dialogView = LayoutInflater.from(this)
                        .inflate(R.layout.dialog_layout_tip,null);
                TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
                tvTitle.setText("退出登录");
                tvMessage.setText("离开后将无法收到好友消息，确定继续？");
                new AlertDialog.Builder(this).setView(dialogView)
                        .setNegativeButton("不是现在",null)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                                        intent.putExtra("type", "logout");
                                        startActivity(intent);
                                        logout(MainActivity.this);
                                        finish();
                                    }
                                }).show();

                break;

            default:
                break;
        }

        item.setChecked(true);
//        layoutMain.closeDrawers();
        return true;
    }

    //刷新消息界面中数据的接口
    public interface OnRefreshDataListener {
        //回调方法
        void onRefreshData();
    }

    //调用回调方法
    private void doRefreshData(OnRefreshDataListener refreshDataListener) {
        refreshDataListener.onRefreshData();
    }

    //按下后退键返回桌面
//    public void onBackPressed() {
//        if (layoutMain.isDrawerOpen(nvMenu)) {
//            layoutMain.closeDrawers();
//        } else {
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            startActivity(intent);
//        }
//    }

    //按两次返回键退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtil.showToast(this,"再按一次退出程序", Toast.LENGTH_SHORT);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logout(this);
        JPushInterface.setAlias(this, "", new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {

            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_main;
    }
}
