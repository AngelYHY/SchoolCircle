package com.myschool.schoolcircle.adapter;

import android.view.View;

import com.myschool.schoolcircle.ui.activity.concact.group.GroupApply;
import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/29.
 */
public class GroupApplyAdapter extends CommonAdapter<Tb_message_info> {
    private GroupApply activity;

    public GroupApplyAdapter(GroupApply activity, List<Tb_message_info> data, int layoutId) {
        super(activity, data, layoutId);
        this.activity = activity;
    }

    @Override
    public void convert(ViewHolder holder, final Tb_message_info tbMessageInfo) {
        Tb_user sender = tbMessageInfo.getSender();
        holder.setImageView(R.id.iv_new_friend_head,sender.getHeadView())
                .setText(R.id.tv_new_friend_name,sender.getRealName())
                .setText(R.id.tv_prove_info,tbMessageInfo.getContent())
                .setText(R.id.tv_time,tbMessageInfo.getTime());

        holder.getView(R.id.btn_agree).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    activity.addGroupMember(tbMessageInfo.getTargetId()+"",
                            tbMessageInfo.getSender().getUsername());
                }
            }
        });

        holder.getView(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    activity.deleteApply(tbMessageInfo);
                }
            }
        });
    }
}
