package com.myschool.schoolcircle.ui.activity.mine;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.myschool.schoolcircle.adapter.CommonAdapter;
import com.myschool.schoolcircle.adapter.ViewHolder;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.dao.NotificationDao;
import com.myschool.schoolcircle.entity.Tb_notification;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.DateUtil;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.RefreshUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class Notification extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.tb_notification)
    Toolbar tbNotification;
    @Bind(R.id.lv_notification)
    ListView lvNotification;
    @Bind(R.id.srl_notification)
    SwipeRefreshLayout srlNotification;
    @Bind(R.id.rl_notification)
    RelativeLayout rlNotification;

    private List<Tb_notification> mNotifications;
    private NotificationAdapter mAdapter;
    private NotificationDao dao;

    private class NotificationAdapter extends CommonAdapter<Tb_notification> {

        public NotificationAdapter(Context context, List<Tb_notification> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, Tb_notification tb_notification) {
            holder.setText(R.id.tv_name,tb_notification.getTitle())
                    .setText(R.id.tv_content,tb_notification.getMessage())
                    .setText(R.id.tv_time,
                            DateUtil.formatDateTime(new Date(tb_notification.getTime())));
            switch (tb_notification.getTitle()) {
                case "活动":
                    holder.setImageView(R.id.civ_avatar,R.mipmap.ic_activity_notify_48dp);
                    break;
                case "动态":
                    holder.setImageView(R.id.civ_avatar,R.mipmap.ic_dynamic_notify_48dp);
                    break;
                default:
                    break;
            }
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    srlNotification.setRefreshing(false);
                    break;
                case HandlerKey.REFRESH_FAIL:
                    srlNotification.setRefreshing(false);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_notification);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//        initEvent();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event,this);
    }

    protected void initView() {
        setSupportActionBar(tbNotification);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RefreshUtil.initRefreshView(srlNotification,this);

        initData();
        initEvent();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_notification;
    }

    //初始化监听事件
    private void initData() {
        dao = new NotificationDao(this);
        mNotifications = new ArrayList<>();
        mAdapter = new NotificationAdapter(this,mNotifications,R.layout.item_message);
        lvNotification.setAdapter(mAdapter);
        reFreshData();
    }

    private void initEvent() {
        lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        lvNotification.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(Notification.this)
                        .setTitle("提醒")
                        .setMessage("确定删除这条通知吗？")
                        .setNegativeButton("不是现在",null)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        //删除，更新数据
                        dao.delete(mNotifications.get(i));
                        reFreshData();
                    }
                }).show();
                return true;
            }
        });
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
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //刷新数据
    private void reFreshData() {
        List<Tb_notification> data = dao.queryAll();
        mNotifications.clear();
        mNotifications.addAll(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRefresh() {
        new Thread() {
            @Override
            public void run() {
                try {
                    reFreshData();
                    sleep(1000);
                    mHandler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
