package com.myschool.schoolcircle.widget;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseViewHolder;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/20 0020
 * github：
 */

public class MyBaseViewHolder extends BaseViewHolder {
    public MyBaseViewHolder(View view) {
        super(view);
    }

    public MyBaseViewHolder setIV(Context context, int viewId, String url) {
        Glide.with(context)
                .load(url)
                .into((ImageView) getView(viewId));
        return this;
    }

}
