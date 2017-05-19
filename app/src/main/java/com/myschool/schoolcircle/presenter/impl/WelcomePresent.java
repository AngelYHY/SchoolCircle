package com.myschool.schoolcircle.presenter.impl;

import android.app.Activity;
import android.content.Context;

import com.myschool.schoolcircle.bean.LoginResponse;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.injector.ContextLife;
import com.myschool.schoolcircle.presenter.WelcomePresenter;
import com.myschool.schoolcircle.ui.activity.welcome.WelcomeActivity;
import com.myschool.schoolcircle.view.WelcomeView;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import free.free.base.BasePresenter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/18 0018
 * github：
 */

public class WelcomePresent extends BasePresenter<WelcomeView> implements WelcomePresenter {

    private final WelcomeActivity mActivity;
    private final RetrofitService mService;

    @Inject
    WelcomePresent(RetrofitService service, Activity activity, @ContextLife("Application") Context context) {
        mActivity = (WelcomeActivity) activity;
        mService = service;
    }


    @Override
    public void login(String name, String psw) {
        Logger.e(name + "--" + psw);
        mService.login(name, psw)
                .compose(mView.<LoginResponse>applySchedulers())
                .compose(mActivity.<LoginResponse>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<LoginResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(LoginResponse result) {
                        if (result.isSuccess()) {
                            mView.response(true, result.getUse());
                        } else {
                            mView.response(false, null);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
