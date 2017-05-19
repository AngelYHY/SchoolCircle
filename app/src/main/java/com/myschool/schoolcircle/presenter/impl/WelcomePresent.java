package com.myschool.schoolcircle.presenter.impl;

import android.app.Activity;
import android.content.Context;

import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.injector.ContextLife;
import com.myschool.schoolcircle.presenter.IWelcome;
import com.myschool.schoolcircle.ui.activity.welcome.WelcomeActivity;
import com.myschool.schoolcircle.view.WelcomeView;

import javax.inject.Inject;

import free.free.base.BasePresenter;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/18 0018
 * github：
 */

public class WelcomePresent extends BasePresenter<WelcomeView> implements IWelcome {

    private final WelcomeActivity mActivity;
    private final RetrofitService mService;

    @Inject
    WelcomePresent(RetrofitService service, Activity activity, @ContextLife("Application") Context context) {
        mActivity = (WelcomeActivity) activity;
        mService = service;
    }

    @Override
    public void response() {

    }
}
