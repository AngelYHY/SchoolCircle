package com.myschool.schoolcircle.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myschool.schoolcircle.base.BaseFragment;
import com.myschool.schoolcircle.main.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class ContactFragment extends BaseFragment {

    @Bind(R.id.tabs_contact)
    TabLayout tabsContact;
    @Bind(R.id.vp_contact)
    ViewPager vpContact;

    private View view;

//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        view = inflater.inflate(R.layout.layout_fragment_contact, null);
//        ButterKnife.bind(this, view);
//
//        initView();
//        return view;
//    }

    protected void initView() {
        List<String> titles = new ArrayList<>();
        List<Fragment> fragments = new ArrayList<>();

        titles.add("校友");
        titles.add("专业");
        titles.add("社团");

        fragments.add(new ClassmateFragment());
        fragments.add(new SpecialtyFragment());
        fragments.add(new AssociationFragment());

        tabsContact.addTab(tabsContact.newTab()
                .setText("校友"));
        tabsContact.addTab(tabsContact.newTab()
                .setText("专业"));
        tabsContact.addTab(tabsContact.newTab()
                .setText("社团"));

//        MainFragmentAdapter adapter =
//                new MainFragmentAdapter(activity.getSupportFragmentManager(),
//                titles, fragments);

//        vpContact.setAdapter(adapter);
//        vpContact.setOffscreenPageLimit(2);//设置页面缓存数为2
//        tabsContact.setupWithViewPager(vpContact);
//        tabsContact.setTabsFromPagerAdapter(adapter);
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.layout_fragment_contact;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
//            Log.i("hide", "contact" + hidden);
        }
    }
}
