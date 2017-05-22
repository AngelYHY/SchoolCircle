package com.myschool.schoolcircle.presenter.impl;

import android.support.v4.app.Fragment;

import com.myschool.schoolcircle.bean.DynamicResponse;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.presenter.DynamicPresenter;
import com.myschool.schoolcircle.ui.fragment.DynamicFragment;
import com.myschool.schoolcircle.view.DynamicView;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.android.FragmentEvent;

import javax.inject.Inject;

import free.free.base.BasePresenter;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public class DynamicPresenterImpl extends BasePresenter<DynamicView> implements DynamicPresenter {

    private final DynamicFragment mFragment;
    private final RetrofitService mService;

    @Inject
    DynamicPresenterImpl(RetrofitService service, Fragment fragment) {
        mFragment = (DynamicFragment) fragment;
        mService = service;
    }

    @Override
    public void getDynamic(String type, String start, String schoolName) {
        Logger.e(type + "--" + start + "--" + schoolName);
        mService.getDynamic(type, start, schoolName)
                .compose(mView.<DynamicResponse>applySchedulers())
                .compose(mFragment.<DynamicResponse>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Observer<DynamicResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(DynamicResponse result) {
                        if (result.isSuccess()) {
                            mView.response(result.getList(), result.isHasMore());
                            Logger.e(result.getList().toString());
                        } else {
                            mView.showMsg(result.getErrorMsg());
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
