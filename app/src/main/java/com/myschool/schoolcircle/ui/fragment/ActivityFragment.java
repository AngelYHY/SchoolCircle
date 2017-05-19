package com.myschool.schoolcircle.ui.fragment;

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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.ActivityRecyclerAdapter;
import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.config.Config;
import com.myschool.schoolcircle.entity.Tb_activity;
import com.myschool.schoolcircle.main.MainActivity;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.school.activitys.ActivityDetailActivity;
import com.myschool.schoolcircle.ui.activity.school.activitys.MyPublishActivityDetail;
import com.myschool.schoolcircle.ui.activity.school.activitys.PublishActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.HidingScrollListener;
import com.myschool.schoolcircle.utils.RefreshUtil;
import com.myschool.schoolcircle.utils.SnackbarUtil;
import com.myschool.schoolcircle.utils.ToastUtil;
import com.myschool.schoolcircle.utils.ViewVisibleManager;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class ActivityFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener{

    @Bind(R.id.rv_activity)
    RecyclerView rvActivity;
    @Bind(R.id.tb_activity)
    Toolbar tbActivity;
    @Bind(R.id.fab_create_activity)
    FloatingActionButton fabCreateActivity;
    @Bind(R.id.srl_activity)
    SwipeRefreshLayout srlActivity;
    @Bind(R.id.cl_activity)
    CoordinatorLayout clActivity;
    @Bind(R.id.pv_activity)
    PhotoView pvActivity;

    private View view;

    private List<Tb_activity> activities;
    private ActivityRecyclerAdapter adapter;
    private boolean isLoading = false;
    private int start = 0;

    private static final String GET_ALL_ACTIVITY = "getAllActivity";
    private static final String GET_ALL_ACTIVITY_IN_MY_SCHOOL = "getAllActivityInMySchool";
    private static final String GET_MY_JOIN_ACTIVITY = "getMyJoinActivity";
    private static final String GET_MY_PUBLISH_ACTIVITY = "getMyPublishActivity";
    private String type = GET_ALL_ACTIVITY;//默认
    private ImageOptions options;

    private boolean toolbarVisible = true;
    private boolean bottomNavigationVisible = true;
    private boolean fabButtonVisible = true;

    //摇一摇刷新
    private SensorManager sensorManager;
    private Sensor sensor;
    private Vibrator vibrator;
    private static final int UPTATE_INTERVAL_TIME = 50;
    private static final int SPEED_SHRESHOLD = 50;//这个值调节灵敏度
    private long lastUpdateTime;
    private float lastX;
    private float lastY;
    private float lastZ;
    private boolean isShake = false;
    private int shackSwitch;//摇一摇开关
    private boolean onShack = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.REFRESH_SUCCESS:
                    if (isShake) {
                        isShake = false;
                    }
                    srlActivity.setRefreshing(false);
                    break;

                case HandlerKey.REFRESH_FAIL:
                    if (isShake) {
                        isShake = false;
                    }
                    srlActivity.setRefreshing(false);
                    break;
            }
        }
    };

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater,
//                             @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_fragment_activity, null);
//        ButterKnife.bind(this, view);
//
//        init();
//
//        //获取系统传感器服务
//        sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
//        //获取系统震动
//        vibrator = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);
//
//        initView();
//
//        return view;
//    }

    private void init() {
        options = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER_INSIDE)
                .build();
        shackSwitch = application.getSettingConfig().getShack();
    }

    @Override
    public void onStart() {
        super.onStart();
        onShack = true;
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
                if (onShack && shackSwitch != -1
                        && !srlActivity.isRefreshing() && !isShake && !isLoading
                        && application.getSettingConfig().getShack() != -1) {
                    isShake = true;
                    vibrator.vibrate(300);
                    rvActivity.smoothScrollToPosition(0);
                    doRefresh();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    protected void initView() {
        init();

        //获取系统传感器服务
        sensorManager = (SensorManager) activity.getSystemService(activity.SENSOR_SERVICE);
        //获取系统震动
        vibrator = (Vibrator) activity.getSystemService(activity.VIBRATOR_SERVICE);

        //初始化下拉刷新控件
        RefreshUtil.initRefreshView(srlActivity, this);
        initRecyclerView();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_activity;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_activity_tool,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_all:
                type = GET_ALL_ACTIVITY;
                getAllActivityByType();
                break;

            case R.id.menu_activity_my_school:
                type = GET_ALL_ACTIVITY_IN_MY_SCHOOL;
                getAllActivityByType();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //初始化列表
    private void initRecyclerView() {
        rvActivity.setLayoutManager(new LinearLayoutManager(activity));
        activities = new ArrayList<>();

        adapter = new ActivityRecyclerAdapter(activity, activities);

        //活动item的点击事件
        adapter.setOnItemClickListener(new ActivityRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (view.getId()) {
                    case R.id.cv_activity:
                        intent2Detail(activities.get(position));
                        break;
                    case R.id.iv_activity:
                        String image = activities.get(position).getPicture();
                        pvActivity.setVisibility(View.VISIBLE);
                        x.image().bind(pvActivity, image, options);
                        break;
                    default:
                        break;
                }
            }
        });

        pvActivity.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                pvActivity.setVisibility(View.GONE);
            }
        });

        rvActivity.setAdapter(adapter);

        //上滑下滑列表隐藏显示
        rvActivity.setOnScrollListener(new HidingScrollListener() {
            @Override
            public void onHide() {
                hideView();
            }

            @Override
            public void onShow() {
                showView();
            }
        });

        final LinearLayoutManager manager = (LinearLayoutManager) rvActivity.getLayoutManager();
        rvActivity.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 50) {
                    int lastItemIndex = manager.findLastVisibleItemPosition();
                    //滑动到最后一个并且状态不是加载中,执行加载更多，isLoading默认值false
                    if (lastItemIndex >= activities.size() - 1 && !isLoading) {
                        loadMore();
                    }
                }
            }
        });

        doRefresh();
    }

    //显示
    private void showView() {
        toolbarVisible = true;
        bottomNavigationVisible = true;
        fabButtonVisible = true;
        ViewVisibleManager.showViews(tbActivity, fabCreateActivity);
        MainActivity.layoutRadio.animate().translationY(0).
                setInterpolator(new DecelerateInterpolator(2));
    }

    //隐藏
    private void hideView() {
        toolbarVisible = false;
        bottomNavigationVisible = false;
        fabButtonVisible = false;
        ViewVisibleManager.hideViews(tbActivity, fabCreateActivity);
        MainActivity.layoutRadio.animate().translationY(
                MainActivity.layoutRadio.getHeight()).
                setInterpolator(new AccelerateInterpolator(2));
    }

    //加载更多
    public void loadMore() {
        activities.add(null);//显示加载中的布局,对应MyAdapter的getItemViewType()方法
        adapter.notifyItemInserted(activities.size() - 1);
        isLoading = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    postAllActivityData(activities.size(), type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //向服务器请求获取所有活动信息
    private void postAllActivityData(int start, String type) {
        RequestParams params = new RequestParams(Config.URL + "Activity");
        params.setConnectTimeout(8000);
        params.addBodyParameter("start", start + "");
        String schoolName = application.getMyInfo().getRegion();

        switch (type) {
            case GET_ALL_ACTIVITY:
                //全部活动
                params.addBodyParameter("type", type);
                break;

            case GET_ALL_ACTIVITY_IN_MY_SCHOOL:
                //只看本校
                if (!schoolName.isEmpty()) {
                    params.addBodyParameter("type", type);
                    params.addBodyParameter("school", schoolName);
                } else {
                    srlActivity.setRefreshing(false);
                    this.type = GET_ALL_ACTIVITY;
                    ToastUtil.showToast(activity,"你还没有设置学校信息", Toast.LENGTH_LONG);
                    return;
                }
                break;
            case GET_MY_JOIN_ACTIVITY:
            case GET_MY_PUBLISH_ACTIVITY:
                //我的参与，我的发布
                params.addBodyParameter("type", type);
                params.addBodyParameter("username", application.getMyInfo().getUserName());
                break;
            default:
                break;
        }

        x.http().post(params, new Callback.CacheCallback<String>() {
            @Override
            public boolean onCache(String result) {
                return false;
            }

            @Override
            public void onSuccess(String result) {
                loadData(result);
                if ("nothing".equals(result)) {
                    if (!toolbarVisible && !bottomNavigationVisible
                            && !fabButtonVisible) {
                        showView();
                    }
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

            }
        });
    }

    //加载数据
    private void loadData(String result) {
        if (!"nothing".equals(result)) {
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Tb_activity>>() {
            }.getType();
            List<Tb_activity> data = gson.fromJson(result, type);
            if (isLoading) {
                //如果是上拉加载
                activities.remove(activities.size() - 1);
                adapter.notifyItemRemoved(activities.size());
                activities.addAll(data);
                isLoading = false;
            } else {
                activities.clear();
                activities.addAll(data);
            }
//            start = activities.size();
            adapter.notifyDataSetChanged();

        } else {
            if (isLoading) {
                //如果是上拉加载
                activities.remove(activities.size() - 1);
                adapter.notifyItemRemoved(activities.size());
                isLoading = false;
            } else {
                activities.clear();
                adapter.notifyDataSetChanged();
            }
            SnackbarUtil.showSnackBarLong(clActivity,"暂无更多活动");
        }
        mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
    }

    //按照用户选择类型获取评论
    private void getAllActivityByType() {
        srlActivity.setRefreshing(true);
        start = 0;
        postAllActivityData(start, type);
        switch (type) {
            case GET_ALL_ACTIVITY:
                activity.getSupportActionBar().setTitle("活动");
                break;
            case GET_ALL_ACTIVITY_IN_MY_SCHOOL:
                activity.getSupportActionBar().setTitle("本校活动");
                break;
            case GET_MY_JOIN_ACTIVITY:
                activity.getSupportActionBar().setTitle("我的参与");
                break;
            case GET_MY_PUBLISH_ACTIVITY:
                activity.getSupportActionBar().setTitle("我的发布");
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.fab_create_activity)
    public void onClick() {
        Intent intent = new Intent(activity, PublishActivity.class);
        startActivityForResult(intent, 1);
    }

    //根据不同情况跳转到不同的详情界面
    public void intent2Detail(Tb_activity activity) {
        if (type.equals(GET_MY_PUBLISH_ACTIVITY)) {
            Intent intent = new Intent(this.activity, MyPublishActivityDetail.class);
            intent.putExtra("activityData", activity);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(this.activity, ActivityDetailActivity.class);
            intent.putExtra("activityData", activity);
            startActivity(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == activity.RESULT_OK) {
            doRefresh();
        }
    }

    //下拉刷新事件
    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    doRefresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mHandler.sendEmptyMessageDelayed(HandlerKey.REFRESH_SUCCESS, 600);
            }
        }).start();
    }

    //刷新数据
    private void doRefresh() {
        start = 0;
        if (!srlActivity.isRefreshing()) {
            srlActivity.setRefreshing(true);
        }
        postAllActivityData(start, type);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            onShack = false;
            setHasOptionsMenu(false);
        } else {
            onShack = true;
            activity.setSupportActionBar(tbActivity);
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            tbActivity.setNavigationIcon(R.mipmap.ic_menu_white_48dp);
            setHasOptionsMenu(true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onShack = false;
    }
}
