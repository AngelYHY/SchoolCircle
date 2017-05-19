package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class MyPutawayRecyclerViewHolder extends RecyclerView.ViewHolder {

    public CardView mCardView;
    public View mMyPutawayPhoto;
    public TextView mMyPutawayName;
    public TextView mMyPutawayPrice;
    public Button mDownpay;

    public MyPutawayRecyclerViewHolder(final View parent, CardView mCardView, View myPutawayPhoto,
                                       TextView myPutawayName, TextView myPutawayPrice, Button Downaway

    ) {
        super(parent);
        this.mCardView = mCardView;
        this.mMyPutawayPhoto = myPutawayPhoto;
        this.mMyPutawayName = myPutawayName;
        this.mMyPutawayPrice = myPutawayPrice;
        this.mDownpay = Downaway;
    }


    public static MyPutawayRecyclerViewHolder newInstance(View parent) {
        CardView cardView = (CardView) parent.findViewById(R.id.cv_myputaway);
        View myPutawayPhoto = parent.findViewById(R.id.iv_myputaway_photo);
        TextView myPutawayName = (TextView) parent.findViewById(R.id.tv_myputaway_name);
        TextView myPutawayPrice = (TextView) parent.findViewById(R.id.tv_myputaway_price);
        Button downaway = (Button) parent.findViewById(R.id.btn_downaway);
        return new MyPutawayRecyclerViewHolder(parent, cardView, myPutawayPhoto,
                myPutawayName, myPutawayPrice, downaway);
    }

    public void setItemText(CharSequence text) {
        mMyPutawayName.setText(text);
        mMyPutawayPrice.setText(text);
    }

    public void setImageView(int resId) {
        mMyPutawayPhoto.setBackgroundResource(resId);
    }
}