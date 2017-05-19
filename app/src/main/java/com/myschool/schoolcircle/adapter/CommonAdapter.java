package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * Created by Mr.R on 2016/7/8.
 */
public abstract class CommonAdapter<T> extends BaseAdapter {
    protected Context context;
    protected List<T> mData;
    private int layoutId;

    public CommonAdapter(Context context, List<T> data, int layoutId) {
        this.context = context;
        this.mData = data;
        this.layoutId = layoutId;
    }

    @Override
    public int getCount() {
        return mData==null ? 0 : mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData==null ? null : mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = ViewHolder.get(context,view,viewGroup, layoutId,i);
        convert(holder,getItem(i));

        return holder.getConvertView();
    }

    //给item设置控件和内容
    public abstract void convert(ViewHolder holder, T t);
}
