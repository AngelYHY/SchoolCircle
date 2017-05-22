package com.myschool.schoolcircle.bean;

import com.myschool.schoolcircle.entity.Tb_dynamic;

import java.util.List;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public class DynamicResponse extends HBase {

    private List<Tb_dynamic> list;

    public List<Tb_dynamic> getList() {
        return list;
    }

    public void setList(List<Tb_dynamic> list) {
        this.list = list;
    }

}
