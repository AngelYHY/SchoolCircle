package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class AboutMeRecyclerViewHolder extends RecyclerView.ViewHolder {

    public CardView mCardView;
    public ImageView avater;
    public TextView tvamUser;
    public TextView tvameTime;
    public ImageView dynamicimage;
    public TextView dynamicinfo;

    public AboutMeRecyclerViewHolder(final View parent,
                                     CardView cardView,
                                     ImageView avater,
                                     TextView tvamUser,
                                     TextView tvameTime,
                                     ImageView dynamicimage,
                                     TextView dynamicinfo) {
        super(parent);
        this.mCardView = cardView;
        this.avater = avater;
        this.tvamUser = tvamUser;
        this.tvameTime = tvameTime;
        this.dynamicimage = dynamicimage;
        this.dynamicinfo = dynamicinfo;
    }

    public static AboutMeRecyclerViewHolder newInstance(View parent) {

        CardView cardView = (CardView) parent.findViewById(R.id.cv_aboutme);
        ImageView avater = (ImageView) parent.findViewById(R.id.iv_aboutme_avater);
        TextView tvamUser = (TextView) parent.findViewById(R.id.tv_aboutme_user);
        TextView tvameTime = (TextView) parent.findViewById(R.id.tv_aboutme_time);
        ImageView dynamicimage = (ImageView) parent.findViewById(R.id.iv_dynamic_image);
        TextView dynamicinfo = (TextView) parent.findViewById(R.id.tv_dynamic_info);

        return new AboutMeRecyclerViewHolder(parent, cardView, avater,
                tvamUser, tvameTime, dynamicimage, dynamicinfo);
    }

    //设置头像
    public void setHeadView(String imageUrl) {
        ImageOptions options = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(avater, imageUrl, options);
    }

    public void setDynamicimage(String imgUrl) {
        ImageOptions options = new ImageOptions.Builder().build();
        x.image().bind(dynamicimage, imgUrl, options);
    }

    public void setTvamUser(CharSequence text) {
        tvamUser.setText(text);
    }

    public void setTvameTime(CharSequence text) {
        tvameTime.setText(text);
    }

    public void setDynamicinfo(CharSequence text) {
        dynamicinfo.setText(text);
    }
}