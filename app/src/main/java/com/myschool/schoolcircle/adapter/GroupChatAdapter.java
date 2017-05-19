package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
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
import com.myschool.schoolcircle.entity.Tb_chat_message;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.DateUtil;

import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.content.ImageContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.content.VoiceContent;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Mr.R on 2016/6/6.
 */
public class GroupChatAdapter extends BaseAdapter {
    private static final int ME_TEXT_MSG = 1;
    private static final int ME_IMAGE_MSG = 2;
    private static final int ME_AUDIO_MSG = 3;
    private static final int FRIEND_TEXT_MSG = 4;
    private static final int FRIEND_IMAGE_MSG = 5;
    private static final int FRIEND_AUDIO_MSG = 6;

    private static final int TEXT_MSG = 7;
    private static final int IMAGE_MSG = 8;
    private static final int AUDIO_MSG = 9;

    private Context context;
    private UserInfo info;
    private LayoutInflater messageInflater;
    private List<Message> dataList;
    private MediaPlayer mMediaPlayer;

    public GroupChatAdapter(Context context, List<Message> list, UserInfo info) {
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
            viewHolder = new ViewHolder();
            int flag = getItemViewType(position);
            if (0 < flag && flag < 4) {
                convertView = messageInflater.inflate(R.layout.item_me_message,null);
            } else if (flag < 7) {
                convertView = messageInflater.inflate(R.layout.item_friend_message,null);
            }
            switch (flag) {
                //文字消息
                case ME_TEXT_MSG:
                case FRIEND_TEXT_MSG:
                    viewHolder.type = TEXT_MSG;
                    viewHolder.tv_text = (TextView) convertView.findViewById(R.id.tv_text);
                    break;
                //图片消息
                case ME_IMAGE_MSG:
                case FRIEND_IMAGE_MSG:
                    viewHolder.type = IMAGE_MSG;
                    viewHolder.nineGridView = (NineGridView) convertView.findViewById(R.id.iv_image);
                    break;
                //语音消息
                case ME_AUDIO_MSG:
                case FRIEND_AUDIO_MSG:
                    viewHolder.type = AUDIO_MSG;
                    viewHolder.iv_voice = (ImageView) convertView.findViewById(R.id.iv_voice);
                    break;
                default:
                    break;
            }
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.iv_head = (ImageView) convertView.findViewById(R.id.iv_head);
            viewHolder.rlMessage = (RelativeLayout) convertView.findViewById(R.id.rl_message);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Message msg = dataList.get(position);
        //发送者名字
        UserInfo fromUser = msg.getFromUser();
        viewHolder.tv_name.setText(fromUser.getNickname());
        //消息创建时间
        viewHolder.tv_time.setText(DateUtil.formatDateTime(new Date(msg.getCreateTime())));
        //头像
        File avatarFile = fromUser.getAvatarFile();
        if (avatarFile != null) {
            Bitmap avatar = BitmapFactory.decodeFile(avatarFile.getPath());
            viewHolder.iv_head.setImageBitmap(avatar);
        }

        switch (viewHolder.type) {
            case TEXT_MSG:
                TextContent content = (TextContent) msg.getContent();
                viewHolder.tv_text.setVisibility(View.VISIBLE);
                viewHolder.tv_text.setText(content.getText());
                break;
            case IMAGE_MSG:
                //处理图片消息
                viewHolder.nineGridView.setVisibility(View.VISIBLE);
                ImageContent imageContent = (ImageContent) msg.getContent();
                String thumbnailUrl = imageContent.getLocalThumbnailPath();//图片对应缩略图的本地地址
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(thumbnailUrl);
                info.setBigImageUrl(thumbnailUrl);
                List<ImageInfo> imageInfo = new ArrayList<>();
                imageInfo.add(info);
                viewHolder.nineGridView.setAdapter(new NineGridViewClickAdapter(context,imageInfo));
                break;
            case AUDIO_MSG:
                //处理语音消息
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
        public ImageView iv_voice;
        public NineGridView nineGridView;
        public ImageView iv_head;
        public TextView tv_name;
        public TextView tv_text;
    }
}
