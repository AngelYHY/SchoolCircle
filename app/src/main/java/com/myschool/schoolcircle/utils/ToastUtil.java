package com.myschool.schoolcircle.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Mr.R on 2016/7/10.
 */
public class ToastUtil {
    private static Toast mToast;

    public static void showToast(Context context,CharSequence text,int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context,text,duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static void showLongToast(Context context,int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context,resId,Toast.LENGTH_LONG);
        } else {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_LONG);
        }
        mToast.show();
    }

    public static void showShortToast(Context context,int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(context,resId,Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
