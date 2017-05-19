package com.myschool.schoolcircle.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class MainFragmentAdapter extends FragmentStatePagerAdapter {
    private List<String> titles;
    private List<Fragment> fragments;

    public MainFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.titles = new ArrayList<>();
        titles.add("消息");
        titles.add("圈子");
        titles.add("校园");
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments==null ? null : fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments==null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles==null ? null : titles.get(position);
    }

}
