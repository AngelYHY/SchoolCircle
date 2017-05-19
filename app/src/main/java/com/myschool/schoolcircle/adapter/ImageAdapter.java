package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.ImageLoader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Mr.R on 2016/8/3.
 */
public class ImageAdapter extends BaseAdapter {
    public ArrayList<String> mSelected = new ArrayList<>();//保存图片的是否选择中状态
    private String mDirPath;
    private List<String> mImagePaths;
    private LayoutInflater mInflater;

    public ImageAdapter(Context context, List<String> mData, String dirPath) {
        this.mDirPath = dirPath;
        this.mImagePaths = mData;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mImagePaths.size();
    }

    @Override
    public Object getItem(int i) {
        return mImagePaths.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_image,viewGroup,false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) view.findViewById(R.id.iv_image);
            viewHolder.imageButton = (ImageButton) view.findViewById(R.id.ib_check);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        //重置状态
        viewHolder.imageView.setImageResource(R.drawable.ic_crop_original_white_48dp);
        viewHolder.imageButton.setImageResource(R.mipmap.ic_radio_button_unchecked_white_24dp);
        viewHolder.imageView.setColorFilter(null);

        final String filePath = mDirPath+"/"+mImagePaths.get(i);
        ImageLoader.getInstance(3, ImageLoader.Type.LIFO)
                .loadImage(filePath,viewHolder.imageView);

        //图片的单击事件
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSelected.contains(filePath)) {
                    //已被选中,就移除
                    mSelected.remove(filePath);
                    viewHolder.imageView.setColorFilter(null);
                    viewHolder.imageButton.setImageResource(
                            R.mipmap.ic_radio_button_unchecked_white_24dp);
                } else {
                    mSelected.clear();
                    //未被选中，就添加
                    mSelected.add(filePath);
                    viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
                    viewHolder.imageButton.setImageResource(R.mipmap.ic_radio_button_checked_white_24dp);
                    notifyDataSetChanged();
                }
            }
        });

        if (mSelected.contains(filePath)) {
            viewHolder.imageView.setColorFilter(Color.parseColor("#77000000"));
            viewHolder.imageButton.setImageResource(
                    R.mipmap.ic_radio_button_checked_white_24dp);
        }

        return view;
    }

    private class ViewHolder {
        ImageView imageView;
        ImageButton imageButton;
    }
}