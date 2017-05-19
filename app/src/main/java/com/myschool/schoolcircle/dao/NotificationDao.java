package com.myschool.schoolcircle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_notification;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.utils.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/22.
 */
public class NotificationDao {

    private Context context; // 上下文对象，提供给 MySqliteHelper的构造方法是用
    private DBOpenHelper helper;// 创建数据库对象
    private SQLiteDatabase database; // 操作数据库的对象
    private Cursor cursor;// 查询的结果集对象

    /**
     * 构造方法，上下文由外部传入
     *
     * @param context
     */
    public NotificationDao(Context context) {
        this.context = context;
    }

    /**
     * 打开数据库
     */
    public void open() {
        helper = new DBOpenHelper(context);
    }

    /**
     * 关闭数据库
     */
    public void close() {

        if (cursor != null) {
            // 必须是查询
            cursor.close();
        }
        if (helper != null) {
            helper.close();
        }
    }

    /**
     * 添加消息
     * @param tb
     * @return
     */
    public int insert(Tb_notification tb) {
        int count = 0;
        open();// 开启数据库
        database = helper.getWritableDatabase(); // 获取一个写入对象

        ContentValues values = new ContentValues();
        // 前面是列名，后面是参数
        values.put("avatar", tb.getAvatar());
        values.put("title", tb.getTitle());
        values.put("message", tb.getMessage());
        values.put("time", tb.getTime());
        values.put("type", tb.getType());

        count = (int) database.insert("tb_notification", null, values);

        close();// 关闭数据库
        return count;
    }

    public int delete(Tb_notification tb) {
        int flag = 0;
        open();
        database = helper.getWritableDatabase();
        flag = database.delete("tb_notification", "_id=?",
                new String[] {tb.get_id()+""});

        close();
        return flag;
    }

    /**
     * 查询所有消息
     * @return
     */
    public List<Tb_notification> queryAll() {

        List<Tb_notification> notifications = new ArrayList<>();
        String columns[] = {"*"};

        open();
        database = helper.getReadableDatabase();
        cursor = database.query("tb_notification", columns, null,
                null, null, null, "time DESC");//按照时间倒序

        while (cursor.moveToNext()){

            //如果可以查找到下一条数据的话
            notifications.add(new Tb_notification(
                    cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("avatar")),
                    cursor.getString(cursor.getColumnIndexOrThrow("title")),
                    cursor.getString(cursor.getColumnIndexOrThrow("message")),
                    cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    cursor.getLong(cursor.getColumnIndexOrThrow("time"))));
        }
        close();
        return notifications;
    }
}
