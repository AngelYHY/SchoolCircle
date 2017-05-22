package com.myschool.schoolcircle.ui.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.myschool.schoolcircle.main.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * App 介绍
 */
public class About extends AppCompatActivity {

    @Bind(R.id.tb_dynamic_detail)
    Toolbar tbDynamicDetail;
    @Bind(R.id.LOGO)
    ImageView LOGO;
    @Bind(R.id.textView)
    TextView textView;
    @Bind(R.id.RE1)
    RelativeLayout RE1;
    @Bind(R.id.textView2)
    TextView textView2;
    @Bind(R.id.RE2)
    RelativeLayout RE2;
    @Bind(R.id.textView3)
    TextView textView3;
    @Bind(R.id.img_right_02)
    ImageView imgRight02;
    @Bind(R.id.RE3)
    RelativeLayout RE3;
    @Bind(R.id.textView4)
    TextView textView4;
    @Bind(R.id.img_right_03)
    ImageView imgRight03;
    @Bind(R.id.RE4)
    RelativeLayout RE4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_about);
        ButterKnife.bind(this);

        initView();

    }




    private void initView() {
        setSupportActionBar(tbDynamicDetail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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












    @OnClick({R.id.img_right_02, R.id.img_right_03})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_right_02:
                Intent intent = new Intent(this,Production.class);
                startActivity(intent);




                break;
            case R.id.img_right_03:

                Intent intent2 = new Intent(this,FeedBack.class);
                startActivity(intent2);


                break;
        }
    }
}
