package com.myschool.schoolcircle.ui.activity.mine;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myschool.schoolcircle.adapter.PhotoAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.school.dynamic.RecyclerItemClickListener;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ProgressDialogUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import me.iwf.photopicker.PhotoPicker;
import me.iwf.photopicker.PhotoPreview;

public class FeedBack extends BaseActivity {

    @Bind(R.id.tb_fankui)
    Toolbar tbFankui;
    @Bind(R.id.et_text_fankui)
    EditText etTextFankui;
    @Bind(R.id.tv_num_fankui)
    TextView tvNumFankui;
    @Bind(R.id.iv_dynamic_picture_fankui)
    ImageView ivDynamicPictureFankui;
    @Bind(R.id.recycler_view_fankui)
    RecyclerView recyclerViewFankui;
    @Bind(R.id.linearLayout)
    LinearLayout linearLayout;

    @Bind(R.id.re)
    RelativeLayout re;
    @Bind(R.id.button01)
    Button button01;


    enum RequestCode {
        ImageView(R.id.iv_dynamic_picture_fankui);
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPLOAD_SUCCESS:

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
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_feed_back);
//        ButterKnife.bind(this);
//        initView();
//        initData();
//        initListener();
//
//    }

    private void initListener() {


        etTextFankui.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                tvNumFankui.setText(charSequence.length() + "/200字");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ivDynamicPictureFankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission(RequestCode.ImageView);
            }
        });


        recyclerViewFankui.addOnItemTouchListener(new RecyclerItemClickListener(this,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        PhotoPreview.builder()
                                .setPhotos(selectedPhotos)
                                .setCurrentItem(position)
                                .start(FeedBack.this);
                    }
                }));
    }

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

    private void onClick(int viewId) {
        switch (viewId) {
            case R.id.iv_dynamic_picture_fankui:
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


    public void tiaozhuan(View v) {


        if (etTextFankui.length() < 1) {
            // mProgressDialog.show();
            showSnackBarLong(re, "需要输入内容后才能发布");

        } else {

            Intent intent = new Intent(this, Success.class);
            startActivity(intent);


        }

    }


    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    private void initData() {

        mUser = application.getUser();
        recyclerViewFankui.setLayoutManager(new StaggeredGridLayoutManager(3,
                OrientationHelper.VERTICAL));
        photoAdapter = new PhotoAdapter(this, selectedPhotos);
        recyclerViewFankui.setAdapter(photoAdapter);
    }

    protected void initView() {

        setSupportActionBar(tbFankui);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressDialog = ProgressDialogUtil.getProgressDialog(this, "提交中...");

        initData();
        initListener();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_feed_back;
    }


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
                ivDynamicPictureFankui.setVisibility(View.GONE);
            } else {
                ivDynamicPictureFankui.setVisibility(View.VISIBLE);
            }
            photoAdapter.notifyDataSetChanged();
        }
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


}

