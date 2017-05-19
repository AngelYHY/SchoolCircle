package com.myschool.schoolcircle.ui.activity.school.dynamic;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myschool.schoolcircle.adapter.PhotoAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_dynamic;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;
import com.myschool.schoolcircle.utils.QiniuCloudUtil;
import com.myschool.schoolcircle.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class SendDynamic extends BaseActivity {

    @Bind(R.id.tb_send_dynamic)
    Toolbar tbSendDynamic;
    @Bind(R.id.et_text_content)
    EditText etTextContent;
    @Bind(R.id.iv_dynamic_picture)
    ImageView ivActivityPictureDynamic;
    @Bind(R.id.ll_dynamic)
    LinearLayout llDynamic;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.tv_num)
    TextView tvNum;

    enum RequestCode {
        ImageView(R.id.iv_dynamic_picture);
        @IdRes
        final int mViewId;

        RequestCode(@IdRes int viewId) {
            mViewId = viewId;
        }
    }

    private Tb_user mUser;
    private ProgressDialog mProgressDialog;
    private PhotoAdapter photoAdapter;
    private ArrayList<String> selectedPhotos = new ArrayList<>();
    private Tb_dynamic tbDynamic;
    private List<String> imageUrls;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPLOAD_SUCCESS:
                    String imageUrl = (String) msg.obj;
                    imageUrls.add((String) msg.obj);
                    if (imageUrls.size() == selectedPhotos.size()) {
                        //如果图片全部上传成功
                        tbDynamic.setImages(imageUrls);
                        publishDynamic(tbDynamic);
                    }
                    break;
                case HandlerKey.UPLOAD_FAIL:

                    break;
                case HandlerKey.TRUE:
                    mProgressDialog.cancel();
                    break;
                case HandlerKey.FALSE:
                    mProgressDialog.cancel();
                    break;
                default:
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_send_dynamic);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//        initListener();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    protected void initView() {
        setSupportActionBar(tbSendDynamic);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "发布中...");

        initData();
        initListener();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_send_dynamic;
    }

    private void initData() {
        mUser = application.getUser();
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(3,
                OrientationHelper.VERTICAL));
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recyclerView.setAdapter(photoAdapter);
    }

    private void initListener() {

        etTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tvNum.setText(charSequence.length()+"/200字");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivActivityPictureDynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(RequestCode.ImageView);
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        PhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .start(SendDynamic.this);
                    }
                }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If request is cancelled, the result arrays are empty.
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            // permission was granted, yay!
            onClick(RequestCode.values()[requestCode].mViewId);

        } else {
            // permission denied, boo! Disable the
            // functionality that depends on this permission.
            Toast.makeText(this,
                    "No read storage permission! Cannot perform the action.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permission) {
        switch (permission) {
            case Manifest.permission.READ_EXTERNAL_STORAGE:
            case Manifest.permission.CAMERA:
                // No need to explain to user as it is obvious
                return false;
            default:
                return true;
        }
    }

    //检查权限
    private void checkPermission(RequestCode requestCode) {
        int readStoragePermissionState = ContextCompat
                .checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int cameraPermissionState = ContextCompat
                .checkSelfPermission(this, Manifest.permission.CAMERA);

        boolean readStoragePermissionGranted =
                readStoragePermissionState != PackageManager.PERMISSION_GRANTED;
        boolean cameraPermissionGranted =
                cameraPermissionState != PackageManager.PERMISSION_GRANTED;

        if (readStoragePermissionGranted || cameraPermissionGranted) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                String[] permissions;
                if (readStoragePermissionGranted && cameraPermissionGranted) {
                    permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA};
                } else {
                    permissions = new String[]{
                            readStoragePermissionGranted ?
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                    : Manifest.permission.CAMERA
                    };
                }
                ActivityCompat.requestPermissions(this,
                        permissions,
                        requestCode.ordinal());
            }

        } else {
            // Permission granted
            onClick(requestCode.mViewId);
        }
    }

    private void onClick(int viewId) {
        switch (viewId) {
            case R.id.iv_dynamic_picture:
                //Intent intent = new Intent(MainActivity.this, PhotoPickerActivity.class);
                //PhotoPickerIntent.setPhotoCount(intent, 9);
                //PhotoPickerIntent.setColumn(intent, 4);
                //startActivityForResult(intent, REQUEST_CODE);
                PhotoPicker.builder()
                        .setPhotoCount(9)
                        .setGridColumnCount(3)
                        .start(this);
                break;
        }
    }

    //解析标题栏布局
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_publish, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //初始化标题栏item点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_activity_publish:
                //发布动态
                tbDynamic = makeTbDynamic();
                if (tbDynamic != null) {
                    mProgressDialog.show();
                    if (selectedPhotos.size() > 0) {
                        imageUrls = new ArrayList<>();
                        for (String selectedPhoto : selectedPhotos) {
                            upLoadImage(selectedPhoto);
                        }
                    } else {
                        publishDynamic(tbDynamic);
                    }
                } else {
                    showSnackBarLong(llDynamic, "需要输入内容后才能发布");
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //接收上一个界面返回的图片地址
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK &&
                (requestCode == PhotoPicker.REQUEST_CODE
                        || requestCode == PhotoPreview.REQUEST_CODE)) {
            List<String> photos = null;
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS);
            }
            selectedPhotos.clear();

            if (photos != null) {
                selectedPhotos.addAll(photos);
            }
            if (selectedPhotos.size() > 0) {
                ivActivityPictureDynamic.setVisibility(View.GONE);
            } else {
                ivActivityPictureDynamic.setVisibility(View.VISIBLE);
            }
            photoAdapter.notifyDataSetChanged();
        }
    }

    //上传图片
    private void upLoadImage(String imageUrl) {
        QiniuCloudUtil.upLoadImage(imageUrl,null,mHandler);
    }

    //构造动态表
    private Tb_dynamic makeTbDynamic() {
        String textContent = etTextContent.getText().toString();
        if (prove(textContent)) {

            Tb_dynamic tbDynamic = new Tb_dynamic();
            tbDynamic.setAvatar(mUser.getHeadView());
            tbDynamic.setUsername(mUser.getUsername());
            tbDynamic.setRealName(mUser.getRealName());
            tbDynamic.setTextContent(textContent);
            return tbDynamic;
        }
        return null;
    }

    //验证发布内容
    private boolean prove(String text) {
        if (!text.isEmpty()) {
            return true;
        }
        return false;
    }

    //发布动态
    private void publishDynamic(Tb_dynamic tb) {
        RequestParams params = new RequestParams(URL + "Dynamic");
        params.setConnectTimeout(8000);
        Gson gson = new Gson();
        String data = gson.toJson(tb);
        params.addBodyParameter("type", "publishDynamic");
        params.addBodyParameter("data", data);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if ("success".equals(result)) {
                    ToastUtil.showToast(SendDynamic.this, "发表成功", Toast.LENGTH_SHORT);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    showSnackBarLong(llDynamic, "网络貌似出了点问题，发表失败");
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
                mHandler.sendEmptyMessage(HandlerKey.TRUE);
            }
        });
    }

}
