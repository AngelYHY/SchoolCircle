package com.myschool.schoolcircle.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.myschool.schoolcircle.ui.fragment.ActivityFragment;
import com.myschool.schoolcircle.ui.fragment.Contact;
import com.myschool.schoolcircle.ui.fragment.DynamicFragment;
import com.myschool.schoolcircle.ui.fragment.MessageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class FragmentController {
    private static FragmentController instance;
    private int containerId;
    private FragmentManager manager;
    private List<Fragment> fragments;

    private FragmentController(int containerId, FragmentManager manager) {
        this.containerId = containerId;
        this.manager = manager;

        initFragment();
    }

    public static FragmentController getInstance(int containerId,FragmentManager manager) {
        instance = new FragmentController(containerId,manager);
        return instance;
    }

    private void initFragment() {
        fragments = new ArrayList<>();
        fragments.add(new MessageFragment());
        fragments.add(new Contact());
        fragments.add(new DynamicFragment());
        fragments.add(new ActivityFragment());

        FragmentTransaction transaction = manager.beginTransaction();
        for (Fragment fragment : fragments) {
            transaction.add(containerId,fragment);
            transaction.hide(fragment);
        }

        transaction.show(fragments.get(0));
        transaction.commit();
    }

    //显示fragment
    public void showFragment(int position) {
        hideFragments();
        Fragment fragment = fragments.get(position);
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
    }

    //隐藏fragment
    public void hideFragments() {
        FragmentTransaction transaction = manager.beginTransaction();
        for (Fragment fragment : fragments) {
            if (fragment != null) {
                transaction.hide(fragment);
            }
        }
        transaction.commit();
    }

    public Fragment getFragment(int position) {
        return fragments==null ? null : fragments.get(position);
    }
}
