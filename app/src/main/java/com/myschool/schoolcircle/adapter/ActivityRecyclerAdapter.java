package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class ActivityRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Tb_activity> activities;
    private int loading = -1;

    public ActivityRecyclerAdapter(Context context, List<Tb_activity> activities) {
        this.context = context;
        this.activities = activities;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != loading) {
            //不在加载
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_activity, parent, false);
            return ActivityRecyclerViewHolder.newInstance(context, view);
        } else {
            //在加载
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof ActivityRecyclerViewHolder) {
            final ActivityRecyclerViewHolder holder = (ActivityRecyclerViewHolder) viewHolder;
            final Tb_activity activity = activities.get(position);
            holder.setImageView(activity.getPicture());
            holder.setThemeText(activity.getTheme());
            if (activity.getSchoolName() != null) {
                holder.setSchoolNameText("[" + activity.getSchoolName() + "]");
            }
            holder.setTimeText(activity.getsDatetime());
//            holder.setSponsorText(activity.getSponsor());
            holder.setTvBeginText(activity.getActivityBegin().substring(5, 16));
            holder.setTvEndText(activity.getActivityEnd().substring(5, 16));
            holder.setPlaceText(activity.getPlace());
            holder.setCbCommentText(activity.getCommentNum() + "");
            holder.setCbJoinText(activity.getJoinNum() + "");
            holder.setCbLikeText(activity.getLikeNum() + "");

            String state = activity.getState();
            holder.setStateText(state);
            switch (state) {
                case "未开始":
                    holder.setCardStateColor(Color.parseColor("#ffab00"));
                    break;
                case "进行中":
                    holder.setCardStateColor(Color.parseColor("#ff403c"));
                    break;
                case "已结束":
                    holder.setCardStateColor(Color.parseColor("#cdcdcd"));
                    break;
                default:
                    break;
            }

            if (listener != null) {
                holder.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        listener.onItemClick(holder.mCardView, position);
                    }
                });

                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = holder.getLayoutPosition();
                        listener.onItemClick(holder.imageView, position);
                    }
                });
            }
        } else {
            ((LoadingViewHolder) viewHolder).progressBar.show();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (activities.get(position) != null) {
            return super.getItemViewType(position);
        } else {
            return loading;
        }
    }

    @Override
    public int getItemCount() {
        return activities == null ? 0 : activities.size();
    }

    //设置点击事件接口，回调等
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
//        void onItemLongClick(View view, int position);
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
