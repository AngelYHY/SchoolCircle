package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * 我的订单
 * Created by Mr.R on 2016/7/12.
 */
public class MyOrderRecyclerViewHolder extends RecyclerView.ViewHolder {

    public CardView mCardView;
    public View mMyOrderPhoto;
    public TextView mMyOrderName;
    public TextView mMyOrderStatus;
    public TextView mMyOrdertotal;
    public TextView mMyOrderAddress;
    public Button mPay;

    public MyOrderRecyclerViewHolder(final View parent,
                                     CardView mCardView,
                                     View myorderPhoto,
                                     TextView myorderName,
                                     TextView myorderOrderStatus,
                                     TextView myordertotal,
                                     TextView myorderAddress,
                                     Button pay) {
        super(parent);
        this.mCardView = mCardView;
        this.mMyOrderPhoto = myorderPhoto;
        this.mMyOrderName = myorderName;
        this.mMyOrderStatus = myorderOrderStatus;
        this.mMyOrdertotal = myordertotal;
        this.mMyOrderAddress = myorderAddress;
        this.mPay = pay;
    }

    public static MyOrderRecyclerViewHolder newInstance(View parent) {
        CardView cardView = (CardView) parent.findViewById(R.id.cv_myorder);
        View myorderPhoto = parent.findViewById(R.id.iv_myorder_photo);
        TextView myorderName = (TextView) parent.findViewById(R.id.tv_myorder_name);
        TextView myorderOrderStatus = (TextView) parent.findViewById(R.id.tv_myorder_status);
        TextView myordertotal = (TextView) parent.findViewById(R.id.tv_myorder_price);
        TextView myorderAddress = (TextView) parent.findViewById(R.id.tv_myorder_location);
        Button pay = (Button) parent.findViewById(R.id.btn_pay);
        return new MyOrderRecyclerViewHolder(parent, cardView, myorderPhoto, myorderName,
                myorderOrderStatus, myordertotal, myorderAddress, pay);
    }

    public void setItemText(CharSequence text) {
        mMyOrderName.setText(text);
        mMyOrderStatus.setText(text);
        mMyOrdertotal.setText(text);
        mMyOrderAddress.setText(text);
    }

    public void setImageView(int resId) {
        mMyOrderPhoto.setBackgroundResource(resId);
    }

}