package com.myschool.schoolcircle.presenter.impl;

import android.app.Activity;

import com.myschool.schoolcircle.bean.LoginResponse;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.presenter.RegisterPresenter;
import com.myschool.schoolcircle.ui.activity.welcome.RegisterActivity;
import com.myschool.schoolcircle.view.RegisterView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import javax.inject.Inject;

import free.free.base.BasePresenter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/19 0019
 * github：
 */

public class RegisterPresenterImpl extends BasePresenter<RegisterView> implements RegisterPresenter {

    private final RegisterActivity mActivity;
    private final RetrofitService mService;

    @Inject
    RegisterPresenterImpl(RetrofitService service, Activity activity) {
        mActivity = (RegisterActivity) activity;
        mService = service;
    }

    @Override
    public void register(String phone, String name, String psw) {
        mService.register(name, psw, "Register", phone)
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
                        mView.showException(e, 0);
                        mView.showMsg(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
