package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.myschool.schoolcircle.lib.CircleImageView;
import com.myschool.schoolcircle.main.R;
import com.squareup.picasso.Picasso;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class ActivityRecyclerViewHolder extends RecyclerView.ViewHolder {
    private Context context;
    public CardView mCardView;
    public ImageView imageView;
    public TextView tvTheme;
    public TextView tvSchoolName;
    public TextView tvTime;
    public TextView tvPlaceContent;
    public TextView tvBegin;
    public TextView tvEnd;
    public CheckBox cbComment;
    public CheckBox cbJoin;
    public CheckBox cbLike;
    public CardView cvState;
    public TextView tvState;

    public ActivityRecyclerViewHolder(final View parent,
                                      Context context,
                                      CardView mCardView,
                                      ImageView imageView,
                                      TextView tvTheme,
                                      TextView tvSchoolName,
                                      TextView tvTime,
                                      TextView tvBegin,
                                      TextView tvEnd,
                                      TextView tvPlaceContent,
                                      CheckBox cbComment,
                                      CheckBox cbJoin,
                                      CheckBox cbLike,
                                      CardView cvState,
                                      TextView tvState) {
        super(parent);
        this.context = context;
        this.mCardView = mCardView;
        this.imageView = imageView;
        this.tvTheme = tvTheme;
        this.tvSchoolName = tvSchoolName;
        this.tvTime = tvTime;
        this.tvBegin = tvBegin;
        this.tvEnd = tvEnd;
        this.tvPlaceContent = tvPlaceContent;
        this.cbComment = cbComment;
        this.cbJoin = cbJoin;
        this.cbLike = cbLike;
        this.cvState = cvState;
        this.tvState = tvState;
    }

    public static ActivityRecyclerViewHolder newInstance(Context context,View parent) {
        CardView cardView = (CardView) parent.findViewById(R.id.cv_activity);
        ImageView imageView = (ImageView) parent.findViewById(R.id.iv_activity);
        TextView tvTheme = (TextView) parent.findViewById(R.id.tv_theme);
        TextView tvSchoolName = (TextView) parent.findViewById(R.id.tv_school_name);
        TextView tvTime = (TextView) parent.findViewById(R.id.tv_time);
        TextView tvBegin = (TextView) parent.findViewById(R.id.tv_begin_content);
        TextView tvEnd = (TextView) parent.findViewById(R.id.tv_end_content);
        TextView tvPlaceContent = (TextView) parent.findViewById(R.id.tv_place_content);
        CheckBox cbJoin = (CheckBox) parent.findViewById(R.id.cb_join);
        CheckBox cbComment = (CheckBox) parent.findViewById(R.id.cb_comment);
        CheckBox cbLike = (CheckBox) parent.findViewById(R.id.cb_like);
        CardView cvState = (CardView) parent.findViewById(R.id.cv_state);
        TextView tvState = (TextView) parent.findViewById(R.id.tv_state);

        return new ActivityRecyclerViewHolder(parent,context,cardView,imageView,
                tvTheme,tvSchoolName,tvTime,tvBegin,tvEnd,tvPlaceContent,cbComment,
                cbJoin,cbLike,cvState,tvState);
    }

    //设置图片
    public void setImageView(String imageUrl) {
        Picasso.with(context).load(imageUrl).fit().centerCrop().into(imageView);
//        ImageOptions options = new ImageOptions.Builder().build();
//        x.image().bind(imageView, imageUrl, options);
    }

    public void setThemeText(CharSequence text) {
        tvTheme.setText(text);
    }

    public void setSchoolNameText(CharSequence text) {
        tvSchoolName.setText(text);
    }

    public void setTimeText(CharSequence text) {
        tvTime.setText(text);
    }

    public void setTvBeginText(CharSequence text) {
        tvBegin.setText(text);
    }

    public void setTvEndText(CharSequence text) {
        tvEnd.setText(text);
    }

    public void setPlaceText(CharSequence text) {
        tvPlaceContent.setText(text);
    }

    public void setCbCommentText(CharSequence text) {
        cbComment.setText(text);
    }

    public void setCbJoinText(CharSequence text) {
        cbJoin.setText(text);
    }

    public void setCbLikeText(CharSequence text) {
        cbLike.setText(text);
    }

    public void setStateText(CharSequence text) {
        tvState.setText(text);
    }

    public void setCardStateColor(@ColorInt int color) {
        cvState.setCardBackgroundColor(color);
    }

}
