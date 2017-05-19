package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class MarketRecyclerViewHolder extends RecyclerView.ViewHolder {
    public CardView mCardView;
    public View mMarketPhoto;
    public TextView mMarketName;
    public ImageView mMarketTotal;
    public TextView mMarketPrice;
    public TextView mMarketUser;
    public TextView mMarketTime;

    public MarketRecyclerViewHolder(final View parent,
                                    CardView mCardView,
                                    View marketPhoto,
                                    TextView marketName,
                                    ImageView marketTotal,
                                    TextView marketPrice,
                                    TextView marketUser,
                                    TextView marketTime
    ) {
        super(parent);
        this.mCardView = mCardView;
        this.mMarketPhoto = marketPhoto;
        this.mMarketName = marketName;
        this.mMarketTotal = marketTotal;
        this.mMarketPrice = marketPrice;
        this.mMarketUser = marketUser;
        this.mMarketTime = marketTime;
    }

    public static MarketRecyclerViewHolder newInstance(View parent) {
        CardView cardView = (CardView) parent.findViewById(R.id.cv_market);
        View marketPhoto = parent.findViewById(R.id.v_market_photo);
        TextView marketName = (TextView) parent.findViewById(R.id.tv_market_name);
        ImageView marketTotal = (ImageView) parent.findViewById(R.id.iv_market_price);
        TextView marketPrice = (TextView) parent.findViewById(R.id.tv_market_price);
        TextView marketUser = (TextView) parent.findViewById(R.id.tv_market_user);
        TextView marketTime = (TextView) parent.findViewById(R.id.tv_market_time);

        return new MarketRecyclerViewHolder(parent, cardView, marketPhoto,
                marketName, marketTotal,
                marketPrice, marketUser, marketTime);
    }

    public void setMarketPhoto(int resId) {
        mMarketPhoto.setBackgroundResource(resId);
    }

    public void setMarketName(CharSequence text) {
        mMarketName.setText(text);
    }

    public void setMarketPrice(CharSequence text) {
        mMarketPrice.setText(text);
    }

    public void setMarketUser(CharSequence text) {
        mMarketUser.setText(text);
    }

    public void setMarketTime(CharSequence text) {
        mMarketTime.setText(text);
    }

}