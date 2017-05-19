package com.myschool.schoolcircle.injector.component;

import android.app.Activity;
import android.content.Context;

import com.myschool.schoolcircle.injector.ContextLife;
import com.myschool.schoolcircle.injector.PerActivity;
import com.myschool.schoolcircle.injector.module.ActivityModule;
import com.myschool.schoolcircle.ui.activity.welcome.WelcomeActivity;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {

    @ContextLife("Activity")
    Context getActivityContext();

    @ContextLife("Application")
    Context getApplicationContext();

    Activity getActivity();

    void inject(WelcomeActivity activity);

}
