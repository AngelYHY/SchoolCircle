package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myschool.schoolcircle.entity.Tb_chat_message;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by Mr.R on 2016/6/6.
 */
public class ChatAdapter extends BaseAdapter {
    private static final int ME_TEXT_MSG = 1;
    private static final int ME_IMAGE_MSG = 3;
    private static final int ME_AUDIO_MSG = 5;
    private static final int FRIEND_TEXT_MSG = 2;
    private static final int FRIEND_IMAGE_MSG = 4;
    private static final int FRIEND_AUDIO_MSG = 6;

    private static final int TEXT_MSG = 7;
    private static final int IMAGE_MSG = 8;
    private static final int AUDIO_MSG = 9;

    private Tb_user user;
    private LayoutInflater messageInflater;
    private List<Tb_chat_message> dataList;
    private ImageOptions headImageOptions = new ImageOptions.Builder()
            .setCircular(true).setLoadingDrawableId(R.mipmap.ic_head)
            .setFailureDrawableId(R.mipmap.ic_head).build();

    private ImageOptions imageOptions = new ImageOptions.Builder()
            .setCrop(true)
            .setRadius(10)
            .setLoadingDrawableId(R.mipmap.ic_head)
            .setFailureDrawableId(R.mipmap.ic_head).build();

    public ChatAdapter(Context context, List<Tb_chat_message> list, Tb_user user) {
        this.user = user;
        messageInflater = LayoutInflater.from(context);
        dataList = list;
    }

    //获取数据量
    @Override
    public int getCount() {
        return dataList.size();
    }

    //获取对应id项的item
    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    //获取对应id项
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Tb_chat_message chatMessage = dataList.get(position);
        if (chatMessage.getSender().getUsername().equals(user.getUsername())) {
            switch (chatMessage.getType()) {
                case "text":
                    return ME_TEXT_MSG;
                case "image":
                    return ME_IMAGE_MSG;
                case "audio":
                    return ME_AUDIO_MSG;
                default:
                    break;
            }
        } else {
            switch (chatMessage.getType()) {
                case "text":
                    return FRIEND_TEXT_MSG;
                case "image":
                    return FRIEND_IMAGE_MSG;
                case "audio":
                    return FRIEND_AUDIO_MSG;
                default:
                    break;
            }
        }
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 7;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;

        if (convertView == null) {
            switch (getItemViewType(position)) {
                case ME_TEXT_MSG:
                    //我发送的文字消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_me_message,null);
                    viewHolder.type = TEXT_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
                    viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                    break;
                case ME_IMAGE_MSG:
                    //我发送的图片消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_me_message,null);
                    viewHolder.type = IMAGE_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
                    viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                    break;
                case ME_AUDIO_MSG:
                    //我发送的语音消息

                    break;
                case FRIEND_TEXT_MSG:
                    //好友发送的文字消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_friend_message,null);
                    viewHolder.type = TEXT_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
                    viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                    break;
                case FRIEND_IMAGE_MSG:
                    //好友发送的图片消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_friend_message,null);
                    viewHolder.type = IMAGE_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
                    viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.iv_image);
                    break;
                case FRIEND_AUDIO_MSG:
                    //好友发送的语音消息

                    break;

                default:
                    break;
            }

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Tb_chat_message item = dataList.get(position);
        Tb_user sender = item.getSender();
        String time = item.getTime();
        viewHolder.tv_time.setText(time);
        x.image().bind(viewHolder.iv_head,sender.getHeadView(),headImageOptions);

        switch (viewHolder.type) {
            case TEXT_MSG:
                viewHolder.tv_text.setVisibility(View.VISIBLE);
                viewHolder.tv_text.setText(item.getContent());
                break;
            case IMAGE_MSG:
                viewHolder.iv_image.setVisibility(View.VISIBLE);
                x.image().bind(viewHolder.iv_image,item.getContent(),imageOptions);
                break;
            case AUDIO_MSG:

                break;

            default:
                break;
        }


        return convertView;
    }

    class ViewHolder {
        public int type;
        public ProgressBar pb_message;
        public TextView tv_time;
        public ImageView iv_image;
        public ImageView iv_head;
        public TextView tv_name;
        public TextView tv_text;
    }
}
