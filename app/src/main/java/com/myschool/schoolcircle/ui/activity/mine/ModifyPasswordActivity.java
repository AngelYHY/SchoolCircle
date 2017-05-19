package com.myschool.schoolcircle.ui.activity.mine;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.api.BasicCallback;

public class ModifyPasswordActivity extends BaseActivity {

    @Bind(R.id.rl_modifypassword)
    RelativeLayout rlModifypassword;
    @Bind(R.id.tb_modifypassword)
    Toolbar tbModifypassword;
    @Bind(R.id.til_modify_oldpassword)
    TextInputLayout tilOldpassword;
    @Bind(R.id.et_modify_oldpassword)
    EditText etOldpassword;
    @Bind(R.id.til_modify_newpassword)
    TextInputLayout tilNewpassword;
    @Bind(R.id.et_modify_newpassword)
    EditText etNewpassword;
    @Bind(R.id.iv_modify_visible)
    ImageView ivVisible;
    @Bind(R.id.btn_modify_confirmpswd)
    Button btnResetpswd;

    private String oldPassword;
    private String newPassword;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPDATE_SUCCESS:
                    showSnackBarLong(rlModifypassword, "修改密码成功");

                    break;
                case HandlerKey.UPDATE_FAIL:
                    showSnackBarLong(rlModifypassword, "修改密码失败");

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
//        setContentView(R.layout.layout_activity_modify_password);
//        ButterKnife.bind(this);
//
//        initView();
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        setSupportActionBar(tbModifypassword);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etOldpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    tilOldpassword.setHint("旧密码");
                    tilOldpassword.setErrorEnabled(false);
                }
                if (s.length() > 16) {
                   tilOldpassword.setError("密码不能超过16位");
                   tilOldpassword.setErrorEnabled(true);//显示提示语

                } else if (s.length() < 6) {
                    tilOldpassword.setError("密码不能少于6位");
                    tilOldpassword.setErrorEnabled(true);

                } else {
                    tilOldpassword.setHint("请输入6~16位之间的密码");
                    tilOldpassword.setErrorEnabled(false);//隐藏提示语
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
                    tilNewpassword.setHint("新密码");
                    tilNewpassword.setErrorEnabled(false);
                }
                if (s.length() > 16) {
                    tilNewpassword.setError("密码不能超过16位");
                    tilNewpassword.setErrorEnabled(true);//显示提示语

                } else if (s.length() < 6) {
                    tilNewpassword.setError("密码不能少于6位");
                    tilNewpassword.setErrorEnabled(true);

                } else {
                    tilNewpassword.setHint("请输入6~16位之间的密码");
                    tilNewpassword.setErrorEnabled(false);//隐藏提示语
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //点击密码可见
        ivVisible.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    etNewpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                } else {
                    etNewpassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                }
                return true;
            }
        });
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_modify_password;
    }


    @OnClick(R.id.btn_modify_confirmpswd)
    public void onClick() {
        oldPassword = application.getUser().getPassword();//用户的旧密码
        String etoldpassword = etOldpassword.getText().toString().trim();//输入的旧密码
        newPassword = etNewpassword.getText().toString().trim();//输入的新密码

        //先判断两次密码是否为空，不为空，进行下一层判断
        if (!TextUtils.isEmpty(etoldpassword) && !TextUtils.isEmpty(newPassword)) {
            //输入的旧密码和旧密码不一致
            if (!etoldpassword.equals(oldPassword)) {
                Snackbar.make(rlModifypassword, "旧密码不一致，请重新输入", Snackbar.LENGTH_LONG).show();
            } else if (newPassword.equals(etoldpassword)) {
                Snackbar.make(rlModifypassword, "你两次输入的密码一致，请重新输入", Snackbar.LENGTH_LONG).show();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("确定修改密码吗？")
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                JMessageClient.updateUserPassword(oldPassword, newPassword, new BasicCallback() {
                                    @Override
                                    public void gotResult(int i, String s) {
                                        if (i == 0) {
                                            modifypswd(newPassword, "password");
                                            mHandler.sendEmptyMessage(HandlerKey.UPDATE_SUCCESS);
                                        } else {
                                            mHandler.sendEmptyMessage(HandlerKey.UPDATE_FAIL);
                                        }
                                    }
                                });
                            }
                        }).show();
            }
        } else {
            ToastUtil.showToast(this, "密码为空，请输入密码", Toast.LENGTH_SHORT);
        }
    }

    //向服务器更新密码
    private void modifypswd(String update, String target) {
        RequestParams params = new RequestParams(URL + "Mine");
        params.setConnectTimeout(8000);
        params.addBodyParameter("username", application.getUser().getUsername());
        params.addBodyParameter("password", application.getUser().getPassword());
        params.addBodyParameter("update", update);
        params.addBodyParameter("target", target);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    Log.i("ModifyPasswordActivity", "onSuccess: 更新密码成功");
                } else {
                    Log.i("ModifyPasswordActivity", "onFail: 更新密码失败");
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

}