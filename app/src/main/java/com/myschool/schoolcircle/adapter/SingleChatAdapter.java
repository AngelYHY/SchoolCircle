package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.myschool.schoolcircle.lib.CircleImageView;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.DateUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Mr.R on 2016/6/6.
 */
public class SingleChatAdapter extends BaseAdapter {
    private static final int ME_TEXT_MSG = 1;
    private static final int ME_IMAGE_MSG = 3;
    private static final int ME_AUDIO_MSG = 5;
    private static final int FRIEND_TEXT_MSG = 2;
    private static final int FRIEND_IMAGE_MSG = 4;
    private static final int FRIEND_AUDIO_MSG = 6;

    private static final int TEXT_MSG = 7;
    private static final int IMAGE_MSG = 8;
    private static final int AUDIO_MSG = 9;

    private UserInfo info;
    private LayoutInflater messageInflater;
    private List<Message> dataList;
    private MediaPlayer mMediaPlayer;
    private Context context;

    public SingleChatAdapter(Context context, List<Message> list, UserInfo info) {
        this.context = context;
        this.info = info;
        messageInflater = LayoutInflater.from(context);
        dataList = list;
        mMediaPlayer = new MediaPlayer();
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
        Message message = dataList.get(position);
        if (message.getFromUser().getUserName().equals(info.getUserName())) {
            switch (message.getContentType()) {
                case text:
                    return ME_TEXT_MSG;
                case image:
                    return ME_IMAGE_MSG;
                case voice:
                    return ME_AUDIO_MSG;
                default:
                    break;
            }
        } else {
            switch (message.getContentType()) {
                case text:
                    return FRIEND_TEXT_MSG;
                case image:
                    return FRIEND_IMAGE_MSG;
                case voice:
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
                    viewHolder.iv_head = (CircleImageView) convertView
                            .findViewById(R.id.iv_head);
                    viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                    break;
                case ME_IMAGE_MSG:
                    //我发送的图片消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_me_message,null);
                    viewHolder.type = IMAGE_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (CircleImageView) convertView.findViewById(R.id.iv_head);
                    viewHolder.iv_image = (NineGridView) convertView.findViewById(R.id.iv_image);
                    break;
                case ME_AUDIO_MSG:
                    //我发送的语音消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_me_message,null);
                    viewHolder.type = AUDIO_MSG;
                    viewHolder.rlMessage = (RelativeLayout) convertView
                            .findViewById(R.id.rl_message);
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (CircleImageView) convertView.findViewById(R.id.iv_head);
                    viewHolder.iv_voice = (ImageView) convertView.findViewById(R.id.iv_voice);

                    break;
                case FRIEND_TEXT_MSG:
                    //好友发送的文字消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_friend_message,null);
                    viewHolder.type = TEXT_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (CircleImageView) convertView
                            .findViewById(R.id.iv_head);
                    viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                    break;
                case FRIEND_IMAGE_MSG:
                    //好友发送的图片消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_friend_message,null);
                    viewHolder.type = IMAGE_MSG;
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (CircleImageView) convertView
                            .findViewById(R.id.iv_head);
                    viewHolder.iv_image = (NineGridView) convertView.findViewById(R.id.iv_image);
                    break;
                case FRIEND_AUDIO_MSG:
                    //好友发送的语音消息
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_friend_message,null);
                    viewHolder.type = AUDIO_MSG;
                    viewHolder.rlMessage = (RelativeLayout) convertView
                            .findViewById(R.id.rl_message);
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (CircleImageView) convertView
                            .findViewById(R.id.iv_head);
                    viewHolder.iv_voice = (ImageView) convertView.findViewById(R.id.iv_voice);
                    break;
                default:
                    viewHolder = new ViewHolder();
                    convertView = messageInflater.inflate(R.layout.item_friend_message,null);
                    viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                    viewHolder.iv_head = (CircleImageView) convertView
                            .findViewById(R.id.iv_head);
                    viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message msg = dataList.get(position);
        //消息创建时间
        viewHolder.tv_time.setText(DateUtil.formatDateTime(new Date(msg.getCreateTime())));
        //头像
        File avatarFile = msg.getFromUser().getAvatarFile();
        if (avatarFile != null) {
            Bitmap avatar = BitmapFactory.decodeFile(avatarFile.getPath());
            viewHolder.iv_head.setImageBitmap(avatar);
        }

        switch (viewHolder.type) {
            case TEXT_MSG:
                //处理文字信息
                TextContent textContent = (TextContent) msg.getContent();
                viewHolder.tv_text.setVisibility(View.VISIBLE);
                viewHolder.tv_text.setText(textContent.getText());
                break;

            case IMAGE_MSG:
                //处理图片消息
                viewHolder.iv_image.setVisibility(View.VISIBLE);
                ImageContent imageContent = (ImageContent) msg.getContent();
                String thumbnailUrl = imageContent.getLocalThumbnailPath();//图片对应缩略图的本地地址
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(thumbnailUrl);
                info.setBigImageUrl(thumbnailUrl);
                List<ImageInfo> imageInfo = new ArrayList<>();
                imageInfo.add(info);
                viewHolder.iv_image.setAdapter(new NineGridViewClickAdapter(context,imageInfo));
                break;
            case AUDIO_MSG:
                //处理语音信息
                viewHolder.iv_voice.setVisibility(View.VISIBLE);
                VoiceContent voiceContent = (VoiceContent) msg.getContent();
                int duration = voiceContent.getDuration();
                final String voicePath = voiceContent.getLocalPath();
                viewHolder.rlMessage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        play(voicePath);
                    }
                });
                break;

            default:
                CustomContent content = (CustomContent) msg.getContent();
                viewHolder.tv_text.setVisibility(View.VISIBLE);
                viewHolder.tv_text.setText(content.getStringValue("content"));
                break;
        }

        return convertView;
    }

    //播放语音
    private void play(String path){
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.start();
    }

    class ViewHolder {
        public int type;
        public ProgressBar pb_message;
        public RelativeLayout rlMessage;
        public TextView tv_time;
        public NineGridView iv_image;
        public ImageView iv_voice;
        public CircleImageView iv_head;
        public TextView tv_name;
        public TextView tv_text;
    }
}
