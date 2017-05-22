package com.myschool.schoolcircle.bean;

import com.myschool.schoolcircle.entity.Tb_activity;

import java.util.List;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public class ActivityResponse extends HBase {

    private List<Tb_activity> list;

    public List<Tb_activity> getList() {
        return list;
    }

    public void setList(List<Tb_activity> list) {
        this.list = list;
    }

}
