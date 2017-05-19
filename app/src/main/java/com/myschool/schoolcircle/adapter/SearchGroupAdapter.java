package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;

import com.myschool.schoolcircle.ui.activity.concact.group.GroupDetail;
import com.myschool.schoolcircle.entity.Tb_group;
import com.myschool.schoolcircle.main.R;

import java.util.List;

import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Mr.R on 2016/8/11.
 */
public class SearchGroupAdapter extends CommonAdapter<Tb_group> {
    private UserInfo mUser;

    public SearchGroupAdapter(Context context,
                              UserInfo user,
                              List<Tb_group> data, int layoutId) {
        super(context, data, layoutId);
        this.mUser = user;
    }

    @Override
    public void convert(ViewHolder holder, final Tb_group tb_group) {
        holder.setText(R.id.tv_group_name, tb_group.getGroupName())
                .setText(R.id.tv_group_describe, tb_group.getGroupDescribe())
                .setCheckBoxText(R.id.cb_join_num, tb_group.getJoinNum() + "")
                .setText(R.id.tv_label, tb_group.getLabel());
        switch (tb_group.getLabel()) {
            case "专业":
                holder.setCardViewColor(R.id.cv_label, Color.parseColor("#0288d1"));
                break;
            case "社团":
                holder.setCardViewColor(R.id.cv_label, Color.parseColor("#ff403c"));
                break;
            case "私人":
                holder.setCardViewColor(R.id.cv_label, Color.parseColor("#ffab00"));
                break;
        }

        holder.getView(R.id.cv_group).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, GroupDetail.class);
                intent.putExtra("group",tb_group);
                if (tb_group.getCreatorUsername().equals(mUser.getUserName())) {
                    intent.putExtra("type","ownerGroup");
                } else {
                    intent.putExtra("type","searchGroup");
                }
                context.startActivity(intent);
            }
        });
    }

}
