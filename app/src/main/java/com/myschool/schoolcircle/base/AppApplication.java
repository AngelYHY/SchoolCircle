package com.myschool.schoolcircle.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.ninegrid.NineGridView;
import com.myschool.schoolcircle.config.SettingConfig;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.injector.component.ApplicationComponent;
import com.myschool.schoolcircle.injector.component.DaggerApplicationComponent;
import com.myschool.schoolcircle.injector.module.ApplicationModule;
import com.myschool.schoolcircle.main.R;
import com.orhanobut.logger.Logger;
import com.rey.material.app.ThemeManager;

import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.UserInfo;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.GINGERBREAD;

/**
 * Author:  jact
 * Date:    2016/3/16
 * Description:
 */
public class AppApplication extends Application {

    private ApplicationComponent component;
    private Tb_user user;
    private UserInfo myInfo;
    private List<Activity> activities;
    private SettingConfig settingConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeManager.init(this, 2, 0, null);
        init();

        initOther();
    }

    private void initOther() {
        activities = new ArrayList<>();
        //设置极光推送时Debug输出的信息
        JPushInterface.setDebugMode(true);//项目完成时要删除！！！！
        //极光推送初始化
        JPushInterface.init(this);
        JMessageClient.init(this);
        x.Ext.init(this);

        settingConfig = new SettingConfig();
        NineGridView.setImageLoader(new GlideImageLoader());
    }

    /**
     * Glide 加载
     */
    private class GlideImageLoader implements NineGridView.ImageLoader {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)
                    .placeholder(R.mipmap.ic_photo_white_48dp)
                    .error(R.mipmap.ic_photo_white_48dp)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)//图片的大尺寸和小尺寸都缓存
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    public Tb_user getUser() {
        return user;
    }

    public void setUser(Tb_user user) {
        this.user = user;
    }

    public UserInfo getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(UserInfo myInfo) {
        this.myInfo = myInfo;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public SettingConfig getSettingConfig() {
        return settingConfig;
    }

    public void setSettingConfig(SettingConfig settingConfig) {
        this.settingConfig = settingConfig;
    }

    public void finish() {
        for (Activity activity : activities) {
            activity.finish();
        }
    }

    @Override
    public void onTerminate() {
        JMessageClient.unRegisterEventReceiver(this);
        JMessageClient.logout();
        super.onTerminate();
    }


    private void init() {
        Logger.init("FreeStar").methodCount(1).hideThreadInfo();
        this.component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        this.component.injectApplication(this);

//        enabledStrictMode();
    }


    private void enabledStrictMode() {
        if (SDK_INT >= GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //
                    .detectAll() //
                    .penaltyLog() //
                    .penaltyDeath() //
                    .build());
        }
    }

    public ApplicationComponent getApplicationComponent() {
        return this.component;
    }

}
