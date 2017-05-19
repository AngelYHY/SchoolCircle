package com.myschool.schoolcircle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myschool.schoolcircle.entity.Tb_chat_message;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.utils.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/22.
 */
public class ChatMessageDao {

    private Context context; // 上下文对象，提供给 MySqliteHelper的构造方法是用
    private DBOpenHelper helper;// 创建数据库对象
    private SQLiteDatabase database; // 操作数据库的对象
    private Cursor cursor;// 查询的结果集对象

    /**
     * 构造方法，上下文由外部传入
     *
     * @param context
     */
    public ChatMessageDao(Context context) {
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
     * @param tb_chat_message
     * @return
     */
    public int insert(Tb_chat_message tb_chat_message) {
        int count = 0;
        Tb_user sender = tb_chat_message.getSender();
        open();// 开启数据库
        database = helper.getWritableDatabase(); // 获取一个写入对象
        ContentValues values = new ContentValues();
        values.put("receiverUsername", tb_chat_message.getReceiverUsername());// 前面是列名，后面是参数
        values.put("senderUsername", sender.getUsername());
        values.put("senderHeadView",sender.getHeadView());
        values.put("senderRealName",sender.getRealName());
        values.put("content", tb_chat_message.getContent());
        values.put("time", tb_chat_message.getTime());
        values.put("type", tb_chat_message.getType());

        count = (int) database.insert("tb_chat_messages", null, values);

        close();// 关闭数据库
        return count;
    }

    /**
     * 根据好友账号查询所有消息
     * @return
     */
    public List<Tb_chat_message> queryAll(String receiverUsername, String senderUsername) {

        List<Tb_chat_message> messages = new ArrayList<>();
        open();
        String columns[] = {"receiverUsername","senderUsername","senderHeadView",
                "senderRealName","content","time","type"};
        database = helper.getReadableDatabase();
        String selection = "receiverUsername='" + receiverUsername + "' and " +
                "senderUsername='" + senderUsername + "'";
        cursor = database.query("tb_chat_messages", columns, selection, null, null, null, null);
        while (cursor.moveToNext()){
            //如果可以查找到下一条数据的话
            Tb_user sender = new Tb_user();
            messages.add(new Tb_chat_message(
                    cursor.getString(cursor.getColumnIndexOrThrow("receiverUsername")),
                    sender,
                    cursor.getString(cursor.getColumnIndexOrThrow("content")),
                    cursor.getString(cursor.getColumnIndexOrThrow("time")),
                    cursor.getString(cursor.getColumnIndexOrThrow("type"))));
        }

        close();
        return messages;
    }

}
