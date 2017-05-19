package com.myschool.schoolcircle.injector.component;

import android.app.Activity;
import android.content.Context;

import com.myschool.schoolcircle.injector.ContextLife;
import com.myschool.schoolcircle.injector.PerFragment;
import com.myschool.schoolcircle.injector.module.FragmentModule;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@PerFragment
@Component(modules = FragmentModule.class, dependencies = ApplicationComponent.class)
public interface FragmentComponent {
    @ContextLife("Application")
    Context getContext();

    @ContextLife("Activity")
    Context getActivityContext();

    Activity getActivity();

}
