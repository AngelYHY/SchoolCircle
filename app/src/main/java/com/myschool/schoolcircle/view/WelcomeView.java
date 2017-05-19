package com.myschool.schoolcircle.view;

import com.myschool.schoolcircle.entity.Tb_user;

import free.free.base.IView;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/18 0018
 * github：
 */

public interface WelcomeView extends IView {
    void response(boolean success, Tb_user user);

}
