package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myschool.schoolcircle.entity.Tb_order;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * 我的订单
 * Created by Mr.R on 2016/7/12.
 */
public class MyOrderRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tb_order> mOrderList;

    public MyOrderRecyclerAdapter(List<Tb_order> orderList) {
        mOrderList = orderList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myorder, parent, false);
        return MyOrderRecyclerViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MyOrderRecyclerViewHolder holder = (MyOrderRecyclerViewHolder) viewHolder;
//        holder.setItemText(mOrderList.get(position).getMarketName());
//        holder.setItemText(mOrderList.get(position).getOrderStatus());
//        holder.setItemText(mOrderList.get(position).getTotal());
//        holder.setItemText(mOrderList.get(position).getAddress());
//        holder.setImageView(mOrderList.get(position).getMarketPhoto());
        holder.mMyOrderPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("mMarketPhoto", "123");
            }
        });

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("mCardView", "123");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrderList == null ? 0 : mOrderList.size();
    }

    //设置点击事件接口，回调等
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}

