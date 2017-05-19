package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myschool.schoolcircle.entity.Tb_market;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class MarketRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tb_market> mMarketList;
    private Context context;
    public MarketRecyclerAdapter(List<Tb_market> marketList) {
        mMarketList = marketList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_market, parent, false);
        return MarketRecyclerViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final MarketRecyclerViewHolder holder = (MarketRecyclerViewHolder) viewHolder;
        Tb_market market = mMarketList.get(position);
        holder.setMarketPhoto(market.getMarketImage());
        holder.setMarketName(market.getMarketName());
//        holder.setMarketTotal(market.getMarketImage());
        holder.setMarketPrice(market.getMarketPrice());
        holder.setMarketUser(market.getUserName());
        holder.setMarketTime(market.getMarketTime());

        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("mCardView", "123");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMarketList == null ? 0 : mMarketList.size();
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

