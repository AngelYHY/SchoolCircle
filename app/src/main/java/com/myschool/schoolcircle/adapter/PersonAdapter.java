package com.myschool.schoolcircle.adapter;

import android.content.Context;

import com.myschool.schoolcircle.entity.Person;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/11.
 */
public class PersonAdapter extends CommonAdapter<Person> {

    public PersonAdapter(Context context, List<Person> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Person person) {
        holder.setImageView(R.id.iv_person_head,person.getHeadId())
                .setText(R.id.tv_person_name,person.getName())
                .setText(R.id.tv_person_information,person.getSignature());
    }

}
