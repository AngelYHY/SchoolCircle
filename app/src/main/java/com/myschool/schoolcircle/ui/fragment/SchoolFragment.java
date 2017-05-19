package com.myschool.schoolcircle.ui.fragment;

import android.view.View;
import android.widget.ImageButton;

import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.main.R;
import com.myschool.schoolcircle.ui.activity.school.activitys.ActivityActivity;
import com.myschool.schoolcircle.ui.activity.school.dynamic.DynamicActivity;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class SchoolFragment extends BaseFragment {
    @Bind(R.id.iv_dynamic)
    ImageButton ivDynamic;
    @Bind(R.id.iv_activity)
    ImageButton ivActivity;
    @Bind(R.id.iv_information)
    ImageButton ivInformation;

    @Override
    protected void initView() {

    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_school;
    }

    @OnClick({R.id.iv_dynamic, R.id.iv_activity})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_dynamic:
                intentToActivity(activity, DynamicActivity.class);
                break;

            case R.id.iv_activity:
                intentToActivity(activity, ActivityActivity.class);
                break;

            default:
                break;
        }
    }

}
