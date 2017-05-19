package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myschool.schoolcircle.ui.activity.school.activitys.DetailsItemActivity;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.main.R;

import java.util.List;

public class AboutMeRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Tb_dynamic> mDynamics;

    private Context context;
    private int loading = -1;

    public AboutMeRecyclerAdapter(Context context, List<Tb_dynamic> dynamics) {
        this.context = context;
        this.mDynamics = dynamics;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != loading) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_aboutme, parent, false);

            return AboutMeRecyclerViewHolder.newInstance(view);
        } else {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);

            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof AboutMeRecyclerViewHolder) {

            AboutMeRecyclerViewHolder holder = (AboutMeRecyclerViewHolder) viewHolder;

            Tb_dynamic dynamic = mDynamics.get(position);
            holder.setHeadView(dynamic.getAvatar());
            holder.setTvamUser(dynamic.getRealName());
            holder.setTvameTime(dynamic.getDatetime());
            holder.setDynamicinfo(dynamic.getTextContent());

            //显示动态的第一张图片，没有就不显示
            List<String> images = dynamic.getImages();
            if (images != null && images.size() > 0) {
                holder.setDynamicimage(images.get(0));
            } else {
                holder.dynamicimage.setVisibility(View.GONE);
                holder.setDynamicimage(null);
            }

            //点击进入详情页
            holder.mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailsItemActivity.class);
                    intent.putExtra("dynamicData", mDynamics.get(position));
                    context.startActivity(intent);
                }
            });
        } else {
            ((LoadingViewHolder) viewHolder).progressBar.show();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mDynamics.get(position) != null) {
            return super.getItemViewType(position);
        } else {
            return loading;
        }
    }

    @Override
    public int getItemCount() {
        return mDynamics == null ? 0 : mDynamics.size();
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

    //上拉加载的viewHolder
    private class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ContentLoadingProgressBar progressBar;
        public TextView textView;

        public LoadingViewHolder(View view) {
            super(view);
            progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.clp_loading);
            textView = (TextView) view.findViewById(R.id.tv_loading);
        }
    }
}