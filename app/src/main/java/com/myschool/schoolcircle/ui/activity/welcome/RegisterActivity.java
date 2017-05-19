package com.myschool.schoolcircle.ui.activity.welcome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_school;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.ui.activity.MainActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.api.BasicCallback;

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.tb_register)
    Toolbar tbRegister;
    @Bind(R.id.et_username)
    EditText etUsername;
    @Bind(R.id.til_username)
    TextInputLayout tilUsername;
    @Bind(R.id.et_password)
    EditText etPassword;
    @Bind(R.id.til_password)
    TextInputLayout tilPassword;
    @Bind(R.id.btn_register)
    Button btnRegister;
    @Bind(R.id.cl_register)
    CoordinatorLayout clRegister;
    @Bind(R.id.cb_visible)
    CheckBox cbVisible;

    private ProgressDialog dialog;
    private String phone;
    private Tb_school school;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerKey.J_REGISTER_SUCCESS:
                    //若极光注册成功，则进行极光登录
                    JLogin((Tb_user) msg.obj);
                    break;
                case HandlerKey.J_REGISTER_FAIL:
                    //若极光注册失败，则数据库事务回滚
                    dialog.cancel();
                    showSnackBarLong(clRegister, R.string.register_fail);
                    break;
                case HandlerKey.J_LOGIN_SUCCESS:
                    //极光登录成功，进入主界面
                    dialog.cancel();
                    readyGoThenKill(MainActivity.class);
                    break;
                case HandlerKey.J_LOGIN_FAIL:
                    //极光登录失败
                    showSnackBarLong(clRegister, R.string.login_fail);
                    dialog.cancel();
                    break;
                case HandlerKey.TIMEOUT:
                    //注册超时
                    showSnackBarLong(clRegister, R.string.register_timeout);
                    dialog.cancel();
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_register);
//        ButterKnife.bind(this);
//
//        init();
//    }


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_register;
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        //注册界面无需具体实现此方法，空实现即可
    }

    protected void initView() {
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        setSupportActionBar(tbRegister);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etUsername = tilUsername.getEditText();
        etPassword = tilPassword.getEditText();

        dialog = new ProgressDialog(this);
        dialog.setMessage("注册中，请稍后...");
        dialog.setCanceledOnTouchOutside(false);

        etUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    tilUsername.setHint("用户名");
                    tilUsername.setErrorEnabled(false);
                }
                if (charSequence.length() > 20) {
                    tilUsername.setError("用户名不能超过20位");
                    tilUsername.setErrorEnabled(true);

                } else if (charSequence.length() < 6) {
                    tilUsername.setError("用户名不能少于6位");
                    tilUsername.setErrorEnabled(true);

                } else {
                    tilUsername.setHint("请输入6~20位之间的用户名");
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

    @OnClick({R.id.btn_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_register:
                //注册
                String username = etUsername.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                if (prove(username, password)) {
                    //验证通过
                    dialog.show();
                    //先向自己的服务器进行注册
                    doRegister(phone, username, password);
                }
                hideSoftInput(etUsername);
                hideSoftInput(etPassword);
                break;
            default:
                break;
        }
    }

    //验证信息
    private boolean prove(String username, String password) {
        if (!password.isEmpty() && !username.isEmpty()) {
            if (username.length() <= 20) {
                if (password.length() <= 20) {
                    //验证通过
                    return true;
                } else {
                    showSnackBarLong(clRegister,"密码不能超过20位");
                    return false;
                }
            } else {
                showSnackBarLong(clRegister,"用户名不能超过20位");
                return false;
            }
        } else {
            showSnackBarLong(clRegister, "用户名或密码不能为空");
            return false;
        }
    }

    //注册
    private void doRegister(String phone, String username, String password) {
        doPost("Register", phone, username, password);
    }

    //向服务器发送post请求
    private void doPost(String type, String phone, String username, String password) {
        RequestParams params = new RequestParams(URL + type);
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "register");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("username", username);
        params.addBodyParameter("password", password);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                proveResult(result);
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

    //验证注册post请求结果
    private void proveResult(String result) {
        if (!"fail".equals(result)) {
            //数据库验证用户信息成功
            Gson gson = new Gson();
            Tb_user user = gson.fromJson(result, Tb_user.class);
            JRegister(user);
        } else {
            dialog.cancel();
            showSnackBarLong(clRegister, R.string.register_fail);
        }
    }

    //极光注册
    private void JRegister(final Tb_user user) {
        JMessageClient.register(user.getUsername(), user.getPassword(),
                new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        Message msg = Message.obtain();
                        if (i == 0) {
                            msg.obj = user;
                            msg.what = HandlerKey.J_REGISTER_SUCCESS;
                        } else {
                            msg.what = HandlerKey.J_REGISTER_FAIL;
                        }
                        handler.sendMessage(msg);
                    }
                });
    }

    //极光登录
    private void JLogin(final Tb_user user) {
        JMessageClient.login(user.getUsername(), user.getPassword(), new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                Message msg = Message.obtain();
                if (i == 0) {
                    msg.obj = user;
                    application.setMyInfo(JMessageClient.getMyInfo());
                    msg.what = HandlerKey.J_LOGIN_SUCCESS;
                } else {
                    msg.what = HandlerKey.J_LOGIN_FAIL;
                }
                handler.sendMessage(msg);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
