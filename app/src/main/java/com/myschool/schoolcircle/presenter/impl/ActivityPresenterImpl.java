package com.myschool.schoolcircle.presenter.impl;

import android.support.v4.app.Fragment;

import com.myschool.schoolcircle.bean.ActivityResponse;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.presenter.ActivityPresenter;
import com.myschool.schoolcircle.ui.fragment.ActivityFragment;
import com.myschool.schoolcircle.view.ActivityView;
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

public class ActivityPresenterImpl extends BasePresenter<ActivityView> implements ActivityPresenter {

    private final ActivityFragment mFragment;
    private final RetrofitService mService;

    @Inject
    ActivityPresenterImpl(RetrofitService service, Fragment fragment) {
        mFragment = (ActivityFragment) fragment;
        mService = service;
    }

    @Override
    public void getActivity(String type, String start, String schoolName, String name) {
        Logger.e(type + "--" + start + "--" + schoolName + "--" + name);
        mService.getActivities(type, start, schoolName, name)
                .compose(mView.<ActivityResponse>applySchedulers())
                .compose(mFragment.<ActivityResponse>bindUntilEvent(FragmentEvent.DESTROY))
                .subscribe(new Observer<ActivityResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ActivityResponse result) {
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
