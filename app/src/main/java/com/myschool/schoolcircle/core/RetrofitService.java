package com.myschool.schoolcircle.core;

import com.myschool.schoolcircle.entity.Tb_user;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Author:  ljo_h
 * Date:    2016/8/10
 * Description:
 */
public interface RetrofitService {
    //登录
    @FormUrlEncoded
    @POST("Login")
    Observable<Tb_user> login(@Field("username") String loginName, @Field("password") String password);

    //注册
    @FormUrlEncoded
    @POST("Register")
    Observable<Tb_user> register(@Field("username") String loginName, @Field("password") String password, @Field("type") String type, @Field("phone") String phone);

}
