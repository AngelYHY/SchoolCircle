package com.myschool.schoolcircle.adapter;

import android.graphics.Color;
import android.view.View;

import com.myschool.schoolcircle.ui.activity.concact.group.Group;
import com.myschool.schoolcircle.entity.Tb_group;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/8/11.
 */
public class GroupAdapter extends CommonAdapter<Tb_group> {

    private Group group;

    public GroupAdapter(Group context, List<Tb_group> data, int layoutId) {
        super(context, data, layoutId);
        this.group = context;
    }

    @Override
    public void convert(ViewHolder holder, final Tb_group tb_group) {
//        setImageView(R.id.iv_group_image, tb_group.getGroupImage())
        holder.setText(R.id.tv_group_name, tb_group.getGroupName())
                .setText(R.id.tv_group_describe, tb_group.getGroupDescribe())
                .setCheckBoxText(R.id.cb_join_num, tb_group.getJoinNum() + "")
                .setText(R.id.tv_label, tb_group.getLabel());
        switch (tb_group.getLabel()) {
            case "私人":
                holder.setCardViewColor(R.id.cv_label, Color.parseColor("#ffab00"));
                break;
            case "专业":
                holder.setCardViewColor(R.id.cv_label, Color.parseColor("#0288d1"));
                break;
            case "社团":
                holder.setCardViewColor(R.id.cv_label, Color.parseColor("#ff403c"));
                break;
        }

        holder.getView(R.id.cv_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                group.enterGroupChat(tb_group);
            }
        });
    }

}
