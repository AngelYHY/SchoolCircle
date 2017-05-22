package com.myschool.schoolcircle.adapter;

import android.support.annotation.LayoutRes;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.widget.MyBaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class DynamicRecyclerAdapterDemo extends BaseQuickAdapter<Tb_dynamic, MyBaseViewHolder> {

    public DynamicRecyclerAdapterDemo(@LayoutRes int layoutResId) {
        super(layoutResId, null);
    }

    @Override
    protected void convert(MyBaseViewHolder holder, Tb_dynamic item) {
        holder.setIV(mContext, R.id.iv_head, item.getAvatar())
                .setText(R.id.tv_real_name, item.getRealName())
                .setText(R.id.tv_school_name, "来自[" + item.getSchoolName() + "]")
                .setText(R.id.tv_text_content, item.getTextContent())
                .setText(R.id.tv_time, item.getDatetime())
                .setText(R.id.cb_comment, item.getCommentNum() + "")
                .setText(R.id.cb_like, item.getLikeNum() + "")
                .addOnClickListener(R.id.cb_retransmission)
                .addOnClickListener(R.id.cb_like);

        ArrayList<ImageInfo> imageInfo = new ArrayList<>();
        List<String> images = item.getImages();
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
        ((NineGridView) holder.getView(R.id.ngv_dynamic)).setAdapter(new NineGridViewClickAdapter(mContext, imageInfo));

    }
}




