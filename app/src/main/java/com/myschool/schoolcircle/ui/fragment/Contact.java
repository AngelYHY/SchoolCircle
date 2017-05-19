package com.myschool.schoolcircle.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.FriendsAdapter;
import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.config.Config;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.concact.group.Group;
import com.myschool.schoolcircle.ui.activity.concact.single.FriendApplyActivity;
import com.myschool.schoolcircle.ui.activity.mine.MineActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class Contact extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.lv_contact)
    ListView lvContact;
    @Bind(R.id.srl_contact)
    SwipeRefreshLayout srlContact;
    @Bind(R.id.tb_contact)
    Toolbar tbContact;

    private View view;
    private FriendsAdapter adapter;
    private List<Tb_user> friends;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HandlerKey.POST_SUCCESS:
                    String result = (String) msg.obj;
                    if (!result.equals("fail")) {
                        updateList(result);
                    }
                    break;

                case HandlerKey.POST_FAIL:

                    break;

                case HandlerKey.REFRESH_SUCCESS:
                    srlContact.setRefreshing(false);
                    break;

                case HandlerKey.REGISTER_SUCCESS:

                    break;
            }
        }
    };

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_contact, null);
//        ButterKnife.bind(this, view);
//        initData();
//        initView();
//        return view;
//    }

    private void initToolbar() {
        activity.setSupportActionBar(tbContact);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tbContact.setNavigationIcon(R.mipmap.ic_menu_white_48dp);
        setHasOptionsMenu(true);
    }

    //初始化数据
    private void initData() {
        friends = new ArrayList<>();
        doRefreshFriendList();
    }

    //初始化界面
    protected void initView() {
        initData();

        adapter = new FriendsAdapter(activity, friends, R.layout.item_contact_classmate);
        lvContact.setAdapter(adapter);
        View head = LayoutInflater.from(activity)
                .inflate(R.layout.layout_head_contact, null);

        LinearLayout llNewFriend = (LinearLayout) head.findViewById(R.id.ll_new_friend);
        LinearLayout llGroup = (LinearLayout) head.findViewById(R.id.ll_group);

        //新朋友
        llNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToActivity(activity, FriendApplyActivity.class);
            }
        });

        //群组
        llGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentToActivity(activity, Group.class);
            }
        });

        lvContact.addHeaderView(head);
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(activity, MineActivity.class);
                Tb_user friend = friends.get(i - 1);
                intent.putExtra("type", friend.getUsername());
                intent.putExtra("nickName", friend.getRealName());
                startActivity(intent);
            }
        });
        //初始化下拉刷新控件
        RefreshUtil.initRefreshView(srlContact, this);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_contact;
    }

    //向服务器发送请求刷新数据
    private void doRefreshFriendList() {
        doPost();
    }

    //post请求
    private void doPost() {
        RequestParams params = new RequestParams(Config.URL + "Refresh");
        params.addBodyParameter("type", "friend_list");
        params.addBodyParameter("username", application.getMyInfo().getUserName());
        params.setConnectTimeout(8000);

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                updateList(result);
                return false;
            }

            @Override
            public void onSuccess(String result) {
                updateList(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    //更新列表数据
    private void updateList(String result) {
        Type type = new TypeToken<ArrayList<Tb_user>>() {
        }.getType();

        Gson gson = new Gson();
        ArrayList<Tb_user> users = gson.fromJson(result, type);

        friends.clear();
        for (Tb_user user : users) {
            friends.add(user);
        }

        adapter.notifyDataSetChanged();
    }

    //刷新
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    doRefreshFriendList();
                    Thread.sleep(500);//休眠0.5秒

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = HandlerKey.REFRESH_SUCCESS;
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            setHasOptionsMenu(false);
        } else {
            initToolbar();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
