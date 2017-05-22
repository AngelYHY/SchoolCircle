package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.myschool.schoolcircle.main.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class DynamicRecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivAvatar;
    public TextView tvRealName;
    public TextView tvSchoolName;
    public TextView tvTextContent;
    public CardView mCardView;
    public TextView tvTime;
    public CheckBox cbRetransmission;
    public CheckBox cbComment;
    public CheckBox cbLike;
    public PhotoView mPhotoView;
    public NineGridView nineGridView;

    public DynamicRecyclerViewHolder(final View parent,
                                     ImageView ivAvatar,
                                     TextView tvRealName,
                                     TextView tvSchoolName,
                                     TextView tvTextContent,
                                     TextView tvTime,
                                     CheckBox cbRetransmission,
                                     CheckBox cbComment,
                                     CheckBox cbLike,
                                     CardView mCardView,
                                     NineGridView nineGridView,
                                     PhotoView mPhotoView) {
        super(parent);
        this.tvRealName = tvRealName;
        this.tvSchoolName = tvSchoolName;
        this.tvTextContent = tvTextContent;
        this.tvTime = tvTime;
        this.ivAvatar = ivAvatar;
        this.cbRetransmission = cbRetransmission;
        this.cbComment = cbComment;
        this.cbLike = cbLike;
        this.mCardView = mCardView;
        this.nineGridView = nineGridView;
        this.mPhotoView = mPhotoView;
    }

    public static DynamicRecyclerViewHolder newInstance(Context context, View parent) {
        ImageView civAvatar = (ImageView) parent.findViewById(R.id.iv_head);
        TextView tvRealName = (TextView) parent.findViewById(R.id.tv_real_name);
        TextView tvSchoolName = (TextView) parent.findViewById(R.id.tv_school_name);
        TextView tvTextContent = (TextView) parent.findViewById(R.id.tv_text_content);
        TextView tvTime = (TextView) parent.findViewById(R.id.tv_time);
        CheckBox cbRetransmission = (CheckBox) parent.findViewById(R.id.cb_retransmission);
        CheckBox cbComment = (CheckBox) parent.findViewById(R.id.cb_comment);
        CheckBox cbLike = (CheckBox) parent.findViewById(R.id.cb_like);
        CardView cardView = (CardView) parent.findViewById(R.id.cv_dynamic);
        NineGridView nineGridView = (NineGridView) parent.findViewById(R.id.ngv_dynamic);
        PhotoView mPhotoView = (PhotoView) parent.findViewById(R.id.photo_view);

        return new DynamicRecyclerViewHolder(parent, civAvatar, tvRealName,
                tvSchoolName, tvTextContent, tvTime, cbRetransmission, cbComment, cbLike,
                cardView, nineGridView, mPhotoView);
    }

    //设置头像
    public void setAvatarImage(String imageUrl) {
        ImageOptions options = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_head)
                .setFailureDrawableId(R.mipmap.ic_head)
                .build();
        x.image().bind(ivAvatar, imageUrl, options);
    }

    public void setRealNameText(CharSequence text) {
        tvRealName.setText(text);
    }

    public void setSchoolNameText(CharSequence text) {
        tvSchoolName.setText(text);
    }

    public void setTextContentText(CharSequence text) {
        tvTextContent.setText(text);
    }

    public void setTimeText(CharSequence text) {
        tvTime.setText(text);
    }

    public void setLikeChecked(boolean flag) {
        cbLike.setChecked(flag);
    }

    public void setLikeNum(String num) {
        cbLike.setText(num);
    }

    public void setCommentNum(String num) {
        cbComment.setText(num);
    }

    public void setNineGridView(Context context, List<ImageInfo> images) {
        nineGridView.setAdapter(new NineGridViewClickAdapter(context, images));
    }

}


