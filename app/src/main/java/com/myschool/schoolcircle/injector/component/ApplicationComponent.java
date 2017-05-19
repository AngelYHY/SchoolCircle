package com.myschool.schoolcircle.injector.component;

import android.content.Context;

import com.myschool.schoolcircle.base.AppApplication;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.injector.ContextLife;
import com.myschool.schoolcircle.injector.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Author:  ljo_h
 * Date:    2016/6/28
 * Description:
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    @ContextLife("Application")
    Context getContext();

    AppApplication injectApplication(AppApplication application);

    RetrofitService getRetrofitService();

}
