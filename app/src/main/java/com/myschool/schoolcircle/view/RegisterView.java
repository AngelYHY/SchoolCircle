package com.myschool.schoolcircle.view;

import com.myschool.schoolcircle.entity.Tb_user;

import free.free.base.IView;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/19 0019
 * github：
 */

public interface RegisterView extends IView {

    void response(boolean success, Tb_user user);

}
