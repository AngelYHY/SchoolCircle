package com.myschool.schoolcircle.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.myschool.schoolcircle.adapter.MessageAdapter;
import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.main.MainActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.concact.group.GroupChat;
import com.myschool.schoolcircle.ui.activity.concact.group.TempGroupChat;
import com.myschool.schoolcircle.ui.activity.concact.single.SingleChat;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Conversation;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class MessageFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, MainActivity.OnRefreshDataListener {

    @Bind(R.id.lv_message)
    ListView lvMessage;
    @Bind(R.id.srl_message)
    SwipeRefreshLayout srlMessage;
    @Bind(R.id.empty_view)
    LinearLayout emptyView;
    @Bind(R.id.tb_message)
    Toolbar tbMessage;

    private View view;
    private List<Conversation> mConversations;
    private MessageAdapter mAdapter;

    private static MessageFragment instance;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    mAdapter.notifyDataSetChanged();
                    srlMessage.setRefreshing(false);
//                    mAdapter.notifyDataSetChanged();
//                    handler.sendEmptyMessage(HandlerKey.TRUE);
                    break;

                case HandlerKey.REFRESH_FAIL:
                    srlMessage.setRefreshing(false);
                    break;
                case HandlerKey.TRUE:
                    srlMessage.setRefreshing(false);
                    break;
            }
        }
    };

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_fragment_message, null);
//        ButterKnife.bind(this, view);
//
//        initView();
//        initData();
//        instance = MessageFragment.this;
//        return view;
//    }

    protected void initView() {
        activity.setSupportActionBar(tbMessage);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tbMessage.setNavigationIcon(R.mipmap.ic_menu_white_48dp);
        setHasOptionsMenu(true);
        RefreshUtil.initRefreshView(srlMessage, this);

        initData();
        instance = MessageFragment.this;
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_message;
    }

    //初始化数据
    private void initData() {
        mConversations = JMessageClient.getConversationList();

        mAdapter = new MessageAdapter(activity,
                mConversations, R.layout.item_message);

        lvMessage.setAdapter(mAdapter);
        refreshConversationList();

        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                setClick(i);
            }
        });

        lvMessage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                //删除本地数据库聊天记录
                View dialogView = LayoutInflater.from(activity)
                        .inflate(R.layout.dialog_layout_tip,null);
                TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
                TextView tvMessage = (TextView) dialogView.findViewById(R.id.tv_message);
                tvTitle.setText("删除聊天");
                tvMessage.setText("同时会将聊天记录一起删除，确定继续吗？");

                new AlertDialog.Builder(activity)
                        .setView(dialogView)
                        .setNegativeButton("不是现在", null)
                        .setPositiveButton("删除",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //删除会话
                                        deleteConversation(i);
                                    }
                                }).show();
                return true;
            }
        });
    }

    //删除本地会话，同时会删除聊天记录
    private void deleteConversation(int position) {

        Conversation conversation = mConversations.get(position);
        Object info = conversation.getTargetInfo();

        if (info instanceof UserInfo) {
            UserInfo targetInfo = (UserInfo) info;
            JMessageClient.deleteSingleConversation(targetInfo.getUserName());
        } else if (info instanceof GroupInfo) {
            GroupInfo targetInfo = (GroupInfo) info;
            JMessageClient.deleteGroupConversation(targetInfo.getGroupID());
        }
        mConversations.remove(position);
        handler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);
    }

    //设置点击事件
    private void setClick(int position) {
        Intent intent = null;
        Conversation conversation = mConversations.get(position);
        Object info = conversation.getTargetInfo();

        switch (conversation.getType()) {
            case single:
                UserInfo userInfo = (UserInfo) info;
                intent = new Intent(activity, SingleChat.class);
                intent.putExtra("targetUsername", userInfo.getUserName());
                intent.putExtra("nickName", userInfo.getNickname());
                startActivity(intent);
                break;
            case group:
                GroupInfo groupInfo = (GroupInfo) info;
                if (!groupInfo.getGroupDescription().equals("temp")) {
                    intent = new Intent(activity, GroupChat.class);
                    intent.putExtra("groupId", groupInfo.getGroupID());
                    startActivity(intent);
                } else {
                    intent = new Intent(activity, TempGroupChat.class);
                    intent.putExtra("groupId", groupInfo.getGroupID());
                    startActivity(intent);
                }
                break;
            default:
                break;
        }
    }

    //刷新会话列表
    private void refreshConversationList() {
        List<Conversation> data = JMessageClient.getConversationList();

        mConversations.clear();
        if (data != null && data.size() > 0) {
            emptyView.setVisibility(View.GONE);
            mConversations.addAll(data);
            sortItem();
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
        handler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);
    }

    //对会话列表进行排序
    private void sortItem() {
        Comparator<Conversation> itemComparator = new Comparator<Conversation>() {
            public int compare(Conversation info1, Conversation info2){

                if (info1.getLatestMessage().getCreateTime()
                        > info2.getLatestMessage().getCreateTime()) {
                    return -1;
                } else if (info1.getLatestMessage().getCreateTime()
                        < info2.getLatestMessage().getCreateTime()) {
                    return 1;
                }
                return 0;
            }
        };
        Collections.sort(mConversations, itemComparator);
    }

    //刷新
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshConversationList();
//                    Thread.sleep(500);
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(HandlerKey.REFRESH_FAIL);
                }
            }
        }).start();
    }

    //数据刷新回调
    @Override
    public void onRefreshData() {
        refreshConversationList();
    }

    //获取本类实例
    public static MessageFragment getMessageFragment() {
        return instance;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshConversationList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            setHasOptionsMenu(true);
        } else {
            setHasOptionsMenu(false);
        }
    }
}
