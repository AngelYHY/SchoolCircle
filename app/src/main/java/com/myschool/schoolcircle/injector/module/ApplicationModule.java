package com.myschool.schoolcircle.injector.module;

import android.content.Context;

import com.myschool.schoolcircle.base.AppApplication;
import com.myschool.schoolcircle.core.RetrofitService;
import com.myschool.schoolcircle.injector.ContextLife;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author:  ljo_h
 * Date:    2016/6/28
 * Description:
 */
@Module
public class ApplicationModule {
    private AppApplication mApplication;

    public ApplicationModule(AppApplication application) {
        mApplication = application;
    }

    @Provides
    @Singleton
    @ContextLife("Application")
    public Context provideContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    RetrofitService provideRetrofitService() {
        //手动创建一个OkHttpClient并设置超时时间
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.connectTimeout(2, TimeUnit.SECONDS);

        String baseUrl = "http://localhost:8080/SchoolCircleServlet/";
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClientBuilder.build())
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(RetrofitService.class);
    }

}
