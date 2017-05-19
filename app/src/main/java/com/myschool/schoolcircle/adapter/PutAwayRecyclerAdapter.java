package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myschool.schoolcircle.entity.Tb_market;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * 我的上架
 * Created by Mr.R on 2016/7/12.
 */
public class PutAwayRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tb_market> mMarketList;

    public PutAwayRecyclerAdapter(List<Tb_market> marketList) {
        mMarketList = marketList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_myputaway, parent, false);
        return PutAwayRecyclerViewHolder.newInstance(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        final PutAwayRecyclerViewHolder holder = (PutAwayRecyclerViewHolder) viewHolder;

        holder.setImageView(mMarketList.get(position).getMarketImage());
        holder.setItemText(mMarketList.get(position).getMarketName());
        holder.setItemText(mMarketList.get(position).getMarketPrice());

        holder.mPutawayPhoto.setOnClickListener(new View.OnClickListener() {
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

