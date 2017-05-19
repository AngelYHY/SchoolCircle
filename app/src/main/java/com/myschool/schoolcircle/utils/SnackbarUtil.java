package com.myschool.schoolcircle.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

import com.myschool.schoolcircle.main.R;

/**
 * Created by Mr.R on 2016/7/19.
 */
public class SnackbarUtil {

    public static void showSnackBarLong(View view, int resId) {
        Snackbar.make(view,resId,Snackbar.LENGTH_LONG).setAction(R.string.get_it,
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    public static void showSnackBarLong(View view,CharSequence text) {
        Snackbar.make(view,text,Snackbar.LENGTH_LONG).setAction(R.string.get_it,
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    public static void showSnackBarShort(View view,int resId) {
        Snackbar.make(view,resId,Snackbar.LENGTH_SHORT).setAction(R.string.get_it,
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    public static void showSnackBarShort(View view,CharSequence text) {
        Snackbar.make(view,text,Snackbar.LENGTH_SHORT).setAction(R.string.get_it,
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }
}
