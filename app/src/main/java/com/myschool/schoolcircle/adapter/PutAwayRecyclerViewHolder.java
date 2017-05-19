package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * 我的上架
 * Created by Mr.R on 2016/7/12.
 */
public class PutAwayRecyclerViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    public View mPutawayPhoto;
    public TextView mPutawayName;
//    public ImageView mPutawayTotal;
    public TextView mPutawayPrice;
    public Button mDownaway;

    public PutAwayRecyclerViewHolder(final View parent,
                                     CardView mCardView,
                                     View putawayPhoto,
                                     TextView putawayName,
                                     TextView putawayPrice,
                                     Button downaway) {
        super(parent);
        this.mCardView = mCardView;
        this.mPutawayPhoto = putawayPhoto;
        this.mPutawayName = putawayName;
        this.mPutawayPrice = putawayPrice;
        this.mDownaway = downaway;
    }

    public static PutAwayRecyclerViewHolder newInstance(View parent) {
        CardView cardView = (CardView) parent.findViewById(R.id.cv_myputaway);
        View putawayPhoto = parent.findViewById(R.id.iv_myputaway_photo);
        TextView putawayName = (TextView) parent.findViewById(R.id.tv_myputaway_name);
//        ImageView putawayTotal = (ImageView) parent.findViewById(R.id.iv_myputaway_price);
        TextView putawayPrice = (TextView) parent.findViewById(R.id.tv_myputaway_price);
        Button downaway = (Button) parent.findViewById(R.id.btn_downaway);
        return new PutAwayRecyclerViewHolder(parent, cardView, putawayPhoto, putawayName,
                putawayPrice, downaway);
    }

    public void setItemText(CharSequence text) {
        mPutawayName.setText(text);
        mPutawayPrice.setText(text);
    }

    public void setImageView(int resId) {
        mPutawayPhoto.setBackgroundResource(resId);
    }
}