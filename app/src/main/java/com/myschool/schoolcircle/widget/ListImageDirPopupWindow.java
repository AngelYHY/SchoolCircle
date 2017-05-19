package com.myschool.schoolcircle.widget;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.myschool.schoolcircle.Beans.FolderBean;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.ImageLoader;

import java.util.List;

/**
 * Created by Mr.R on 2016/8/4.
 */
public class ListImageDirPopupWindow extends PopupWindow {
    private int mWidth;
    private int mHeight;
    private View mConvertView;
    private List<FolderBean> mData;
    private ListView mListView;

    public interface OnDirSelectedListener {
        void onSelected(FolderBean folderBean);
    }

    public OnDirSelectedListener mListener;

    public void setOnDirSelectedListener(OnDirSelectedListener listener) {
        this.mListener = listener;
    }

    public ListImageDirPopupWindow(Context context, List<FolderBean> data) {
        calWidthAndHeight(context);

        mConvertView = LayoutInflater.from(context).inflate(R.layout.layout_popup_picture,null);
        mData = data;
        setContentView(mConvertView);
        setWidth(mWidth);
        setHeight(mHeight);

        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());

        setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        initView(context);
        initEvent();
    }

    private void initView(Context context) {
        mListView = (ListView) mConvertView.findViewById(R.id.lv_popup_picture);
        mListView.setAdapter(new ListDirAdapter(context,mData));
    }

    private void initEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (mListener != null) {
                    mListener.onSelected(mData.get(i));
                }
            }
        });
    }

    /**
     * 计算popupWindow的高度和宽度
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        mWidth = outMetrics.widthPixels;
        mHeight = (int) (outMetrics.heightPixels * 0.7);
    }

    private class ListDirAdapter extends ArrayAdapter<FolderBean>{
        private LayoutInflater mInflater;
        private List<FolderBean> mData;

        public ListDirAdapter(Context context, List<FolderBean> data) {
            super(context, 0, data);
            mInflater = LayoutInflater.from(context);
            mData = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_popup_picture,null,false);
                viewHolder.iv_folder_image = (ImageView) convertView
                        .findViewById(R.id.iv_folder_image);
                viewHolder.tv_folder_name = (TextView) convertView
                        .findViewById(R.id.tv_folder_name);
                viewHolder.tv_image_count = (TextView) convertView
                        .findViewById(R.id.tv_image_count);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

//            FolderBean bean = getItem(position);
            FolderBean bean = mData.get(position);

            //重置
            viewHolder.iv_folder_image.setImageResource(R.drawable.ic_crop_original_white_48dp);
            //加载数据
            ImageLoader.getInstance().loadImage(bean.getFirstImagePath(),viewHolder.iv_folder_image);
            viewHolder.tv_folder_name.setText(bean.getFolderName());
            viewHolder.tv_image_count.setText(bean.getCount()+"");

            return convertView;
        }

        private class ViewHolder {
            ImageView iv_folder_image;
            TextView tv_folder_name;
            TextView tv_image_count;
        }
    }
}
