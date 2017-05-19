package com.myschool.schoolcircle.adapter;

import android.content.Context;

import com.myschool.schoolcircle.entity.Person;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/11.
 */
public class FriendsAdapter extends CommonAdapter<Tb_user> {

    public FriendsAdapter(Context context, List<Tb_user> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Tb_user user) {
        holder.setImageView(R.id.iv_person_head,user.getHeadView())
                .setText(R.id.tv_person_name,user.getRealName())
                .setText(R.id.tv_person_information,user.getSignature());
    }

}
