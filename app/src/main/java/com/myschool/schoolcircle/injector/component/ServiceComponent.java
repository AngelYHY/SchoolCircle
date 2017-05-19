package com.myschool.schoolcircle.injector.component;

import android.content.Context;

import com.myschool.schoolcircle.injector.ContextLife;
import com.myschool.schoolcircle.injector.PerService;
import com.myschool.schoolcircle.injector.module.ServiceModule;

import dagger.Component;

/**
 * Created by yuyidong on 15/11/22.
 */
@PerService
@Component(dependencies = ApplicationComponent.class, modules = {ServiceModule.class})
public interface ServiceComponent {

    @ContextLife("Service")
    Context getServiceContext();

    @ContextLife("Application")
    Context getApplicationContext();

}
