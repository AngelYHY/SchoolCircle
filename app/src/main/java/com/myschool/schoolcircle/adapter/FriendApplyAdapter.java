package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.view.View;

import com.myschool.schoolcircle.ui.activity.concact.single.FriendApplyActivity;
import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/29.
 */
public class FriendApplyAdapter extends CommonAdapter<Tb_message_info> {
    private FriendApplyActivity activity;

    public FriendApplyAdapter(Context context, List<Tb_message_info> data, int layoutId) {
        super(context, data, layoutId);
        activity = FriendApplyActivity.activity;
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
                    activity.doAdd(tbMessageInfo.getSender().getUsername());
                }
            }
        });

        holder.getView(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (activity != null) {
                    activity.deleteFriendApply(tbMessageInfo);
                }
            }
        });
    }
}
