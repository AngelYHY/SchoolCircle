package com.myschool.schoolcircle.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Mr.R on 2016/8/12.
 */
public class ProgressDialogUtil {
    private static ProgressDialog mProgressDialog;

    public static ProgressDialog getProgressDialog(Context context) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setCanceledOnTouchOutside(false);

        return mProgressDialog;
    }

    public static ProgressDialog getProgressDialog(Context context,String msg) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(msg);
        mProgressDialog.setCanceledOnTouchOutside(false);

        return mProgressDialog;
    }
}
