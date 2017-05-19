package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/13.
 */
public abstract class CommonRecyclerAdapter<T> extends RecyclerView.Adapter<MyViewHolder> {
    protected Context context;
    protected List<T> mData;
    protected LayoutInflater mInflater;

    public CommonRecyclerAdapter(Context context, List<T> data) {
        this.context = context;
        this.mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        convert(holder,position);

        if (listener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int layoutPosition = holder.getLayoutPosition();
                    listener.onItemClick(holder.itemView,layoutPosition);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int layoutPosition = holder.getLayoutPosition();
                    listener.onItemLongClick(holder.itemView,layoutPosition);
                    return true;
                }
            });
        }
    }

    public abstract void convert(MyViewHolder holder, int position);

    @Override
    public int getItemCount() {
        return mData==null ? 0 : mData.size();
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
