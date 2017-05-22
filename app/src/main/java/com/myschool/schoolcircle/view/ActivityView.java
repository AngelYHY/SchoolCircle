package com.myschool.schoolcircle.view;

import com.myschool.schoolcircle.entity.Tb_activity;

import java.util.List;

import free.free.base.IView;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public interface ActivityView extends IView {
    void response(List<Tb_activity> list, boolean hasMore);
}
