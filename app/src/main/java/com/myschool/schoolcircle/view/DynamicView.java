package com.myschool.schoolcircle.view;

import com.myschool.schoolcircle.entity.Tb_dynamic;

import java.util.List;

import free.free.base.IView;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public interface DynamicView extends IView {
    void response(List<Tb_dynamic> list, boolean hasMore);
}
