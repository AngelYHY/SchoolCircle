package com.myschool.schoolcircle.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.myschool.schoolcircle.lib.CircleImageView;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.DateUtil;

import java.io.File;
import java.util.Date;
import java.util.List;

import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Mr.R on 2016/7/11.
 */
public class MessageAdapter extends CommonAdapter<Conversation> {

    public MessageAdapter(Context context, List<Conversation> data, int layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, Conversation conversation) {
        //获取最后一条消息
        Message msg = conversation.getLatestMessage();
        if (msg != null) {
            //消息创建时间
            holder.setText(R.id.tv_time, DateUtil.formatDateTime(new Date(msg.getCreateTime())));
            //未读消息数
            int num = conversation.getUnReadMsgCnt();
            CardView cv_unread_num = holder.getView(R.id.cv_unread_num);
            if (num > 0) {
                cv_unread_num.setVisibility(View.VISIBLE);
                if (num > 99) {
                    holder.setText(R.id.tv_unread_num, "99+");
                } else {
                    holder.setText(R.id.tv_unread_num, num + "");
                }
            } else {
                cv_unread_num.setVisibility(View.INVISIBLE);
            }

            //消息对象的头像
            CircleImageView circleImageView = holder.getView(R.id.civ_avatar);
            switch (conversation.getType()) {
                case single:
                    File avatarFile = conversation.getAvatarFile();
                    if (avatarFile != null) {
                        //如果头像不为空就设置头像
                        Glide.with(context).load(avatarFile).into(circleImageView);
                    }
                    break;
                case group:
                    circleImageView.setImageResource(R.mipmap.ic_group_48dp);
                    break;
            }

            Object info = conversation.getTargetInfo();
            String title = "";
            conversation.getType();
            if (info instanceof UserInfo) {
                title = ((UserInfo)info).getNickname();
            } else {
                title = ((GroupInfo)info).getGroupName();
            }
            //消息类型图标
            ImageView ivType = holder.getView(R.id.iv_type);
            switch (msg.getContentType()) {
                case text:
                    //处理文字消息
                    ivType.setImageResource(R.mipmap.ic_textsms_gray_18dp);
                    TextContent content = (TextContent) msg.getContent();
                    holder.setText(R.id.tv_name, title);
                    TextView tvText = holder.getView(R.id.tv_content);
                    tvText.setText(content.getText());
                    break;
                case image:
                    ivType.setImageResource(R.mipmap.ic_image_gray_18dp);
                    holder.setText(R.id.tv_name, conversation.getTitle());
                    TextView tvImage = holder.getView(R.id.tv_content);
                    tvImage.setText("图片消息");
                    break;
                case voice:
                    ivType.setImageResource(R.mipmap.ic_volume_up_gray_18dp);
                    holder.setText(R.id.tv_name, conversation.getTitle());
                    TextView tvVoice = holder.getView(R.id.tv_content);
                    tvVoice.setText("语音消息");
                    break;
                default:
                    break;
            }
        }
    }
}
