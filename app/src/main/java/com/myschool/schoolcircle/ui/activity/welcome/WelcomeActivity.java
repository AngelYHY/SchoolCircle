package com.myschool.schoolcircle.ui.activity.welcome;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.presenter.impl.WelcomePresent;
import com.myschool.schoolcircle.ui.activity.MainActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.ToastUtil;
import com.myschool.schoolcircle.view.WelcomeView;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Set;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

public class WelcomeActivity extends BaseActivity implements WelcomeView {

    @Inject
    WelcomePresent mPresenter;

    @Bind(R.id.iv_logo)
    ImageView ivLogo;
    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.til_username)
    TextInputLayout tilUsername;
    @Bind(R.id.til_password)
    TextInputLayout tilPassword;
    @Bind(R.id.tv_Loading)
    TextView tvLoading;
    @Bind(R.id.rl_welcome)
    RelativeLayout rlWelcome;
    @Bind(R.id.btn_forget_password)
    Button btnForgetPassword;
    @Bind(R.id.cb_visible)
    CheckBox cbVisible;
    @Bind(R.id.rl_password)
    RelativeLayout rlPassword;

    private ProgressDialog mProgressDialog;
    private ValueAnimator animator;
    private int windowHeight;
    private SharedPreferences preference;
    private SharedPreferences.Editor editor;

    private int duration = 400;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    //气泡呼吸
                    doBreath();
                    break;
                case 2:
                    //气泡停止呼吸
                    animator.cancel();
                    //气泡放大
                    doAmplify();
                    hideView();
                    showView();
                    handler.sendEmptyMessageDelayed(3, 400);
                    break;
                case 3:
                    //气泡循环上下移动
                    doMoveUpDown();
                    //气泡循环左右移动
                    doMoveLeftRight();
                    break;
                case HandlerKey.LOGIN_SUCCESS:
                    mProgressDialog.cancel();
                    readyGoThenKill(MainActivity.class);
                    break;
                case HandlerKey.LOGIN_FAIL:
                    mProgressDialog.cancel();
                    String error = (String) msg.obj;

                    if (error.toLowerCase().equals("invalid password")) {
                        showSnackBarLong(rlWelcome, "密码输入错误");
                    }
                    if (error.toLowerCase().equals("user not exist")) {
                        showSnackBarLong(rlWelcome, "用户不存在");
                    }
                    break;
                case 0:
                    mProgressDialog.cancel();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initInjector() {
        super.initInjector();
        mActivityComponent.inject(this);
        mIPresenter = mPresenter;
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        //设置状态栏透明
//        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            Window window = getWindow();
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
//                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//            window.setStatusBarColor(Color.TRANSPARENT);
//            window.setNavigationBarColor(Color.TRANSPARENT);
//        }
//
//        setContentView(R.layout.layout_activity_welcome);
//        ButterKnife.bind(this);
//
//        doStart();
//    }

    @Override
    public void setContentView(int layoutResID) {
        //设置状态栏透明
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
        super.setContentView(layoutResID);
    }

    @Override
    protected void initView() {
        mPresenter.attachView(this);
        doStart();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_welcome;
    }

    //开始
    private void doStart() {
        Intent intent = getIntent();
        String type = intent.getStringExtra("type");
//        handler.sendEmptyMessage(1);
        init();
        preference = getSharedPreferences("autoLogin", MODE_PRIVATE);
        String username = preference.getString("username", "");
        String password = preference.getString("password", "");

        //取消自动登录
//        handler.sendEmptyMessageDelayed(2, 3500);
        //自动登录
//        if (type == null) {
//            if (username.isEmpty()) {
//                handler.sendEmptyMessageDelayed(2,4000);
//            } else {
//                if (prove(username,password)) {
//                    login(username,password);
//                } else {
//                    handler.sendEmptyMessageDelayed(2,300);
//                }
//            }
//        } else {
//            handler.sendEmptyMessageDelayed(2,300);
//        }

        //自动登录
        if (type == null) {
            if (prove(username, password)) {
                login(username, password);
                return;
            }
        }
        handler.sendEmptyMessage(1);
        handler.sendEmptyMessageDelayed(2, 2000);
    }

    //初始化
    private void init() {
        controlKeyboardLayout(rlWelcome, btnLogin);

        Rect rect = new Rect();
        //获取root在窗体的可视区域
        rlWelcome.getWindowVisibleDisplayFrame(rect);
        windowHeight = rlWelcome.getRootView().getHeight();

        preference = getSharedPreferences("autoLogin", MODE_PRIVATE);
        editor = preference.edit();

        etUsername = tilUsername.getEditText();
        etPassword = tilPassword.getEditText();
        initEditTextListener();
        initEvent();

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "登录中...");
    }

    private void initEditTextListener() {
        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 20) {
                    tilUsername.setError("用户名不能超过20位");
                    tilUsername.setErrorEnabled(true);
                } else {
                    tilUsername.setErrorEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 20) {
                    tilPassword.setError("密码不能超过20位");
                    tilPassword.setErrorEnabled(true);
                } else {
                    tilPassword.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {

    }

    @OnClick({R.id.btn_login, R.id.btn_register, R.id.btn_forget_password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                //登录
                readyLogin();
                break;
            case R.id.btn_register:
                //注册
                readyGo(SMSCodeActivity.class);
//                Intent intent = new Intent(this, RegisterActivity.class);
//                intent.putExtra("phone", "15359600016");
//                application.getActivities().add(this);
//                startActivity(intent);
                break;
            case R.id.btn_forget_password:
                //忘记密码
                readyGo(ResetPasswordActivity.class);
                break;
            default:
                break;
        }
    }

    private String TAG = "Welcome";

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    // 建议这里往 SharePreference 里写一个成功设置的状态。
                    // 成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    // 延迟 60 秒来调用 Handler 设置别名
                    mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
                    break;
            }
        }
    };

    private static final int MSG_SET_ALIAS = 1001;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.d(TAG, "Set alias in handler.");
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
                default:
                    Log.i(TAG, "Unhandled msg - " + msg.what);
                    break;
            }
        }
    };

    private void initEvent() {
        //点击密码可见
        cbVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    etPassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    etPassword.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                }
                etPassword.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = etPassword.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });
    }

    //准备登录
    private void readyLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if (etUsername.isFocused()) {
            hideSoftInput(etUsername);
        } else {
            hideSoftInput(etPassword);
        }
        if (prove(username, password)) {
            mProgressDialog.show();
            login(username, password);
        }
    }

    //验证用户信息
    private boolean prove(String username, String password) {
        if (!username.isEmpty()) {
            if (username.length() <= 20) {
                if (!password.isEmpty()) {
                    if (password.length() <= 20) {
                        return true;
                    } else {
                        showSnackBarLong(rlWelcome, "密码不能超过20位");
                    }
                } else {
                    showSnackBarLong(rlWelcome, "请输入密码");
                }
            } else {
                showSnackBarLong(rlWelcome, "用户名不能超过20位");
            }
        } else {
            showSnackBarLong(rlWelcome, "请输入用户名");
        }
        return false;
    }

    //登录
    private void login(final String username, final String password) {
        //从服务器获取个人信息
//        getMyUserInfo(username, password);
        mPresenter.login(username, password);
    }

    //登录极光服务器
    private void JLogin(final String username, final String password) {
        JMessageClient.login(username, password, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Message msg = Message.obtain();
                if (i == 0) {
                    //登录成功
                    UserInfo myInfo = JMessageClient.getMyInfo();
                    if (myInfo != null) {
                        //设置别名
                        mHandler.sendMessage(mHandler
                                .obtainMessage(MSG_SET_ALIAS, username));
                        application.setMyInfo(myInfo);
                        //清空原有的自动登录信息
                        editor.clear();
                        editor.commit();
                        //记住现有的自动登录信息
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.commit();
//                        intentToActivity(MainActivity.class);
                        msg.what = HandlerKey.LOGIN_SUCCESS;
                    } else {
                        msg.obj = s;
                        msg.what = HandlerKey.LOGIN_FAIL;
                    }
                } else {
                    //登录失败
                    msg.obj = s;
                    msg.what = HandlerKey.LOGIN_FAIL;
                }
                handler.sendMessage(msg);
            }
        });
    }

    //从自己的服务器获取我的个人信息
    private void getMyUserInfo(final String username, String password) {
        RequestParams params = new RequestParams(URL + "Login");
        params.setConnectTimeout(8000);

        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Gson gson = new Gson();
                    Tb_user user = gson.fromJson(result, Tb_user.class);
                    application.setUser(user);
                    JLogin(user.getUsername(), user.getPassword());
//                    handler.sendEmptyMessage(HandlerKey.LOGIN_SUCCESS);
                } else {
                    mProgressDialog.cancel();
                    showSnackBarLong(rlWelcome, "用户名或密码错误");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtil.showToast(WelcomeActivity.this, "服务器连接失败，请检查网络", Toast.LENGTH_LONG);
                mProgressDialog.cancel();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //呼吸动画
    private void doBreath() {
        animator = ValueAnimator.ofInt(100, 130, 100);
        animator.setDuration(1600);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int curValue = (int) valueAnimator.getAnimatedValue();
                ivLogo.setScaleX(curValue / 100f);
                ivLogo.setScaleY(curValue / 100f);
                //loading字样透明度改变
                tvLoading.setAlpha(curValue / 150f);
            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    //气泡放大
    private void doAmplify() {
        Rect rect = new Rect();
        //获取root在窗体的可视区域
        rlWelcome.getWindowVisibleDisplayFrame(rect);
        windowHeight = rlWelcome.getRootView().getHeight();
        if (windowHeight > 1080) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }
        ivLogo.animate()
                .y((int) (windowHeight / 5.8))
                .scaleX(6.9f).scaleY(6.9f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setDuration(duration).start();
    }

    //气泡上下移动
    private void doMoveUpDown() {
        final int y = (int) ivLogo.getY();
        ValueAnimator animator = ValueAnimator.ofInt(y, windowHeight / 10, y);
        animator.setDuration(5000);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int curValue = (int) valueAnimator.getAnimatedValue();
                if (ivLogo != null) {
                    ivLogo.setY(curValue);
                }

            }
        });
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    //气泡左右循环移动
    private void doMoveLeftRight() {
        final int x = (int) ivLogo.getX();
        ValueAnimator animator = ValueAnimator.ofInt(x, 380, x + 70, x);
        animator.setDuration(8000);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int curValue = (int) valueAnimator.getAnimatedValue();
                if (ivLogo != null) {
                    ivLogo.setX(curValue);
                }

            }
        });
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    //显示控件
    private void showView() {
        tilUsername.setVisibility(View.VISIBLE);
        tilUsername.animate().translationY(-230)
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .start();

        rlPassword.setVisibility(View.VISIBLE);
        rlPassword.animate().translationY(-230)
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(150);

        tilPassword.setVisibility(View.VISIBLE);
        tilPassword.animate()
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(150);

        cbVisible.setVisibility(View.VISIBLE);
        cbVisible.animate()
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(150);

        btnRegister.setVisibility(View.VISIBLE);
        btnRegister.animate().translationX(270)
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(150);

        btnLogin.setVisibility(View.VISIBLE);
        btnLogin.animate().translationY(-230)
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(150);

        btnForgetPassword.setVisibility(View.VISIBLE);
        btnForgetPassword.animate().translationX(-240)
                .alpha(1)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(duration)
                .setStartDelay(150);
    }

    //隐藏loading字样
    private void hideView() {
        tvLoading.animate()
                .alpha(0)
                .translationY(50)
                .setInterpolator(new LinearOutSlowInInterpolator())
                .start();
        tvLoading.setVisibility(View.INVISIBLE);
    }

    /**
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     *                     addOnGlobalLayoutListener
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect rect = new Rect();
                        //获取root在窗体的可视区域
                        root.getWindowVisibleDisplayFrame(rect);
                        //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                        int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                        int scrollHeight = 0;
                        //若不可视区域高度大于100，则键盘显示
                        if (rootInvisibleHeight > 100) {
                            int[] location = new int[2];
                            //获取scrollToView在窗体的坐标
                            scrollToView.getLocationInWindow(location);
                            //计算root滚动高度，使scrollToView在可见区域
                            scrollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                            root.animate()
                                    .translationY(-scrollHeight - 10)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .start();
                        } else {
                            //键盘隐藏
                            root.animate()
                                    .translationY(scrollHeight)
                                    .setInterpolator(new DecelerateInterpolator())
                                    .start();
                        }
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    public void response(boolean success, Tb_user user) {
        if (success) {
            application.setUser(user);
            JLogin(user.getUsername(), user.getPassword());
        } else {
            mProgressDialog.cancel();
            showSnackBarLong(rlWelcome, "用户名或密码错误");
        }
    }
}
