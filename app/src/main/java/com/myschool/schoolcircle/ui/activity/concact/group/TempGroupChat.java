package com.myschool.schoolcircle.ui.activity.concact.group;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.myschool.schoolcircle.adapter.GroupChatAdapter;
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
import cn.jpush.im.api.BasicCallback;

public class TempGroupChat extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_chat)
    Toolbar tbChat;
    @Bind(R.id.et_message)
    EditText etMessage;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.lv_chat)
    ListView lvChat;
    @Bind(R.id.ib_audio)
    ImageButton ibAudio;
    @Bind(R.id.ib_image)
    ImageButton ibImage;
    @Bind(R.id.srl_chat)
    SwipeRefreshLayout srlChat;
    @Bind(R.id.btn_send_voice)
    AudioRecorderButton btnSendVoice;
    @Bind(R.id.ll_input)
    RelativeLayout llInput;
    @Bind(R.id.rl_voice)
    RelativeLayout rlVoice;
    @Bind(R.id.layout_chat)
    RelativeLayout layoutChat;

    private Conversation mConversation;
    private GroupChatAdapter mAdapter;
    private List<Message> mMessages;
    private long groupId;

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
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_group_chat);
//        ButterKnife.bind(this);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        //初始化
//        init();
//    }

    //初始化
    private void init() {
        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        Intent intent = getIntent();
        groupId = intent.getLongExtra("groupId",0);
        //进入群聊
        mConversation = JMessageClient.getGroupConversation(groupId);
        JMessageClient.enterGroupConversation(groupId);
//        initView();
    }

    protected void initView() {
        init();

        setSupportActionBar(tbChat);
        getSupportActionBar().setHomeButtonEnabled(true);//设置标题栏的按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mConversation.getTitle());

        RefreshUtil.initRefreshView(srlChat, this);

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

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_group_chat;
    }

    //初始化数据
    private void initData() {
        mMessages = new ArrayList<>();
        mAdapter = new GroupChatAdapter(
                TempGroupChat.this, mMessages, application.getMyInfo());
        lvChat.setAdapter(mAdapter);
        initMessages();
    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //获取群聊会话
    private Conversation getConversation(long groupId) {
        Conversation conv = JMessageClient.getGroupConversation(groupId);
        if (conv == null) {
            conv = Conversation.createGroupConversation(groupId);
        }
        return conv;
    }

    @OnClick({R.id.ib_audio, R.id.ib_image, R.id.btn_send,
            R.id.et_message, R.id.ib_input, R.id.ib_voice_image})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_audio:
                //发送语音
                llInput.setVisibility(View.INVISIBLE);
                rlVoice.setVisibility(View.VISIBLE);
                break;
            case R.id.ib_input:
                llInput.setVisibility(View.VISIBLE);
                rlVoice.setVisibility(View.GONE);
                break;
            case R.id.btn_send:
                //发送文字聊天消息
                String msg = etMessage.getText().toString();
                if (!msg.isEmpty()) {
                    sendTextMessage(msg);
                    etMessage.setText("");
                    hideSoftInput(etMessage);
                } else {
                    ToastUtil.showShortToast(TempGroupChat.this,
                            R.string.chat_message_cannot_be_empty);
                }
                break;
            case R.id.ib_image:
                //打开图片选择界面
                Intent intent = new Intent(TempGroupChat.this, GroupImageActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.ib_voice_image:
                Intent intent2 = new Intent(this, GroupImageActivity.class);
                startActivityForResult(intent2, 1);
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
        //判断会话是否为空
        if (mConversation != null) {
            //创建文本消息
            Message textMessage = mConversation
                    .createSendTextMessage(text, application.getMyInfo().getNickname());
            textMessage.setOnSendCompleteCallback(new BasicCallback() {
                @Override
                public void gotResult(int i, String s) {
                    if (i == 0) {
                        //发送成功，刷新界面
                        ToastUtil.showToast(TempGroupChat.this, "发送成功", Toast.LENGTH_SHORT);
                    } else {
                        //发送失败
                        ToastUtil.showToast(TempGroupChat.this, "发送失败", Toast.LENGTH_SHORT);
                    }
                }
            });

            mMessages.add(textMessage);
            refreshUI();
            //发送消息
            JMessageClient.sendMessage(textMessage);
        } else {
            //重新获取会话
            mConversation = getConversation(groupId);
            //递归
            sendTextMessage(text);
        }
    }

    //发送图片消息
    private void sendImageMessage(String imageUri) {
        //判断会话是否为空
        if (mConversation != null) {
            //创建图片消息
            Message message = null;
            try {
                message = mConversation
                        .createSendImageMessage(new File(imageUri),
                                application.getMyInfo().getUserName());
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            //发送成功，刷新界面
                            ToastUtil.showToast(TempGroupChat.this,
                                    "发送成功", Toast.LENGTH_SHORT);
                        } else {
                            //发送失败
                            ToastUtil.showToast(TempGroupChat.this,
                                    "发送失败", Toast.LENGTH_SHORT);
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
        } else {
            //重新获取会话
            mConversation = getConversation(groupId);
            //递归
            sendImageMessage(imageUri);
        }
    }

    //发送语音消息
    private void sendVoiceMessage(String voiceUri, int duration) {
        //判断会话是否为空
        if (mConversation != null) {
            //创建语音消息
            Message message = null;
            try {
                message = mConversation
                        .createSendVoiceMessage(new File(voiceUri), duration,
                                application.getMyInfo().getUserName());
                message.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        if (i == 0) {
                            //发送成功，刷新界面
                            ToastUtil.showToast(TempGroupChat.this,
                                    "发送成功", Toast.LENGTH_SHORT);
                        } else {
                            //发送失败
                            ToastUtil.showToast(TempGroupChat.this,
                                    "发送失败", Toast.LENGTH_SHORT);
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
        } else {
            //重新获取会话
            mConversation = getConversation(groupId);
            //递归
            sendVoiceMessage(voiceUri, duration);
        }
    }

    //接收从上一个界面返回的值
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

    //消息接收事件监听
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

    //初始化聊天记录
    private void initMessages() {
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

    //刷新
    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {

                try {

                    Log.i("GroupChat", "run: GroupChat开始加载");
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
                        Log.i("GroupChat", "run: GroupChat有数据");
                    } else {
                        Log.i("GroupChat", "run: GroupChat没有数据");
                        //没有加载出数据
                        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 300);
                    }

                } catch (Exception e) {
                    Log.i("GroupChat", "run: GroupChat错误");
                    e.printStackTrace();
                    mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_FAIL, 300);
                }

            }
        }.start();

    }

    //标题栏的监听事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //关闭当前界面
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        //事件接收类的解绑
        JMessageClient.unRegisterEventReceiver(this);
        //退出群聊
        JMessageClient.exitConversation();
        super.onDestroy();
    }
}