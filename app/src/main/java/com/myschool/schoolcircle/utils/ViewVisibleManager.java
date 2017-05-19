package com.myschool.schoolcircle.utils;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by Mr.R on 2016/7/14.
 */
public class ViewVisibleManager {

    //隐藏
    public static void hideViews(Toolbar toolbar, FloatingActionButton floatingActionButton) {
        //隐藏toolbar
        toolbar.animate().translationY(-toolbar.getHeight()).
                setInterpolator(new AccelerateInterpolator(2));

        if (floatingActionButton != null) {
            //隐藏悬浮按钮
            CoordinatorLayout.LayoutParams lp =
                    (CoordinatorLayout.LayoutParams) floatingActionButton
                    .getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
//            floatingActionButton.animate()
//                    .translationY(floatingActionButton.getHeight() + fabBottomMargin).
//                    setInterpolator(new AccelerateInterpolator(2))
//                    .start();
            floatingActionButton.hide();
        }
    }

    //显示
    public static void showViews(Toolbar toolbar, FloatingActionButton floatingActionButton) {
        toolbar.animate().translationY(0).
                setInterpolator(new DecelerateInterpolator(2));

        if (floatingActionButton != null) {
//            floatingActionButton.animate().translationY(0).
//                    setInterpolator(new DecelerateInterpolator(2))
//                    .start();
            floatingActionButton.show();
        }
    }

}
