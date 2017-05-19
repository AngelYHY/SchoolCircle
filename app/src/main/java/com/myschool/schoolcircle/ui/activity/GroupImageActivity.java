package com.myschool.schoolcircle.ui.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.myschool.schoolcircle.Beans.FolderBean;
import com.myschool.schoolcircle.adapter.GroupImageAdapter;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.config.Config;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.ToastUtil;
import com.myschool.schoolcircle.widget.ListImageDirPopupWindow;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;

public class GroupImageActivity extends BaseActivity {

    @Bind(R.id.gv_image_main)
    GridView gvImageMain;
    @Bind(R.id.tv_image_count)
    TextView tvImageCount;
    @Bind(R.id.rl_bottom)
    RelativeLayout rlBottom;
    @Bind(R.id.tv_folder_name)
    TextView tvFolderName;
    @Bind(R.id.tb_image)
    Toolbar tbImage;

    private ProgressDialog mDialog;
    private ListImageDirPopupWindow mImageDirPopupWindow;

    private List<FolderBean> mFolders = new ArrayList<>();
    private List<String> mImages;
    private GroupImageAdapter mAdapter;
    private File mCurrentDir;
    private int mMaxCount;
    private static final int DATA_LOADED = 0x110;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DATA_LOADED:
                    mDialog.dismiss();
                    data2View();
                    break;

                case HandlerKey.UPLOAD_SUCCESS:
                    //图片上传成功,headView存放的是图片的网络地址
                    mDialog.cancel();
                    String image = (String) msg.obj;
                    Intent intent = new Intent();
                    intent.putExtra("image",Config.QINIU_URL+image);
                    setResult(RESULT_OK,intent);
                    finish();
                    break;

                case HandlerKey.UPLOAD_FAIL:
                    //图片上传失败
                    mDialog.cancel();
                    break;
            }
        }
    };

    private void data2View() {
        if (mCurrentDir == null) {
            Toast.makeText(this, "未扫描到任何图片", Toast.LENGTH_SHORT).show();
            return;
        }

        mImages = Arrays.asList(mCurrentDir.list());
        mAdapter = new GroupImageAdapter(this, mImages, mCurrentDir.getAbsolutePath());
        gvImageMain.setAdapter(mAdapter);

        tvFolderName.setText(mCurrentDir.getName());
        tvImageCount.setText(mMaxCount + "");
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_group_image);
//        ButterKnife.bind(this);
//
//        initView();
//        initData();
//        initEven();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event,this);
    }

    protected void initView() {
        setSupportActionBar(tbImage);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDialog = new ProgressDialog(this);

        mDialog.setCanceledOnTouchOutside(false);

        initPopupWindow();

        initData();
        initEven();
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_group_image;
    }

    //初始化PopupWindow
    private void initPopupWindow() {
        mImageDirPopupWindow = new ListImageDirPopupWindow(this, mFolders);
        mImageDirPopupWindow.setAnimationStyle(R.style.dir_popup_window_anim);
        mImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                changeLight(1.0f);
            }
        });

        mImageDirPopupWindow.setOnDirSelectedListener(new ListImageDirPopupWindow
                .OnDirSelectedListener() {
            @Override
            public void onSelected(FolderBean folderBean) {
                mCurrentDir = new File(folderBean.getFolderDir());
                mImages = Arrays.asList(mCurrentDir.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File file, String fileName) {
                        if (fileName.endsWith(".jpg")
                                || fileName.endsWith(".jpeg")
                                || fileName.endsWith(".png")) {

                            return true;
                        }
                        return false;
                    }
                }));

                //选择新的文件夹后更新数据
                mAdapter = new GroupImageAdapter(GroupImageActivity.this,
                        mImages, mCurrentDir.getAbsolutePath());
                gvImageMain.setAdapter(mAdapter);

                tvFolderName.setText(folderBean.getFolderName());
                tvImageCount.setText(folderBean.getCount() + "");
                mImageDirPopupWindow.dismiss();
            }
        });
    }

    //PopupWindow出现时区域亮度变化
    private void changeLight(float num) {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = num;
        getWindow().setAttributes(params);
    }

    /**
     * 利用ContentProvider扫描手机中的图片
     */
    private void initData() {
        //判断存储卡是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "当前存储卡不可用", Toast.LENGTH_LONG).show();
            return;
        }
        mDialog.setMessage("加载图片资源中...");
        mDialog.show();

        //开启子线程加载图片资源
        new Thread() {
            @Override
            public void run() {
                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver cr = GroupImageActivity.this.getContentResolver();

                Cursor cursor = cr.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "= ? or " +
                                MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                Set<String> mDirPaths = new HashSet<String>();
                //遍历获取每一张图片的路径
                while (cursor.moveToNext()) {
                    String path = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    FolderBean folderBean = null;

                    if (mDirPaths.contains(dirPath)) {
                        continue;
                    } else {
                        mDirPaths.add(dirPath);
                        //构造图片所在的文件夹
                        folderBean = new FolderBean();
                        folderBean.setFolderDir(dirPath);
                        folderBean.setFirstImagePath(path);
                    }

                    if (parentFile.list() == null) {
                        continue;
                    }

                    int imageCount = parentFile.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File file, String fileName) {
                            if (fileName.endsWith(".jpg")
                                    || fileName.endsWith(".jpeg")
                                    || fileName.endsWith(".png")) {

                                return true;
                            }
                            return false;
                        }
                    }).length;

                    folderBean.setCount(imageCount);
                    //将构造好的文件夹放入文件夹的集合中，用于popupWindow文件夹的显示
                    mFolders.add(folderBean);

                    //底部显示的文件夹的名称，图片数量
                    if (imageCount > mMaxCount) {
                        mMaxCount = imageCount;
                        mCurrentDir = parentFile;
                    }
                }
                cursor.close();
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(DATA_LOADED);
            }
        }.start();
    }

    //初始化监听事件
    private void initEven() {
        rlBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageDirPopupWindow.showAsDropDown(rlBottom, 0, 0);
                changeLight(0.3f);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_mine_image,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_edit_mine_image_checked:
                if (mAdapter.mSelected.size() > 0) {
                    ArrayList<String> images = new ArrayList<>();
                    for (String s : mAdapter.mSelected) {
                        images.add(s);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("images",images);
                    setResult(RESULT_OK,intent);
                    mAdapter.mSelected.clear();
                    finish();
                } else {
                    ToastUtil.showToast(this,"你没有选择任何图片",Toast.LENGTH_SHORT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
