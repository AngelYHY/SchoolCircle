package com.myschool.schoolcircle.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/7/13.
 */
public class MyViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    public TextView tv_text;

//    public MyViewHolder(View itemView) {
//        super(itemView);
//        tv_text = (TextView) itemView.findViewById(R.id.itemTextView);
//    }

    private MyViewHolder(View convertView) {
        super(convertView);
        Log.i("adapter","MyViewHolder");
        this.mViews = new SparseArray<>();
        //解析布局
        this.mConvertView = convertView;
        mConvertView.setTag(this);
    }

    private MyViewHolder(View convertView, int position) {
        super(convertView);
        this.mViews = new SparseArray<>();
        this.mPosition = position;
        //解析布局
        this.mConvertView = convertView;
        mConvertView.setTag(this);
    }

    /**
     *获取ViewHolder对象
     * @param convertView:item的视图
     * @param position:当前item的id
     * @return:返回ViewHolder对象
     */
    public static MyViewHolder get(View convertView, int position) {
        if (convertView == null) {
            return new MyViewHolder(convertView,position);
        } else {
            MyViewHolder holder = (MyViewHolder) convertView.getTag();
            holder.mPosition = position;//更新position
            return holder;
        }
    }

}
