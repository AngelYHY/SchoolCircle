package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.List;

/**
 * Created by Mr.R on 2016/8/29.
 */
public class NineGridViewAdapter extends NineGridViewClickAdapter {

    public NineGridViewAdapter(Context context, List<ImageInfo> imageInfo) {
        super(context, imageInfo);
    }
}
