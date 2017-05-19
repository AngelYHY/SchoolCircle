package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * 下单通知
 * Created by Mr.R on 2016/7/12.
 */
public class MyInformRecyclerViewHolder extends RecyclerView.ViewHolder {

    public CardView mCardView;
    public View mMyInformPhoto;//商品照片
    public TextView mMyInformName;//商品名称
    public TextView mMyInformStatus;//订单状态
    public TextView mMyInformtotal;//订单价格
    public TextView mMyInformAddress;//地址


    public MyInformRecyclerViewHolder(final View parent,
                                      CardView mCardView,
                                      View MyInformPhoto,
                                      TextView MyInformDescribe,
                                      TextView MyInformOrderStatus,
                                      TextView MyInformtotal,
                                      TextView MyInformAddress) {
        super(parent);
        this.mCardView = mCardView;
        this.mMyInformPhoto = MyInformPhoto;
        this.mMyInformName = MyInformDescribe;
        this.mMyInformStatus = MyInformOrderStatus;
        this.mMyInformtotal = MyInformtotal;
        this.mMyInformAddress = MyInformAddress;

    }

    public static MyInformRecyclerViewHolder newInstance(View parent) {

        CardView cardView = (CardView) parent.findViewById(R.id.cv_myinform);
        View MyInformPhoto = parent.findViewById(R.id.iv_myinform_image);
        TextView MyInformDescribe = (TextView) parent.findViewById(R.id.tv_myinform_name);
        TextView MyInformOrderStatus = (TextView) parent.findViewById(R.id.tv_myinform_status);
        TextView MyInformtotal = (TextView) parent.findViewById(R.id.tv_myinform_price);
        TextView MyInformAddress = (TextView) parent.findViewById(R.id.tv_myinform_location);

        return new MyInformRecyclerViewHolder(parent, cardView, MyInformPhoto, MyInformDescribe,
                MyInformOrderStatus, MyInformtotal, MyInformAddress);
    }

    public void setItemText(CharSequence text) {
        mMyInformName.setText(text);
        mMyInformStatus.setText(text);
        mMyInformtotal.setText(text);
        mMyInformAddress.setText(text);
    }

    public void setImageView(int resId) {
        mMyInformPhoto.setBackgroundResource(resId);
    }
}