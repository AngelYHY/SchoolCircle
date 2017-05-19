package com.myschool.schoolcircle.ui.activity.school.dynamic;

import android.app.ProgressDialog;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.DynamicRecyclerAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.school.activitys.DetailsItemActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.HidingScrollListener;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.ViewVisibleManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import uk.co.senab.photoview.PhotoView;

public class DynamicActivity extends BaseActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    @Bind(R.id.tb_dynamic)
    Toolbar tbDynamic;
    @Bind(R.id.rv_dynamic)
    RecyclerView rvDynamic;
    @Bind(R.id.fab_create_dynamic)
    FloatingActionButton fabCreateDynamic;
    @Bind(R.id.srl_dynamic)
    SwipeRefreshLayout srlDynamic;
    @Bind(R.id.coor_dynamic)
    CoordinatorLayout coorDynamic;
    @Bind(R.id.et_input)
    EditText etInput;
    @Bind(R.id.rl_input)
    RelativeLayout rlInput;
    @Bind(R.id.photo_view)
    PhotoView photoView;
    @Bind(R.id.v_empty)
    View vEmpty;

    private static final String GET_ALL_DYNAMIC = "getAllDynamic";
    private static final String GET_ALL_DYNAMIC_IN_MY_SCHOOL = "getAllDynamicInMySchool";

    private ProgressDialog mProgressDialog;
    private List<Tb_dynamic> mDynamics;
    private DynamicRecyclerAdapter adapter;
    private int start = 0;
    private boolean isLoading;
    private String type = GET_ALL_DYNAMIC;
    private String fabType = "createDynamic";
    private int retransmissionDynamicId = -1;

    //摇一摇刷新
    private int shackSwitch;
    private SensorManager sensorManager;
    private Sensor sensor;
    private Vibrator vibrator;
    private static final int UPTATE_INTERVAL_TIME = 50;
    private static final int SPEED_SHRESHOLD = 60;//这个值调节灵敏度
    private long lastUpdateTime;
    private float lastX;
    private float lastY;
    private float lastZ;
    private boolean isShack = false;
    private boolean onShake = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    if (isShack) {
                        isShack = false;
                    }
                    srlDynamic.setRefreshing(false);
                    break;

                case HandlerKey.REFRESH_FAIL:
                    if (!isShack) {
                        isShack = false;
                    }
                    srlDynamic.setRefreshing(false);
                    break;
                case HandlerKey.TRUE:
                    fabCreateDynamic.setImageResource(R.mipmap.ic_send_white_48dp);
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_dynamic);
//        ButterKnife.bind(this);
//
//        init();
//
//        //获取系统传感器服务
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        //获取系统震动
//        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//
//        //事件接收类的注册
//        JMessageClient.registerEventReceiver(this);
//        initView();
//    }

    private void init() {
        //摇一摇开关
        shackSwitch = application.getSettingConfig().getShack();

        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    fabCreateDynamic.show();
                } else {
                    fabCreateDynamic.hide();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        onShake = true;
        if (sensorManager != null) {
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }
        if (sensor != null) {
            sensorManager.registerListener(sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_GAME);//这里选择感应频率
        }
    }

    /**
     * 重力感应监听
     */
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            if (timeInterval < UPTATE_INTERVAL_TIME) {
                return;
            }
            lastUpdateTime = currentUpdateTime;
            // 传感器信息改变时执行该方法
            float[] values = event.values;
            float x = values[0]; // x轴方向的重力加速度，向右为正
            float y = values[1]; // y轴方向的重力加速度，向前为正
            float z = values[2]; // z轴方向的重力加速度，向上为正
            float deltaX = x - lastX;
            float deltaY = y - lastY;
            float deltaZ = z - lastZ;

            lastX = x;
            lastY = y;
            lastZ = z;
            double speed = (Math.sqrt(deltaX * deltaX + deltaY * deltaY
                    + deltaZ * deltaZ) / timeInterval) * 100;
            if (speed >= SPEED_SHRESHOLD) {
//                if (onShake && shackSwitch != -1 && !isShack
//                        && !srlDynamic.isRefreshing() && !isLoading) {
//                    isShack = true;
//                    //震动300毫秒
//                    vibrator.vibrate(300);
//                    rvDynamic.smoothScrollToPosition(0);
//                    doRefresh();
//                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        init();

        //获取系统传感器服务
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        //获取系统震动
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //事件接收类的注册
        JMessageClient.registerEventReceiver(this);

        setSupportActionBar(tbDynamic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "正在转发...");
        RefreshUtil.initRefreshView(srlDynamic, this);
        initRecyclerView();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_dynamic;
    }

    private void initRecyclerView() {
        mDynamics = new ArrayList<>();
        rvDynamic.setLayoutManager(new LinearLayoutManager(this));

        adapter = new DynamicRecyclerAdapter(this, mDynamics);
        adapter.setOnItemClickListener(new DynamicRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.cv_dynamic:
                        Intent intent = new Intent(DynamicActivity.this,
                                DetailsItemActivity.class);
                        intent.putExtra("dynamicData",mDynamics.get(position));
                        startActivity(intent);
                        break;
                    case R.id.cb_retransmission:
                        showInput(position);
                        break;
                    default:
                        break;
                }
            }
        });

        rvDynamic.setAdapter(adapter);
        rvDynamic.setItemAnimator(new DefaultItemAnimator());
        doRefresh();

        rvDynamic.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                ViewVisibleManager.hideViews(tbDynamic, fabCreateDynamic);
            }

            @Override
            public void onShow() {
                ViewVisibleManager.showViews(tbDynamic, fabCreateDynamic);
            }
        });

        final LinearLayoutManager manager =
                (LinearLayoutManager) rvDynamic.getLayoutManager();

        rvDynamic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 50) {
                    int lastItemIndex = manager.findLastVisibleItemPosition();
                    //滑动到最后一个并且状态不是加载中,执行加载更多，isLoading默认值false
                    if (lastItemIndex >= mDynamics.size() - 1 && !isLoading) {
                        loadMore();
                    }
                }
            }
        });
    }

    //加载更多
    private void loadMore() {
        mDynamics.add(null);
        adapter.notifyItemInserted(mDynamics.size() - 1);
        isLoading = true;
        new Thread() {
            @Override
            public void run() {
                try {

//                    start += 5;
                    Thread.sleep(600);
                    getAllDynamic();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dynamic_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
//            case R.id.menu_dynamic_about_me:
//                //与我相关
//                Intent intent = new Intent(this, AboutMeActivity.class);
//                startActivityForResult(intent, 1);
//                break;
            case R.id.menu_dynamic_all:
                type = GET_ALL_DYNAMIC;
                doRefresh();
                break;
            case R.id.menu_dynamic_my_school:
                type = GET_ALL_DYNAMIC_IN_MY_SCHOOL;
                doRefresh();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //显示输入框
    public void showInput(int position) {
        retransmissionDynamicId = mDynamics.get(position).get_id();
        rvDynamic.smoothScrollToPosition(position);
        vEmpty.setVisibility(View.VISIBLE);
        rlInput.setVisibility(View.VISIBLE);
        etInput.setFocusable(true);
        etInput.setFocusableInTouchMode(true);
        etInput.requestFocus();
        etInput.requestFocusFromTouch();
        showSoftInput(etInput);
        fabCreateDynamic.hide();
        fabType = "retransmission";
        mHandler.sendEmptyMessageDelayed(HandlerKey.TRUE, 200);
    }

    //转发动态
    private void retransmissionDynamic(String text) {
        mProgressDialog.show();
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "retransmissionDynamic");
        params.addBodyParameter("username", application.getMyInfo().getUserName());
        params.addBodyParameter("retransmission", text);
        if (retransmissionDynamicId != -1) {
            params.addBodyParameter("dynamicId", retransmissionDynamicId + "");
        }

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    showSnackBarLong(coorDynamic, "转发成功");
                } else {
                    showSnackBarLong(coorDynamic, "转发失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mProgressDialog.cancel();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        onShake = false;
    }

    @Override
    protected void onDestroy() {
        //取消事件接收类的注册
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }

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
                mHandler.sendEmptyMessage(HandlerKey.REFRESH_SUCCESS);
            }
        }).start();
    }

    //刷新
    private void doRefresh() {
        start = 0;
        if (!srlDynamic.isRefreshing()) {
            srlDynamic.setRefreshing(true);
        }
        getAllDynamic();
    }

    //请求获得所有数据
    private void getAllDynamic() {
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.addBodyParameter("type", type);
        params.addBodyParameter("start", start + "");
        if (type.equals(GET_ALL_DYNAMIC_IN_MY_SCHOOL)) {
            String schoolName = application.getMyInfo().getRegion();
            if (!schoolName.isEmpty()) {
                params.addBodyParameter("schoolName", schoolName);
            } else {
                showSnackBarLong(coorDynamic, "你还没有设置学校信息");
            }
        }

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                loadData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showSnackBarLong(coorDynamic,"网络貌似出了点问题~");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
            }
        });
    }

    //加载数据
    private void loadData(String result) {
        if (!"nothing".equals(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Tb_dynamic>>() {
            }.getType();
            List<Tb_dynamic> data = gson.fromJson(result, type);
            if (isLoading) {
                //如果是上拉加载
                mDynamics.remove(mDynamics.size() - 1);
                adapter.notifyItemRemoved(mDynamics.size());
                mDynamics.addAll(data);
                isLoading = false;
            } else {
                mDynamics.clear();
                mDynamics.addAll(data);
            }
            start = mDynamics.size();
            adapter.notifyDataSetChanged();

        } else {
            if (isLoading) {
                //如果是上拉加载
                mDynamics.remove(mDynamics.size() - 1);
                adapter.notifyItemRemoved(mDynamics.size());
                isLoading = false;
            } else {
                mDynamics.clear();
                adapter.notifyDataSetChanged();
            }
            showSnackBarLong(coorDynamic, "暂无更多动态");
        }
        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 825 && resultCode == RESULT_OK) {
            doRefresh();
        }
    }

    @OnClick({R.id.fab_create_dynamic, R.id.v_empty})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_create_dynamic:
                switch (fabType) {
                    case "createDynamic":
                        Intent intent = new Intent(this, SendDynamic.class);
                        startActivityForResult(intent, 825);
                        break;
                    case "retransmission":
                        String text = etInput.getText().toString().trim();
                        etInput.setText("");
                        fabCreateDynamic.setImageResource(R.mipmap.ic_add_white_48dp);
                        fabType = "createDynamic";
                        retransmissionDynamic(text);
                        vEmpty.setVisibility(View.GONE);
                        rlInput.setVisibility(View.INVISIBLE);
                        hideSoftInput(etInput);
                        break;
                    default:
                        break;
                }
                break;
            case R.id.v_empty:
                vEmpty.setVisibility(View.GONE);
                rlInput.setVisibility(View.INVISIBLE);
                fabCreateDynamic.setImageResource(R.mipmap.ic_add_white_48dp);
                fabType = "createDynamic";
                etInput.setText("");
                hideSoftInput(etInput);
                break;
            default:
                break;
        }
    }
}