package com.myschool.schoolcircle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.myschool.schoolcircle.entity.Tb_chat_message;
import com.myschool.schoolcircle.entity.Tb_message_info;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.utils.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/22.
 */
public class MessageInfoDao {

    private Context context; // 上下文对象，提供给 MySqliteHelper的构造方法是用
    private DBOpenHelper helper;// 创建数据库对象
    private SQLiteDatabase database; // 操作数据库的对象
    private Cursor cursor;// 查询的结果集对象

    /**
     * 构造方法，上下文由外部传入
     *
     * @param context
     */
    public MessageInfoDao(Context context) {
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
     * @param tbMessageInfo
     * @return
     */
    public int insert(Tb_message_info tbMessageInfo) {
        int count = 0;
        open();// 开启数据库
        database = helper.getWritableDatabase(); // 获取一个写入对象

        ContentValues values = new ContentValues();
        // 前面是列名，后面是参数
        Tb_user sender = tbMessageInfo.getSender();
        values.put("receiverUsername", tbMessageInfo.getReceiverUsername());
        values.put("senderUsername", sender.getUsername());
        values.put("senderHeadView", sender.getHeadView());
        values.put("senderRealName", sender.getRealName());
        values.put("content", tbMessageInfo.getContent());
        values.put("time", tbMessageInfo.getTime());
        values.put("type", tbMessageInfo.getType());

        count = (int) database.insert("tb_message_info", null, values);

        close();// 关闭数据库
        return count;
    }

    public int delete(Tb_message_info tbMessageInfo) {
        int flag = 0;
        open();
        database = helper.getWritableDatabase();
        flag = database.delete("tb_message_info", "_id=?",
                new String[] {tbMessageInfo.get_id()+""});

        close();
        return flag;
    }

    /**
     * 查询所有消息
     * @return
     */
    public List<Tb_message_info> queryAllByName(Tb_user user) {

        List<Tb_message_info> infos = new ArrayList<>();
        String columns[] = {"*"};
        String selection = "receiverUsername='" + user.getUsername() + "'";

        open();
        database = helper.getReadableDatabase();
        cursor = database.query("tb_message_info", columns, selection,
                null, null, null, "time DESC");//按照时间倒序

        while (cursor.moveToNext()){
            Tb_user sender = new Tb_user();
            sender.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("senderUsername")));
            sender.setRealName(cursor.getString(cursor.getColumnIndexOrThrow("senderRealName")));
            sender.setHeadView(cursor.getString(cursor.getColumnIndexOrThrow("senderHeadView")));

            //如果可以查找到下一条数据的话
            infos.add(new Tb_message_info(cursor.getInt(cursor.getColumnIndexOrThrow("_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("receiverUsername")),
                    sender,
                    cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    cursor.getString(cursor.getColumnIndexOrThrow("time")),
                    cursor.getString(cursor.getColumnIndexOrThrow("type"))));
        }
        close();
        return infos;
    }

    public void alter(Tb_message_info tbMessageInfo) {

        String values[] = {tbMessageInfo.getSender().getHeadView(),
                            tbMessageInfo.getSender().getRealName(),
                            tbMessageInfo.getContent(),tbMessageInfo.getTime(),
                            tbMessageInfo.getType(),tbMessageInfo.get_id()+""};
        open();
        database = helper.getWritableDatabase();
        database.execSQL("update tb_message_info set " +
                "senderHeadView=?,senderRealName=?,content=?,time=?,type=? where _id=?",values);
        close();
    }

    //判断消息简介是否存在
    public int isExist(String receiverUsername,Tb_user sender,String type) {

        int index = -1;
        String columns[] = {"_id"};
        String selection = "receiverUsername='"+receiverUsername+
                "' and senderUsername='" + sender.getUsername() + "'" +
                " and type='" + type + "'";
        open();
        database = helper.getReadableDatabase();
        cursor = database.query("tb_message_info", columns, selection,
                null, null, null, null);//按照时间倒序

        if (cursor.moveToNext()){
            //如果可以查找到下一条数据的话
            index = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        }

        close();
        return index;
    }

    public int isExist(String type) {

        int index = -1;
        String columns[] = {"_id"};
        String selection = "type='" + type + "'";
        open();
        database = helper.getReadableDatabase();
        cursor = database.query("tb_message_info", columns, selection,
                null, null, null, null);//按照时间倒序

        if (cursor.moveToNext()){
            //如果可以查找到下一条数据的话
            index = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        }

        close();
        return index;
    }
}
