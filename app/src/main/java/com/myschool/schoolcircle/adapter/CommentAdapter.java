package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.myschool.schoolcircle.entity.Tb_comment;
import com.myschool.schoolcircle.main.R;

import java.util.List;

/**
 * Created by Mr.R on 2016/8/19.
 */
public class CommentAdapter extends CommonAdapter<Tb_comment> {
    private List<Tb_comment> data;
    private int loading = -1;

    public CommentAdapter(Context context, List<Tb_comment> data, int layoutId) {
        super(context, data, layoutId);
        this.data = data;
    }

    @Override
    public void convert(ViewHolder holder, Tb_comment tb_comment) {
        holder.setImageView(R.id.iv_avatar,tb_comment.getAvatar())
                .setText(R.id.tv_content,tb_comment.getTextContent())
                .setText(R.id.tv_time,tb_comment.getDatetime());

        if (tb_comment.getTargetName().isEmpty()) {
            holder.setText(R.id.tv_name,tb_comment.getRealName());
        } else {
            holder.setText(R.id.tv_name,tb_comment.getRealName()+
                    " 回复 "+tb_comment.getTargetName());
        }

        RadioButton rbAnswer = holder.getView(R.id.iv_answer);
        int num = tb_comment.getChildComments().size();
        if (num > 0) {
            rbAnswer.setVisibility(View.VISIBLE);
            rbAnswer.setText(num+"");
        } else {
            rbAnswer.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(data.get(position)!=null){
            return super.getItemViewType(position);
        }else{
            return loading;
        }
    }

    //上拉加载的viewHolder
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
