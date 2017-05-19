package com.myschool.schoolcircle.adapter;

import android.content.Context;

import com.myschool.schoolcircle.entity.Association;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/11.
 */
public class AssociationAdapter extends CommonAdapter<Association> {

    public AssociationAdapter(Context context, List<Association> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Association association) {
        holder.setImageView(R.id.iv_association_head,association.getAssociationHeadId())
                .setText(R.id.tv_association_name,association.getName())
                .setText(R.id.tv_association_information,association.getInformation());
    }

}
