package com.myschool.schoolcircle.ui.activity.mine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.myschool.schoolcircle.main.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Production extends AppCompatActivity {

    @Bind(R.id.tb_jieshao)
    Toolbar tbJieshao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_production);
        ButterKnife.bind(this);

        initView();
    }



    private void initView() {
        setSupportActionBar(tbJieshao);
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





}
