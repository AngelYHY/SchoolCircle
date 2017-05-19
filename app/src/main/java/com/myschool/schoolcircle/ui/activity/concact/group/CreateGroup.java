package com.myschool.schoolcircle.ui.activity.concact.group;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myschool.schoolcircle.adapter.CommonAdapter;
import com.myschool.schoolcircle.adapter.ViewHolder;
import com.myschool.schoolcircle.base.BaseActivity;
import com.myschool.schoolcircle.entity.Tb_group;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.mine.ImageActivity;
import com.myschool.schoolcircle.utils.HandlerKey;
import com.myschool.schoolcircle.utils.QiniuCloudUtil;
import com.myschool.schoolcircle.utils.ToastUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.callback.CreateGroupCallback;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;

public class CreateGroup extends BaseActivity {

    @Bind(R.id.tb_create_group)
    Toolbar tbCreateGroup;
    @Bind(R.id.iv_group_image)
    ImageView ivGroupImage;
    @Bind(R.id.et_group_name)
    EditText etGroupName;
    @Bind(R.id.til_group_name)
    TextInputLayout tilGroupName;
    @Bind(R.id.tv_specialty)
    TextView tvSpecialty;
    @Bind(R.id.rl_specialty)
    RelativeLayout rlSpecialty;
    @Bind(R.id.ll_group_information)
    LinearLayout llGroupInformation;
    @Bind(R.id.rl_create_group)
    RelativeLayout rlCreateGroup;

    private ProgressDialog mProgressDialog;

    private long mGroupId;
    private String mType;
    private String mImageUrl = "";
    private String mGroupName = "";
    private String mSchoolName = "";
    private String mGrade = "";
    private String mSpecialty = "";
    private String mLabel;

    private List<String> mData;
    private ItemAdapter mAdapter;
    private AlertDialog mAlertDialog;

    class ItemAdapter extends CommonAdapter<String> {

        public ItemAdapter(Context context, List<String> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, String s) {
            holder.setText(R.id.tv_text, s);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerKey.UPLOAD_SUCCESS:
                    //上传群头像成功
                    mImageUrl = (String) msg.obj;
                    //在极光服务器上创建群组
                    JCreateGroup();
                    break;
                case HandlerKey.UPLOAD_FAIL:

                    break;
                case HandlerKey.CREATE_GROUP_SUCCESS:
                    //在极光服务器上创建群组成功
                    mGroupId = (long) msg.obj;
                    //将群组信息保存到自己的服务器
                    postGroupInformation();
                    break;
                case HandlerKey.CREATE_GROUP_FAIL:
                    //在极光服务器上创建群组失败
                    showSnackBarLong(rlCreateGroup,"创建群组失败");
                    mProgressDialog.cancel();
                    break;
            }
        }
    };

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.layout_activity_create_group);
//        ButterKnife.bind(this);
//
//        initData();
//        initView();
//    }

    @Override
    public void onEvent(LoginStateChangeEvent event) {
        eventAction(event, this);
    }

    //初始化数据
    private void initData() {
        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
    }

    //初始化界面
    protected void initView() {
        initData();

        setSupportActionBar(tbCreateGroup);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("创建群组中...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        etGroupName = tilGroupName.getEditText();
        switch (mType) {
            case "personal":
                mLabel = "私人";
                break;
            case "specialty":
                mLabel = "专业";
                llGroupInformation.setVisibility(View.VISIBLE);
                mAdapter = new ItemAdapter(this, mData, R.layout.item_text);
                break;
            case "association":
                mLabel = "社团";
                mData = new ArrayList<>();
                mAdapter = new ItemAdapter(this, mData, R.layout.item_text);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_activity_create_group;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checked_tool, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_checked:
                readyCreateGroup();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //准备创建群组
    private void readyCreateGroup() {
        if (checkInformation()) {
            mProgressDialog.show();
            if (!mImageUrl.equals("")) {
                //若有图片，先将图片上传到七牛云
                QiniuCloudUtil.upLoadImage(mImageUrl,null,mHandler);
            } else {
                //若没有图片,就直接向极光服务器上创建群组
                JCreateGroup();
            }

        } else {
            showSnackBarLong(rlCreateGroup,"需要将群组信息填写完整才能创建。");
        }
    }

    //向极光服务器请求创建一个群组
    private void JCreateGroup() {
        JMessageClient.createGroup(mGroupName, "", new CreateGroupCallback() {
            @Override
            public void gotResult(int i, String s, long l) {
                if (i == 0) {
                    Message msg = Message.obtain();
                    msg.obj = l;
                    msg.what = HandlerKey.CREATE_GROUP_SUCCESS;
                    mHandler.sendMessage(msg);
                } else {
                    mHandler.sendEmptyMessage(HandlerKey.CREATE_GROUP_FAIL);
                }
            }
        });
    }

    //检查信息是否填写完整
    private boolean checkInformation() {
        mGroupName = etGroupName.getText().toString();
        switch (mType) {
            case "personal":
                if (!mGroupName.isEmpty()) {
                    return true;
                }
                return false;
            case "specialty":
                if (!mGroupName.isEmpty() && !mSpecialty.isEmpty()) {
                    return true;
                }
                return false;
            case "association":
                if (!mGroupName.isEmpty()) {
                    return true;
                }
                return false;
            default:
                break;
        }

        return false;
    }

    //构造群组信息数据
    private String makeGroupInformation() {
        Tb_user user = application.getUser();
        UserInfo userInfo = application.getMyInfo();
        Tb_group tb_group = new Tb_group();
        tb_group.setGroupId(mGroupId);
        tb_group.setCreatorUsername(user.getUsername());
        tb_group.setGroupImage(mImageUrl);
        tb_group.setGroupName(mGroupName);
        tb_group.setGroupSchoolName(userInfo.getRegion());
        tb_group.setGroupGrade(user.getStartSchoolYear());
        tb_group.setGroupSpecialty(mSpecialty);
        tb_group.setLabel(mLabel);

        Gson gson = new Gson();
        String groupInformation = gson.toJson(tb_group);
        return groupInformation;
    }

    //将创建的群组信息提交到服务器
    private void postGroupInformation() {
        RequestParams params = new RequestParams(URL + "Group");
        params.setConnectTimeout(8000);
        params.addBodyParameter("type", "createGroup");
        params.addBodyParameter("groupInformation", makeGroupInformation());

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                if (!"fail".equals(result)) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                    ToastUtil.showToast(CreateGroup.this, "群组创建成功", Toast.LENGTH_LONG);
                } else {
                    ToastUtil.showToast(CreateGroup.this, "群组创建失败", Toast.LENGTH_LONG);
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

    //点击事件
    @OnClick({R.id.iv_group_image, R.id.rl_specialty,})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_group_image:
                Intent intent = new Intent(this, ImageActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.rl_specialty:
                List<String> data2 = new ArrayList<>();
                for (int i = 0; i < 20; i++) {
                    data2.add("计算机科学与技术" + i);
                }
                showAlertDialog("专业", data2, 800, 1500);
                break;
            default:
                break;
        }
    }

    //接收上一个界面返回的信息
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<String> image = (ArrayList<String>) data
                    .getSerializableExtra("image");
            mImageUrl = image.get(0);
            setGroupImage();
        }
    }

    //设置群组头像
    private void setGroupImage() {
        ImageOptions options = new ImageOptions.Builder()
                .setCircular(true).build();
        x.image().bind(ivGroupImage, mImageUrl, options);
    }

    //显示选择器
    private void showAlertDialog(final String title,
                                 final List<String> data, int width, int height) {
        //解析对话框的布局
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_layout_text, null);
        TextView tvTitle = (TextView) dialogView
                .findViewById(R.id.tv_title);
        ListView lvItems = (ListView) dialogView
                .findViewById(R.id.lv_items);

        //设置对话框标题
        tvTitle.setText("选择" + title);

        //初始化listView数据和监听事件
        mAdapter = new ItemAdapter(this, data, R.layout.item_text);
        lvItems.setAdapter(mAdapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int i, long l) {
                String text = data.get(i);
                switch (title) {
                    case "专业":
                        tvSpecialty.setText(text);
                        mSpecialty = text;
                        break;
                    default:
                        break;
                }
                mAlertDialog.cancel();
            }
        });

        //创建及显示对话框
        mAlertDialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();
        mAlertDialog.show();
        mAlertDialog.getWindow().setLayout(width, height);
    }
}
