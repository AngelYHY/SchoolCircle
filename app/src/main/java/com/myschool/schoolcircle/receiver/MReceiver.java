package com.myschool.schoolcircle.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.myschool.schoolcircle.ui.activity.school.activitys.ActivityActivity;
import com.myschool.schoolcircle.dao.NotificationDao;
import com.myschool.schoolcircle.entity.Tb_notification;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Mr.R on 2016/7/20.
 */
public class MReceiver extends BroadcastReceiver {
    private static final String TAG = "MyReceiver";
    private static final String TYPE_ACTIVITY = "activity";
    private static final String TYPE_DYNAMIC = "dynamic";
    private static final String TYPE_FRIEND_APPLY = "friendApply";
    private static final String TYPE_GROUP_APPLY = "groupApply";

    private NotificationManager nm;
    private NotificationDao dao;

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
//            Bundle bundle = intent.getExtras();
//            String title = bundle.getString(JPushInterface.EXTRA_TITLE);
//            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
//
//            Toast.makeText(context, "标题：" +
//                    title + "内容：" + message, Toast.LENGTH_LONG).show();
//        }

        if (null == nm) {
            nm = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();
        Log.i("MReceiver", "onReceive: MReceiver");
//        Logger.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + AndroidUtil.printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.i("MReceiver", "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.i("MReceiver", "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.i("MReceiver", "接受到推送下来的通知");
            receivingNotification(context,bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            Log.i(TAG, "用户点击打开了通知");
            openNotification(context,bundle);

        } else {
            Log.i(TAG, "Unhandled intent - " + intent.getAction());
        }

    }

    //接收通知
    private void receivingNotification(Context context, Bundle bundle){
//        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        Gson gson = new Gson();
        Type dataType = new TypeToken<Map<String,String>>() {}.getType();
        Map<String,String> map = gson.fromJson(extras, dataType);

        String title = map.get("title");
        String type = map.get("type");
        String time = map.get("time");

        dao = new NotificationDao(context);
        Tb_notification notification = new Tb_notification();
        notification.setTitle(title);
        notification.setMessage(message.substring(4));
        try {
            long datetime = stringToLong(time,"yyyy-MM-dd HH:mm:ss");
            notification.setTime(datetime);
            notification.setType(type);
            int flag = dao.insert(notification);
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        if (flag != 0) {
//            Log.i(TAG, " title : " + title);
//            Log.i(TAG, "message : " + message);
//            Log.i(TAG, "extras : " + type);
//        } else {
//            Log.i(TAG, "通知存储失败");
//        }
    }

    //打开通知
    private void openNotification(Context context, Bundle bundle){
        Intent mIntent = null;
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        Gson gson = new Gson();
        Type type = new TypeToken<Map<String,String>>() {}.getType();
        Map<String,String> map = gson.fromJson(extras, type);
        String myType = map.get("type");

        switch (myType) {
            case TYPE_ACTIVITY:
                mIntent = new Intent(context, ActivityActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(mIntent);
                break;
            case TYPE_DYNAMIC:

                break;
            case TYPE_FRIEND_APPLY:

                break;
            case TYPE_GROUP_APPLY:

                break;
            default:
                break;
        }

    }

    // string类型转换为long类型
    // strTime要转换的String类型的时间
    // formatType时间格式
    // strTime的时间格式和formatType的时间格式必须相同
    public static long stringToLong(String strTime, String formatType)
            throws ParseException {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    // date类型转换为long类型
    // date要转换的date类型的时间
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    // string类型转换为date类型
    // strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
    // HH时mm分ss秒，
    // strTime的时间格式必须要与formatType的时间格式相同
    public static Date stringToDate(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }
}
