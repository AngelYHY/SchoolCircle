package com.myschool.schoolcircle.ui.activity.welcome;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.api.BasicCallback;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.mob.tools.utils.R.getStringRes;

public class ResetPasswordActivity extends BaseActivity {

    @Bind(R.id.rl_resetpassword)
    RelativeLayout rlResetpassword;
    @Bind(R.id.tb_resetpassword)
    Toolbar tbResetpassword;
    @Bind(R.id.rl_reset_resetpswd)
    RelativeLayout rlResetpswd;
    @Bind(R.id.til_reset_phone)
    TextInputLayout tilPhone;
    @Bind(R.id.et_reset_phone)
    EditText etPhone;
    @Bind(R.id.til_reset_newpswd)
    TextInputLayout tilNewpswd;
    @Bind(R.id.et_reset_newpassword)
    EditText etNewpassword;
    @Bind(R.id.ll_reset_verification)
    LinearLayout llVerification;
    @Bind(R.id.til_reset_verification)
    TextInputLayout tilVerification;
    @Bind(R.id.et_reset_verification)
    EditText etVerification;
    @Bind(R.id.btn_reset_getverification)
    Button btnGetVerification;
    @Bind(R.id.cb_visible)
    CheckBox cbVisible;
    @Bind(R.id.tv_reset_tip)
    TextView tvTip;
    @Bind(R.id.btn_reset_resetpswd)
    Button btnResetpswd;

    private int time = 60;
    private static String iPhone;
    private boolean flag = true;
    private String iCord;
    private String newPassword;
    private String phone;

    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPDATE_SUCCESS:
                    mProgressDialog.cancel();
                    showSnackBarLong(rlResetpassword, "重置密码成功");

                    break;
                case HandlerKey.UPDATE_FAIL:
                    mProgressDialog.cancel();
                    showSnackBarLong(rlResetpassword, "重置密码失败");
                    break;
                default:
                    break;
            }
        }
    };
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_reset_password);
//        ButterKnife.bind(this);
//
//        initView();
//
//        SMSSDK.initSDK(this, "154be235e1686", "00aa5165076d86ed2b2f43862c998ab8");
//        EventHandler eh = new EventHandler() {
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                handler.sendMessage(msg);
//            }
//        };
//        SMSSDK.registerEventHandler(eh);
//
//    }

    protected void initView() {
        setSupportActionBar(tbResetpassword);
        getSupportActionBar().setHomeButtonEnabled(true);//设置标题栏的按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this,"重置密码中...");

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 11) {
                    tilPhone.setError("电话号码不能超过11位");
                    tilPhone.setErrorEnabled(true);//显示提示语
                } else {
                    tilPhone.setErrorEnabled(false);//隐藏提示语
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        etNewpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    tilNewpswd.setHint("新密码");
                    tilNewpswd.setErrorEnabled(false);
                }
                if (s.length() > 16) {
                    tilNewpswd.setError("密码不能超过16位");
                    tilNewpswd.setErrorEnabled(true);//显示提示语

                } else if (s.length() < 6) {
                    tilNewpswd.setError("密码不能少于6位");
                    tilNewpswd.setErrorEnabled(true);

                } else {
                    tilNewpswd.setHint("请输入6~16位之间的密码");
                    tilNewpswd.setErrorEnabled(false);//隐藏提示语
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //点击密码可见
        cbVisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    etNewpassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                } else {
                    etNewpassword.setTransformationMethod(
                            PasswordTransformationMethod.getInstance());
                }
                etNewpassword.postInvalidate();
                //切换后将EditText光标置于末尾
                CharSequence charSequence = etNewpassword.getText();
                if (charSequence instanceof Spannable) {
                    Spannable spanText = (Spannable) charSequence;
                    Selection.setSelection(spanText, charSequence.length());
                }
            }
        });

        etVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4) {
                    tilVerification.setError("验证码不能超过4位");
                    tilVerification.setErrorEnabled(true);//显示提示语
                } else {
                    tilVerification.setErrorEnabled(false);//隐藏提示语
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        SMSSDK.initSDK(this, "154be235e1686", "00aa5165076d86ed2b2f43862c998ab8");
        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_reset_password;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            Log.e("event", "result= " + result);
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功,验证通过 才进行这一步
                    showResetDialog();
                    handlerText.sendEmptyMessage(2);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //服务器验证码发送成功
                    Snackbar.make(rlResetpassword, "验证码已发送", Snackbar.LENGTH_LONG).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("event", "flag= " + flag);
                if (flag) {
                    btnGetVerification.setVisibility(View.VISIBLE);
                    Snackbar.make(rlResetpassword, "验证码获取失败，请重新获取", Snackbar.LENGTH_LONG).show();
                    tvTip.setVisibility(View.GONE);
                    etVerification.requestFocus();
                } else {
                    ((Throwable) data).printStackTrace();
                    int resId = getStringRes(ResetPasswordActivity.this, "smssdk_network_error");
                    Snackbar.make(rlResetpassword, "验证码错误", Snackbar.LENGTH_LONG).show();
                    etVerification.selectAll();
                    if (resId > 0) {
                        Toast.makeText(ResetPasswordActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //验证码送成功后提示文字
    private void reminderText() {
        tvTip.setVisibility(View.VISIBLE);
        handlerText.sendEmptyMessageDelayed(1, 1000);
    }

    Handler handlerText = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                if (time > 0) {
                    tvTip.setText("验证码已发送" + time + "秒");
                    time--;
                    handlerText.sendEmptyMessageDelayed(1, 1000);
                } else {
                    tvTip.setText("提示信息");
                    time = 60;
                    tvTip.setVisibility(View.GONE);
                    btnGetVerification.setVisibility(View.VISIBLE);
                }
            } else {
                etVerification.setText("");
                tvTip.setText("提示信息");
                time = 60;
                tvTip.setVisibility(View.GONE);
                btnGetVerification.setVisibility(View.VISIBLE);
            }
        }
    };

    @OnClick({R.id.btn_reset_getverification, R.id.btn_reset_resetpswd})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reset_getverification:

                phone = etPhone.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)) {
                    if (phone.length() == 11) {
                        SMSSDK.getVerificationCode("86", phone);
                        etVerification.requestFocus();
                        btnGetVerification.setVisibility(View.GONE);
                        reminderText();
                    } else {
                        Snackbar.make(rlResetpassword, "请输入正确的手机号码", Snackbar.LENGTH_LONG).show();
                        etPhone.requestFocus();
                    }
                } else {
                    Snackbar.make(rlResetpassword, "请输入您的手机号码", Snackbar.LENGTH_LONG).show();
                    etPhone.requestFocus();
                }
                hideSoftInput(etPhone);
                hideSoftInput(etVerification);
                break;


            case R.id.btn_reset_resetpswd:

                newPassword = etNewpassword.getText().toString().trim();
                phone = etPhone.getText().toString().trim();
                String verification = etVerification.getText().toString().trim();
                if (!TextUtils.isEmpty(newPassword)
                        && !TextUtils.isEmpty(verification)
                        && !TextUtils.isEmpty(phone)) {
                    if (verification.length() == 4) {
                        iCord = verification;
                        SMSSDK.submitVerificationCode("86", phone, iCord);
                        flag = false;
                    } else {
                        Snackbar.make(rlResetpassword, "请输入正确的验证码", Snackbar.LENGTH_SHORT).show();
                        etVerification.requestFocus();
                    }
                } else {
                    Snackbar.make(rlResetpassword, "请输入手机号码,密码和验证码", Snackbar.LENGTH_SHORT).show();
                    etPhone.requestFocus();
                }
                hideSoftInput(etPhone);
                hideSoftInput(etVerification);
                hideSoftInput(etNewpassword);
                break;
        }
    }

    //显示提示文字对话框
    private void showResetDialog() {
        new AlertDialog.Builder(this)
                .setTitle("确认")
                .setMessage("确定重置密码吗？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newPassword = etNewpassword.getText().toString().trim();
                        modifyPassword(phone,newPassword);

                    }
                }).show();
    }

    //修改密码
    private void modifyPassword(String phone, final String newPassword) {
        mProgressDialog.show();
        RequestParams params = new RequestParams(URL + "Mine");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type","password");
        params.addBodyParameter("phone", phone);
        params.addBodyParameter("newPassword", newPassword);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Log.i("ResetPasswordActivity", " onSuccess: 重置密码成功 ");
                    Gson gson = new Gson();
                    Tb_user user = gson.fromJson(result,Tb_user.class);
                    JModifyPassword(user.getPassword(),newPassword);
                } else {
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                    Log.i("ResetPasswordActivity", "onFail: 重置密码失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //修改极光服务器上用户的密码
    private void JModifyPassword(String oldPassword,String newPassword) {
        JMessageClient.updateUserPassword(oldPassword, newPassword, new BasicCallback() {
            @Override
            public void gotResult(int i, String s) {
                if (i == 0) {
                    //修改成功
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                } else {
                    mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {

    }
}