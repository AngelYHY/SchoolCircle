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
 * 下单通知
 * Created by Mr.R on 2016/7/12.
 */
public class MyInformRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tb_order> mOrderList;

    public MyInformRecyclerAdapter(List<Tb_order> orderList) {
        mOrderList = orderList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myinform, parent, false);
        return MyInformRecyclerViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MyInformRecyclerViewHolder holder = (MyInformRecyclerViewHolder) viewHolder;
//
//        holder.setItemText(mOrderList.get(position).getMarketName());//商品名称
//        holder.setItemText(mOrderList.get(position).getOrderStatus());//订单状态
//        holder.setItemText(mOrderList.get(position).getTotal());//价格
//        holder.setItemText(mOrderList.get(position).getAddress());//地址

        holder.mMyInformPhoto.setOnClickListener(new View.OnClickListener() {
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

