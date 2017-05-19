package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

/**
 * Created by Mr.R on 2016/7/8.
 */
public class ViewHolder {
    private SparseArray<View> mViews;
    private int mPosition;
    private View mConvertView;

    /**
     * 构造方法（私有化）
     * @param context:上下文
     * @param parent:viewGroup
     * @param layoutId:布局的id
     * @param position:item的位置
     */
    private ViewHolder(Context context, ViewGroup parent, int layoutId, int position) {
        this.mViews = new SparseArray<>();
        this.mPosition = position;
        //解析布局
        this.mConvertView = LayoutInflater.from(context).inflate(layoutId,parent,false);
        mConvertView.setTag(this);
    }

    /**
     *获取ViewHolder对象
     * @param context:上下文
     * @param convertView:item的视图
     * @param parent:viewGroup
     * @param layoutId:布局的id
     * @param position:当前item的id
     * @return:返回ViewHolder对象
     */
    public static ViewHolder get(Context context, View convertView,
                                 ViewGroup parent, int layoutId, int position) {
        if (convertView == null) {
            return new ViewHolder(context,parent,layoutId,position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;//更新position
            return holder;
        }
    }

    /**
     * 根据控件的id获取该控件
     * @param viewId:控件的id
     * @param <T>:控件的类型
     * @return:返回控件
     */
    public <T extends View> T getView(int viewId) {
        View view = mViews.get(viewId);

        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId,view);
        }

        return (T)view;
    }

    /**
     * 获取item视图
     * @return:返回当前视图
     */
    public View getConvertView() {
        return mConvertView;
    }

    public ViewHolder setText(int viewId,String text) {
        TextView textView = getView(viewId);
        textView.setText(text);

        return this;
    }

    public ViewHolder setImageView(int viewId,int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);

        return this;
    }

    public ViewHolder setImageView(int viewId,String url) {
        ImageView imageView = getView(viewId);
        ImageOptions options = new ImageOptions.Builder()
                .setCircular(true)
                .setLoadingDrawableId(R.mipmap.ic_head)
                .setFailureDrawableId(R.mipmap.ic_head).build();
        x.image().bind(imageView,url,options);

        return this;
    }

    public ViewHolder setCheckBoxText(int viewId, String text) {
        CheckBox checkBox = getView(viewId);
        checkBox.setText(text);
        return this;
    }

    public ViewHolder setCardViewColor(int viewId, int color) {
        CardView cardView = getView(viewId);
        cardView.setCardBackgroundColor(color);
        return this;
    }

    public ViewHolder setCheckBox(int viewId,int resId) {
        CheckBox checkBox = getView(viewId);
//        checkBox.setImageResource(resId);

        return this;
    }
}
