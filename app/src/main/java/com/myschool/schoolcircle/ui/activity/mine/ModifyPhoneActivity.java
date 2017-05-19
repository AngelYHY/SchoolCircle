package com.myschool.schoolcircle.ui.activity.mine;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import static com.mob.tools.utils.R.getStringRes;

public class ModifyPhoneActivity extends BaseActivity {

    @Bind(R.id.rl_modifyphone)
    RelativeLayout rlModifyphone;
    @Bind(R.id.tb_modifyphone)
    Toolbar tbModifyphone;
    @Bind(R.id.tv_modify_oldphone)
    TextView tvOldphone;
    @Bind(R.id.rl_modify_phone)
    RelativeLayout rlPhone;
    @Bind(R.id.til_modify_newphone)
    TextInputLayout tilNewphone;
    @Bind(R.id.et_modify_newphone)
    EditText etNewphone;
    @Bind(R.id.til_modify_verification)
    TextInputLayout tilVerification;
    @Bind(R.id.et_modify_verification)
    EditText etVerification;
    @Bind(R.id.btn_modify_getverification)
    Button btnGetVerification;
    @Bind(R.id.tv_modify_tip)
    TextView tvTip;
    @Bind(R.id.btn_modify_confirm)
    Button btnConfirm;

    private int time = 60;
    private static String Phone;
    private boolean flag = true;
    private String iCord;
    private String oldPhone;
    private String newPhone;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPDATE_SUCCESS:
                    showSnackBarLong(rlModifyphone, "修改手机号成功");

                    break;
                case HandlerKey.UPDATE_FAIL:
                    showSnackBarLong(rlModifyphone, "修改手机号失败");

                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_modify_phone);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
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
                    showConfirDialog();
                    handlerText.sendEmptyMessage(2);
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //服务器验证码发送成功
                    Snackbar.make(rlModifyphone, "验证码已发送", Snackbar.LENGTH_LONG).show();
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                    //返回支持发送验证码的国家列表
                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("event", "flag= " + flag);
                if (flag) {
                    btnGetVerification.setVisibility(View.VISIBLE);
                    Snackbar.make(rlModifyphone, "验证码获取失败，请重新获取", Snackbar.LENGTH_LONG).show();
                    tvTip.setVisibility(View.GONE);
                    etNewphone.requestFocus();
                } else {
                    ((Throwable) data).printStackTrace();
                    int resId = getStringRes(ModifyPhoneActivity.this, "smssdk_network_error");
                    Snackbar.make(rlModifyphone, "验证码错误", Snackbar.LENGTH_LONG).show();
                    etVerification.selectAll();
                    if (resId > 0) {
                        Toast.makeText(ModifyPhoneActivity.this, resId, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
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

    protected void initView() {
        setSupportActionBar(tbModifyphone);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etNewphone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 11) {
                    tilNewphone.setError("手机号码不能超过11位");
                    tilNewphone.setErrorEnabled(true);//显示提示语
                } else {
                    tilNewphone.setErrorEnabled(false);//隐藏提示语
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
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

        initData();

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
        return R.layout.layout_activity_modify_phone;
    }

    //获取原本的手机号
    private void initData() {
        oldPhone = application.getUser().getPhone();
        tvOldphone.setText(oldPhone);
    }

    @OnClick({R.id.btn_modify_getverification, R.id.btn_modify_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_modify_getverification:
                newPhone = etNewphone.getText().toString().trim();
                if (!TextUtils.isEmpty(newPhone)) {
                    if (newPhone.length() == 11) {
                        if (!newPhone.equals(oldPhone)) {
                            Phone = etNewphone.getText().toString().trim();
                            SMSSDK.getVerificationCode("86", Phone);
                            etVerification.requestFocus();
                            btnGetVerification.setVisibility(View.GONE);
                            reminderText();
                        } else {
                            Snackbar.make(rlModifyphone, "输入的手机与旧手机号一致，请重输", Snackbar.LENGTH_LONG).show();
                            etNewphone.requestFocus();
                        }
                    } else {
                        Snackbar.make(rlModifyphone, "请输入正确的手机号码", Snackbar.LENGTH_LONG).show();
                        etNewphone.requestFocus();
                    }
                } else {
                    Snackbar.make(rlModifyphone, "请输入您的手机号码", Snackbar.LENGTH_LONG).show();
                    etNewphone.requestFocus();
                }
                break;

            case R.id.btn_modify_confirm:
                newPhone = etNewphone.getText().toString().trim();
                String verification = etVerification.getText().toString().trim();
                if (!TextUtils.isEmpty(newPhone) && !TextUtils.isEmpty(verification)) {
                    if (verification.length() == 4) {
                        iCord = verification;
                        SMSSDK.submitVerificationCode("86", Phone, iCord);
                        flag = false;
                    } else {
                        Snackbar.make(rlModifyphone, "请输入正确的验证码", Snackbar.LENGTH_SHORT).show();
                        etVerification.requestFocus();
                    }
                } else {
                    Snackbar.make(rlModifyphone, "请输入手机号码和验证码", Snackbar.LENGTH_SHORT).show();
                    etNewphone.requestFocus();
                }
                break;
        }
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

    //显示提示文字对话框
    private void showConfirDialog() {

        new AlertDialog.Builder(this)
                .setTitle("确认")
                .setMessage("确定修改手机号码吗？")
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            newPhone = etNewphone.getText().toString().trim();
                            modifyphone(newPhone);
                            mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                        } else {
                            mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                        }
                    }
                }).show();
    }

    private void modifyphone(String update) {
        RequestParams params = new RequestParams(URL + "Mine");
        params.setConnectTimeout(8000);
        params.addBodyParameter("username", application.getUser().getUsername());
        params.addBodyParameter("update", update);
        params.addBodyParameter("target", "phone");

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    Log.i("ModifyPhoneActivity", " onSuccess: 更新手机号成功 ");
                } else {
                    Log.i("ModifyPhoneActivity", "onFail: 更新手机号失败");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }
}