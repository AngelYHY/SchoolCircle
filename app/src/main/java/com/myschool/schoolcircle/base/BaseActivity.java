package com.myschool.schoolcircle.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.myschool.schoolcircle.config.Config;
import com.myschool.schoolcircle.injector.component.ActivityComponent;
import com.myschool.schoolcircle.injector.component.DaggerActivityComponent;
import com.myschool.schoolcircle.injector.module.ActivityModule;
import com.myschool.schoolcircle.ui.activity.welcome.WelcomeActivity;
import com.orhanobut.logger.Logger;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import free.free.R;
import free.free.base.BaseAppCompatActivity;
import free.free.base.IView;
import free.free.util.ToastUtils;
import free.free.util.netstatu.NetUtils;
import free.free.widget.EmptyView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/18 0018
 * github：
 */

public abstract class BaseActivity extends BaseAppCompatActivity implements IView, View.OnClickListener {

    protected MaterialDialog dialog;
    protected ActivityComponent mActivityComponent;
    protected EmptyView mEmptyView;
    public int page = 1;
    protected static final int PAGE_SIZE = 3;

    protected static AppApplication application;
    protected final String URL = Config.URL;

    @Override
    public <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    @Override
    protected void initInjector() {
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(((AppApplication) getApplication()).getApplicationComponent())
                .build();
        init();
    }


    private void init() {
        application = (AppApplication) getApplication();
    }

    //登录状态改变监听
    public abstract void onEvent(LoginStateChangeEvent event);

    //登录状态改变时事件
    protected void eventAction(LoginStateChangeEvent event, final Context context) {
        LoginStateChangeEvent.Reason reason = event.getReason();//获取变更的原因
        UserInfo myInfo = event.getMyInfo();//获取当前被登出账号的信息
        switch (reason) {
            case user_password_change:
                //用户密码在服务器端被修改
                new AlertDialog.Builder(context).setTitle("提醒")
                        .setMessage("您的密码已在服务器端被修改，请注意是否为本人操作。")
                        .setCancelable(false)
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(
                                        BaseActivity.this, WelcomeActivity.class);
                                intent.putExtra("type", "logout");
                                startActivity(intent);
                                logout(context);
                                finish();
                            }
                        }).show();
                break;
            case user_logout:
                //用户换设备登录
                new AlertDialog.Builder(context).setTitle("提醒")
                        .setMessage("您的账号已在其他设备上登录，请注意是否为本人操作。")
                        .setCancelable(false)
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(
                                        BaseActivity.this, WelcomeActivity.class);
                                intent.putExtra("type", "logout");
                                startActivity(intent);
                                logout(context);
                                finish();
                            }
                        }).show();
                break;
            case user_deleted:
                //用户被删除
                new AlertDialog.Builder(context).setTitle("提醒")
                        .setMessage("很抱歉，您的账号已被删除")
                        .setCancelable(false)
                        .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(
                                        BaseActivity.this, WelcomeActivity.class);
                                intent.putExtra("type", "logout");
                                startActivity(intent);
                                logout(context);
                                finish();
                            }
                        }).show();
                break;
        }
    }

    //退出登录
    protected void logout(Context context) {
        application.setUser(null);
        //事件接收类的解绑
        JMessageClient.unRegisterEventReceiver(context);
        JMessageClient.logout();//退出极光登录
    }

    protected void showSnackBarLong(View view, int resId) {
//        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction(R.string.get_it, this).show();
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).setAction("知道了", this).show();
    }

    protected void showSnackBarLong(View view, CharSequence text) {
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("知道了", this).show();
    }

    protected void showSnackBarShort(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_SHORT).setAction("知道了", this).show();
    }

    protected void showSnackBarShort(View view, CharSequence text) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).setAction("知道了", this).show();
    }

    //强制显示软键盘
    protected void showSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    protected void hideSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {

    }


    @Override
    protected void onNetworkConnected(NetUtils.NetType type) {
        showMsg("请注意，网络连接已连接---" + type);
    }

    @Override
    protected void onNetworkDisConnected() {
        showMsg("请注意，网络连接已断开");
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return true;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.FADE;
    }

//    @Override
//    public void showNetError() {
//        toggleNetworkError(true, null);
//    }

    @Override
    public void showLoading() {
        if (mEmptyView != null) {
            mEmptyView.showLoading();
        }
    }

    @Override
    public void hide() {
        if (mEmptyView != null) {
            mEmptyView.hide();
        }
    }

    @Override
    public void showMsg(String msg) {
//        ToastUtils.shortToast(getApplication(), msg);
        ToastUtils.shortToast(getApplication(), msg);
    }

    @Override
    public void showException(Throwable ex, int flag) {
        //toggleShowError(true, msg, null);
        if (flag == 1) {
            showFail(1);
        } else if (flag == 2) {
            showFail(2);
            return;
        }
        if (ex instanceof Exception) {
            //无网络
            if (ex instanceof UnknownHostException) {
                if (mEmptyView != null) {
                    mEmptyView.showError("网络异常");
                } else {
                    showMsg("网络异常");
                }
            } else if (ex instanceof SocketTimeoutException) {
                showMsg(mContext.getString(R.string.common_error_service) + "具体原因：连接超时");
            } else {
                showMsg(mContext.getString(R.string.common_error_service) + "具体原因：" + ex.getMessage());
            }
            Logger.e(ex.getMessage());
        }
    }

    @Override
    public void showEmpty() {
        if (mEmptyView != null) {
            mEmptyView.showEmpty();
        }
    }

    @Override
    public void showEmpty(String msg) {
        if (mEmptyView != null) {
            mEmptyView.showEmpty();
        }
    }

    @Override
    public void showDialog() {
        showDialog(getString(R.string.loading_default_text));
    }

    @Override
    public void showDialog(String content) {
        if (dialog == null) {
            dialog = new MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .content(content)
                    .canceledOnTouchOutside(false)
                    .build();
            dialog.show();
        } else {
            dialog.setContent(content);
            dialog.show();
        }
    }

    @Override
    public void showFail(int flag) {

    }

    @Override
    public void hideDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIPresenter != null) {
            mIPresenter.detachView();
        }
    }
}
