package com.myschool.schoolcircle.ui.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.FriendsAdapter;
import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.NetUtil;
import com.myschool.schoolcircle.utils.RefreshUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class ClassmateFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener{

    private ListView lvContactClassmate;
    private SwipeRefreshLayout srlClassmate;

    private View view;
    private FriendsAdapter adapter;
    private ArrayList<Tb_user> friends;

    private Gson gson;

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
                    srlClassmate.setRefreshing(false);
//                    srlClassmate.setEnabled(false);
                    break;

                case HandlerKey.REGISTER_SUCCESS:

                    break;
            }
        }
    };

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_fragment_classmate, null);
//        initData();
//        initView();
//        return view;
//    }

    //初始化界面
    protected void initView() {
        initData();

        lvContactClassmate = (ListView) view.findViewById(R.id.lv_contact_classmate);
        srlClassmate = (SwipeRefreshLayout) view.findViewById(R.id.srl_classmate);

        adapter = new FriendsAdapter(activity, friends, R.layout.item_contact_classmate);
        lvContactClassmate.setAdapter(adapter);
        lvContactClassmate.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                ToastUtil.showToast(activity, friends.get(i).getPhone() + "", Toast.LENGTH_SHORT);
//                Intent intent = new Intent(activity,ChatActivity.class);
//                intent.putExtra("friendName",friends.get(i).getUsername());
//                startActivity(intent);
            }
        });
        //初始化下拉刷新控件
        RefreshUtil.initRefreshView(srlClassmate,this);
        //初始化监听事件
        initListener();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_classmate;
    }

    //初始化数据
    private void initData() {
        gson = new GsonBuilder().create();
        friends = new ArrayList<>();
        doRefresh();
    }

    private void initListener() {
        //下拉刷新监听事件
//        srlClassmate.setOnRefreshListener(this);
    }

    //向服务器发送请求刷新数据
    private void doRefresh() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "friend_list");
        map.put("username", application.getUser().getUsername());

        NetUtil.getDataByPost("Refresh", map, handler);
    }

    //更新列表数据
    private void updateList(String result) {
        Type type = new TypeToken<ArrayList<Tb_user>>() {
        }.getType();

        ArrayList<Tb_user> beens = gson.fromJson(result, type);

        friends.clear();
        for (Tb_user been : beens) {
            friends.add(been);
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

                    doRefresh();
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
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("classmate","销毁");
    }
}
