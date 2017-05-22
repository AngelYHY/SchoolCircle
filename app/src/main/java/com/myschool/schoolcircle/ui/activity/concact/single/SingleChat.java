package com.myschool.schoolcircle.ui.activity.concact.single;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myschool.schoolcircle.adapter.SingleChatAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.GroupImageActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.ToastUtil;
import com.myschool.schoolcircle.widget.AudioRecorderButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.event.MessageBaseEvent;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.api.BasicCallback;

/**
 * Created by Mr.R on 2016/8/14.
 */
public class SingleChat extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_chat)
    Toolbar tbChat;
    @Bind(R.id.ib_audio)
    ImageButton ibAudio;
    @Bind(R.id.et_message)
    EditText etMessage;
    @Bind(R.id.ib_image)
    ImageButton ibImage;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.lv_chat)
    ListView lvChat;
    @Bind(R.id.layout_chat)
    RelativeLayout layoutChat;
    @Bind(R.id.srl_chat)
    SwipeRefreshLayout srlChat;
    @Bind(R.id.btn_send_voice)
    AudioRecorderButton btnSendVoice;
    @Bind(R.id.ll_input)
    RelativeLayout llInput;
    @Bind(R.id.rl_voice)
    RelativeLayout rlVoice;

    private UserInfo mUser;
    private Conversation mConversation;
    private String mTargetUsername;

    private SingleChatAdapter mAdapter;
    private List<Message> mMessages;

    private int offset = 0;
    private int limit = 15;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 1:
                    lvChat.setSelection(mMessages.size() - 1);
                    break;
                case HandlerKey.REFRESH_SUCCESS:
                    int size = (int) msg.obj;
                    mAdapter.notifyDataSetChanged();
                    lvChat.setSelection(size);
                    //设置列表自动滚动到对应位置
                    lvChat.smoothScrollToPosition(size - 1);
                    srlChat.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlChat.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_chat);
//        ButterKnife.bind(this);
//
//        JMessageClient.registerEventReceiver(this);
//        init();
//        initData();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化
    protected void initView() {
        JMessageClient.registerEventReceiver(this);

        Intent intent = getIntent();
        mTargetUsername = intent.getStringExtra("targetUsername");
        setSupportActionBar(tbChat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(intent.getStringExtra("nickName"));

        RefreshUtil.initRefreshView(srlChat, this);
        JMessageClient.enterSingleConversation(mTargetUsername);

        //发送语音按钮
        btnSendVoice.setOnAudioFinishRecorderListener(
                new AudioRecorderButton.AudioFinishRecorderListener() {
                    @Override
                    public void onFinish(float seconds, String filePath) {
                        sendVoiceMessage(filePath, (int) seconds);
                    }
                });

        initData();
    }

    //初始化数据
    private void initData() {
        mUser = application.getMyInfo();
        mMessages = new ArrayList<>();
        mAdapter = new SingleChatAdapter(this, mMessages, application.getMyInfo());
        lvChat.setAdapter(mAdapter);
        initMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //获取会话
    private Conversation getConversation(String targetUsername) {
        Conversation conversation = JMessageClient
                .getSingleConversation(targetUsername);
        if (conversation == null) {
            conversation = Conversation.createSingleConversation(targetUsername);
        }
        return conversation;
    }

    //初始化聊天记录
    private void initMessages() {
        mConversation = JMessageClient.getSingleConversation(mTargetUsername);
        if (mConversation != null) {
            List<Message> messages = mConversation.getMessagesFromNewest(offset, limit);
            if (messages.size() > 0) {
                for (Message message : messages) {
                    mMessages.add(0, message);
                }
                refreshUI();
                offset += limit;
            }
        }
    }

    //刷新UI
    private void refreshUI() {
        mAdapter.notifyDataSetChanged();
        //列表显示位置设置为最后一行
        lvChat.setSelection(mMessages.size() - 1);
    }

    @OnClick({R.id.ib_audio, R.id.ib_image, R.id.btn_send,
            R.id.et_message, R.id.ib_input, R.id.ib_voice_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_audio:
                llInput.setVisibility(View.INVISIBLE);
                rlVoice.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_input:
                llInput.setVisibility(View.VISIBLE);
                rlVoice.setVisibility(View.GONE);
                break;
            case R.id.ib_image:
                Intent intent1 = new Intent(this, GroupImageActivity.class);
                startActivityForResult(intent1, 1);
                break;
            case R.id.ib_voice_image:
                Intent intent2 = new Intent(this, GroupImageActivity.class);
                startActivityForResult(intent2, 1);
                break;
            case R.id.btn_send:
                //获取文本
                String text = etMessage.getText().toString();
                if (!text.isEmpty()) {
                    sendTextMessage(text);
                    etMessage.setText("");
                    hideSoftInput(etMessage);
                } else {
                    ToastUtil.showShortToast(this, R.string.chat_message_cannot_be_empty);
                }
                break;
            case R.id.et_message:
                mHandler.sendEmptyMessageDelayed(1, 100);
                break;
            default:
                break;
        }
    }

    //发送文本消息
    private void sendTextMessage(String text) {
        mConversation = getConversation(mTargetUsername);
        //判断会话是否为空
        if (mConversation != null) {
            //创建文本消息
            Message textMessage = mConversation
                    .createSendTextMessage(text, mUser.getNickname());
            textMessage.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        //发送成功，刷新界面
//                        ToastUtil.showToast(SingleChat.this, "发送成功", Toast.LENGTH_SHORT);
                    } else {
                        //发送失败
                        ToastUtil.showToast(SingleChat.this, "发送失败", Toast.LENGTH_SHORT);
                    }
                }
            });

            mMessages.add(textMessage);
            refreshUI();
            //发送消息
            JMessageClient.sendMessage(textMessage);
        }
    }

    //发送图片消息
    private void sendImageMessage(String imageUri) {
        mConversation = getConversation(mTargetUsername);
        //判断会话是否为空
        if (mConversation != null) {
            //创建图片消息
            Message message = null;
            try {
                message = mConversation
                        .createSendImageMessage(new File(imageUri), mUser.getNickname());
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            //发送成功，刷新界面
//                            ToastUtil.showToast(SingleChat.this, "发送成功", Toast.LENGTH_SHORT);
                        } else {
                            //发送失败
                            ToastUtil.showToast(SingleChat.this, "发送失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            mMessages.add(message);
            refreshUI();
            //发送消息
            JMessageClient.sendMessage(message);
        }
    }

    //发送语音消息
    private void sendVoiceMessage(String voiceUri, int duration) {
        mConversation = getConversation(mTargetUsername);
        //判断会话是否为空
        if (mConversation != null) {
            //创建语音消息
            Message message = null;
            try {
                message = mConversation.createSendVoiceMessage(
                        new File(voiceUri), duration, mUser.getNickname());
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            //发送成功，刷新界面
//                            ToastUtil.showToast(SingleChat.this, "发送成功", Toast.LENGTH_SHORT);
                        } else {
                            //发送失败
                            ToastUtil.showToast(SingleChat.this, "发送失败", Toast.LENGTH_SHORT);
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            mMessages.add(message);
            refreshUI();
            //发送消息
            JMessageClient.sendMessage(message);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //获取每一张图片的本地地址
            ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra("images");
            for (String image : images) {
                sendImageMessage(image);
            }
        }
    }

    //    //消息接收事件监听
    public void onEventMainThread(MessageBaseEvent event) {
        Message msg = event.getMessage();
        switch (msg.getContentType()) {
            case text:
            case image:
            case voice:
                mMessages.add(msg);
                refreshUI();
                break;

            case eventNotification:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //重置未读信息数
        if (mConversation != null) {
            mConversation.resetUnreadCount();
        }
        JMessageClient.exitConversation();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_chat;
    }

    //刷新
    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {

                try {

                    //加载聊天记录
                    List<Message> newMessages = mConversation
                            .getMessagesFromNewest(offset, limit);
                    int size = newMessages.size();
                    if (size > 0) {
                        //如果加载出的数据数量大于0
                        for (Message newMessage : newMessages) {
                            mMessages.add(0, newMessage);
                        }
                        offset += limit;
                        android.os.Message msg = android.os.Message.obtain();
                        msg.what = HandlerKey.REFRESH_SUCCESS;
                        msg.obj = size;
                        mHandler.sendMessageDelayed(msg, 300);
                    } else {
                        //没有加载出数据
                        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 300);
                    }

                } catch (Exception e) {
                    mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 300);
                }

            }
        }.start();

    }
}
