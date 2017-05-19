package com.myschool.schoolcircle.ui.activity.welcome;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.main.R;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.mob.tools.utils.R.getStringRes;

public class SMSCodeActivity extends BaseActivity
        implements View.OnClickListener {

    @Bind(R.id.tb_smscode)
    Toolbar tbSmscode;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.til_phone)
    TextInputLayout tilPhone;
    @Bind(R.id.et_cord)
    EditText etCord;
    @Bind(R.id.til_sms_cord)
    TextInputLayout tilSmsCord;
    @Bind(R.id.btn_get_cord)
    Button btnGetCord;
    @Bind(R.id.tv_tip)
    TextView tvTip;
    @Bind(R.id.btn_prove)
    Button btnProve;
    @Bind(R.id.verificationLayout)
    LinearLayout verificationLayout;

    private static String iPhone;
    private String iCord;
    private int time = 60;
    private boolean flag = true;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_smscode);
//        ButterKnife.bind(this);
//
//        init();
//
//        SMSSDK.initSDK(this, "154be235e1686", "00aa5165076d86ed2b2f43862c998ab8");
//        EventHandler eh = new EventHandler() {
//
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//
//                Message msg = new Message();
//                msg.arg1 = event;
//                msg.arg2 = result;
//                msg.obj = data;
//                handler.sendMessage(msg);
//            }
//
//        };
//        SMSSDK.registerEventHandler(eh);
//
//    }

    protected void initView() {
        setSupportActionBar(tbSmscode);
        getSupportActionBar().setHomeButtonEnabled(true);//设置标题栏的按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

        etCord.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 4) {
                    tilSmsCord.setError("验证码不能超过4位");
                    tilSmsCord.setErrorEnabled(true);//显示提示语
                } else {
                    tilSmsCord.setErrorEnabled(false);//隐藏提示语
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnGetCord.setOnClickListener(this);
        btnProve.setOnClickListener(this);

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
    public void onEvent(LoginStateChangeEvent event) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_get_cord:
                if (!TextUtils.isEmpty(etPhone.getText().toString().trim())) {
                    if (etPhone.getText().toString().trim().length() == 11) {
                        iPhone = etPhone.getText().toString().trim();
                        SMSSDK.getVerificationCode("86", iPhone);
                        etCord.requestFocus();
                        btnGetCord.setVisibility(View.GONE);
                        hideSoftInput(etPhone);
                        reminderText();
                    } else {
                        Snackbar.make(verificationLayout, "请输入正确的电话号码", Snackbar.LENGTH_LONG).show();
                        etPhone.requestFocus();
                    }
                } else {
                    Snackbar.make(verificationLayout, "请输入您的电话号码", Snackbar.LENGTH_LONG).show();
                    etPhone.requestFocus();
                }
                break;

            case R.id.btn_prove:
                if (!TextUtils.isEmpty(etCord.getText().toString().trim())) {
                    if (etCord.getText().toString().trim().length() == 4) {
                        iCord = etCord.getText().toString().trim();
                        SMSSDK.submitVerificationCode("86", iPhone, iCord);
                        flag = false;
                    } else {
                        Snackbar.make(verificationLayout, "请输入正确的验证码", Snackbar.LENGTH_LONG).show();
                        etCord.requestFocus();
                    }
                } else {
                    Snackbar.make(verificationLayout, "请输入验证码", Snackbar.LENGTH_LONG).show();
                    etCord.requestFocus();
                }
                break;

            default:
                break;
        }
    }

    //标题栏的监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //关闭当前界面
            case android.R.id.home:
                finish();
                break;

            default:
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
                    btnGetCord.setVisibility(View.VISIBLE);
                }
            } else {
                etCord.setText("");
                tvTip.setText("提示信息");
                time = 60;
                tvTip.setVisibility(View.GONE);
                btnGetCord.setVisibility(View.VISIBLE);
            }
        }

        ;
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {

                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功,验证通过

                    //打开注册页面
                    Intent intent = new Intent(SMSCodeActivity.this,RegisterActivity.class);
                    intent.putExtra("phone",iPhone);
                    startActivity(intent);
                    finish();
                    handlerText.sendEmptyMessage(2);

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//服务器验证码发送成功
//                    reminderText();
                    Snackbar.make(verificationLayout, "验证码已发送", Snackbar.LENGTH_LONG).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (flag) {
                    btnGetCord.setVisibility(View.VISIBLE);
                    Snackbar.make(verificationLayout, "验证码获取失败，请重新获取", Snackbar.LENGTH_LONG).show();
                    etPhone.requestFocus();
                } else {
                    ((Throwable) data).printStackTrace();
                    int resId = getStringRes(SMSCodeActivity.this, "smssdk_network_error");
                    Snackbar.make(verificationLayout, "验证码错误", Snackbar.LENGTH_LONG).show();
                    etCord.selectAll();
                    if (resId > 0) {
                        Toast.makeText(SMSCodeActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }


    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_smscode;
    }

}
