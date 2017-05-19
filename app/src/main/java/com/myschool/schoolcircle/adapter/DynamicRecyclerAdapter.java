package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.main.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class DynamicRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public List<Tb_dynamic> mList;
    private Context context;
    private int loading = -1;

    public DynamicRecyclerAdapter(Context context, List<Tb_dynamic> mList) {
        this.context = context;
        this.mList = mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType != loading) {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_dynamic, parent, false);
            return DynamicRecyclerViewHolder.newInstance(context,view);
        } else {
            final View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof DynamicRecyclerViewHolder) {
            final DynamicRecyclerViewHolder holder = (DynamicRecyclerViewHolder) viewHolder;
            final Tb_dynamic dynamic = mList.get(position);
            holder.setAvatarImage(dynamic.getAvatar());
            holder.setRealNameText(dynamic.getRealName());
            if (dynamic.getSchoolName() != null) {
                holder.setSchoolNameText("来自["+dynamic.getSchoolName()+"]");
            }
            holder.setTextContentText(dynamic.getTextContent());
            holder.setTimeText(dynamic.getDatetime());
            holder.setLikeNum(dynamic.getLikeNum()+"");
            holder.setCommentNum(dynamic.getCommentNum()+"");
//            holder.setLikeChecked(true);
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            List<String> images = dynamic.getImages();
            if (images != null) {
                if (images.size() > 0) {
                    for (String image : images) {
                        ImageInfo info = new ImageInfo();
                        info.setThumbnailUrl(image);
                        info.setBigImageUrl(image);
                        imageInfo.add(info);
                    }
                }
            }
            holder.nineGridView.setAdapter(new NineGridViewClickAdapter(context,imageInfo));


            if (listener != null) {
                //卡片的点击事件
                holder.mCardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int layoutPosition = holder.getLayoutPosition();
                        listener.onItemClick(holder.mCardView,layoutPosition);
                    }
                });

                //转发的点击事件
                holder.cbRetransmission.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int layoutPosition = holder.getLayoutPosition();
                        listener.onItemClick(holder.cbRetransmission,layoutPosition);
                    }
                });

                //点赞的点击事件
                holder.cbLike.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        String s = holder.cbLike.getText().toString();
                        int num = Integer.parseInt(s);
                        if (compoundButton.isChecked()) {
                            holder.cbLike.setText((num+1)+"");
                            int layoutPosition = holder.getLayoutPosition();
                            listener.onItemClick(holder.cbLike,layoutPosition);
                        } else {
                            if (num > 0) {
                                holder.cbLike.setText((num-1)+"");
                                listener.onItemClick(holder.cbLike,-1);
                            }
                        }
                    }
                });
            }

        } else{
            ((LoadingViewHolder) viewHolder).progressBar.show();
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    //设置点击事件接口，回调等
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
//        void onItemLongClick(View view, int position);
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position)!=null){
            return super.getItemViewType(position);
        }else{
            return loading;
        }
    }

    private OnItemClickListener listener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder{
        public ContentLoadingProgressBar progressBar;
        public TextView textView;

        public LoadingViewHolder(View view){
            super(view);
            progressBar= (ContentLoadingProgressBar) view.findViewById(R.id.clp_loading);
            textView=(TextView)view.findViewById(R.id.tv_loading);
        }
    }

}




