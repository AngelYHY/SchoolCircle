package com.myschool.schoolcircle.presenter.impl;

import android.app.Activity;

import com.myschool.schoolcircle.bean.Base;
import com.myschool.schoolcircle.bean.LoginResponse;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.presenter.MinePresenter;
import com.myschool.schoolcircle.ui.activity.mine.MineActivity;
import com.myschool.schoolcircle.view.MineView;
import com.orhanobut.logger.Logger;
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

public class MinePresenterImpl extends BasePresenter<MineView> implements MinePresenter {

    private final MineActivity mActivity;
    private final RetrofitService mService;

    @Inject
    MinePresenterImpl(RetrofitService service, Activity activity) {
        mActivity = (MineActivity) activity;
        mService = service;
    }

    @Override
    public void updateInfo(String name, final String update, final String target) {
        mService.update(name, update, target)
                .compose(mView.<Base>applySchedulers())
                .compose(mActivity.<Base>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Base>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Base result) {
                        mView.updateResponse(result.isSuccess(), target, update);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mView.showException(e, 0);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void getUserInfo(String name) {
        mService.getUserInfo(name)
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
                        Logger.e(e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
