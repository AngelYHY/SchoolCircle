package com.myschool.schoolcircle.ui.activity.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.adapter.CommonAdapter;
import com.myschool.schoolcircle.adapter.ViewHolder;
import com.myschool.schoolcircle.config.Config;
import com.myschool.schoolcircle.entity.Tb_school;
import com.myschool.schoolcircle.main.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchSchool extends AppCompatActivity
        implements SearchView.OnQueryTextListener {

    @Bind(R.id.sv_school)
    SearchView svSchool;
    @Bind(R.id.lv_school)
    ListView lvSchool;

    private String phone;
    private List<Tb_school> schools;
    private ItemAdapter mAdapter;
    private Callback.Cancelable post;

    class ItemAdapter extends CommonAdapter<Tb_school> {

        public ItemAdapter(Context context, List<Tb_school> data, int layoutId) {
            super(context, data, layoutId);
        }

        @Override
        public void convert(ViewHolder holder, Tb_school s) {
            holder.setText(R.id.tv_text, s.getSchoolName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_search_school);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        phone = intent.getStringExtra("phone");

        svSchool.setOnQueryTextListener(this);
        schools = new ArrayList<>();
        mAdapter = new ItemAdapter(this,schools,R.layout.item_text);
        lvSchool.setAdapter(mAdapter);

        lvSchool.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(SearchSchool.this,RegisterActivity.class);
                intent1.putExtra("school",schools.get(i));
                intent1.putExtra("phone",phone);
                startActivity(intent1);
            }
        });
    }

    //提交搜索
    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!query.isEmpty()) {
            if (post != null && !post.isCancelled()) {
                post.cancel();
            }
            search(query);
        }
        return true;
    }

    //搜索栏文字改变的触发的事件
    @Override
    public boolean onQueryTextChange(String newText) {
        if (!newText.isEmpty()) {
            if (post != null && !post.isCancelled()) {
                post.cancel();
            }
            search(newText);
        } else {
            schools.clear();
            mAdapter.notifyDataSetChanged();
        }
        return true;
    }

    //搜索
    private void search(String strSearch) {
        RequestParams params = new RequestParams(Config.URL + "Register");
        params.addBodyParameter("type", "searchSchool");
        params.addBodyParameter("search", strSearch);
        params.setConnectTimeout(8000);

        post = x.http().post(params, new Callback.CommonCallback<String>() {
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

    //更新列表
    private void updateList(String result) {
        if (!"nothing".equals(result)) {

            Gson gson = new Gson();
            Type type = new TypeToken<List<Tb_school>>() {
            }.getType();

            List<Tb_school> data = gson.fromJson(result, type);
            schools.clear();
            schools.addAll(data);

        } else {
//            mHandler.sendEmptyMessage(HandlerKey.REFRESH_FAIL);
        }
    }
}
