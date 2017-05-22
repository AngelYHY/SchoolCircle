package com.myschool.schoolcircle.adapter;

import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.widget.MyBaseViewHolder;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/22 0022
 * github：
 */

public class ActivityRecyclerAdapterDemo extends BaseQuickAdapter<Tb_activity, MyBaseViewHolder> {

    public ActivityRecyclerAdapterDemo(@LayoutRes int layoutResId) {
        super(layoutResId, null);
    }

    @Override
    protected void convert(MyBaseViewHolder holder, Tb_activity item) {
        String state = item.getState();
        holder.setIV(mContext, R.id.iv_activity, item.getPicture())
                .setText(R.id.tv_theme, item.getTheme())
                .setText(R.id.tv_school_name, item.getSchoolName())
                .setText(R.id.tv_time, item.getsDatetime())
                .setText(R.id.tv_begin_content, item.getActivityBegin())
                .setText(R.id.tv_end_content, item.getActivityEnd())
                .setText(R.id.tv_place_content, item.getPlace())
                .setText(R.id.cb_join, item.getJoinNum() + "")
                .setText(R.id.cb_comment, item.getCommentNum() + "")
                .setText(R.id.cb_like, item.getLikeNum() + "")
                .setText(R.id.tv_state, item.getState())
                .addOnClickListener(R.id.iv_activity);

        CardView cv = holder.getView(R.id.cv_state);
        switch (state) {
            case "未开始":
                cv.setCardBackgroundColor(Color.parseColor("#ffab00"));
                break;
            case "进行中":
                cv.setCardBackgroundColor(Color.parseColor("#ff403c"));
                break;
            case "已结束":
                cv.setCardBackgroundColor(Color.parseColor("#cdcdcd"));
                break;
        }
    }
}
