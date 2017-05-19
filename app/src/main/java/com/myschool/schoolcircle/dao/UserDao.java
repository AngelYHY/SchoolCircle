package com.myschool.schoolcircle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.myschool.schoolcircle.entity.Tb_chat_message;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.utils.DBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr.R on 2016/7/23.
 */
public class UserDao {
    private Context context; // 上下文对象，提供给 MySqliteHelper的构造方法是用
    private DBOpenHelper helper;// 创建数据库对象
    private SQLiteDatabase database; // 操作数据库的对象
    private Cursor cursor;// 查询的结果集对象

    /**
     * 构造方法，上下文由外部传入
     *
     * @param context
     */
    public UserDao(Context context) {
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
     * @param tb_user
     * @return
     */
    public int insert(Tb_user tb_user) {
        int count = 0;
        open();// 开启数据库
        database = helper.getWritableDatabase(); // 获取一个写入对象

        ContentValues values = new ContentValues();
        values.put("username", tb_user.getUsername());// 前面是列名，后面是参数
        values.put("headView", tb_user.getHeadView());
        values.put("realName", tb_user.getRealName());
        values.put("gender", tb_user.getGender());
        values.put("birthday", tb_user.getBirthday());
        values.put("startSchoolYear", tb_user.getStartSchoolYear());
        values.put("signature", tb_user.getSignature());
        values.put("phone", tb_user.getPhone());
        values.put("password", tb_user.getPassword());

        count = (int) database.insert("tb_user", null, values);

        close();// 关闭数据库
        return count;
    }

    /**
     * //根据用户名查找用户
     * @param username
     * @return
     */
    public Tb_user query(String username) {
        Tb_user user = null;
        open();
        String columns[] = {"_id","username","realName","headView","gender",
                "birthday","startSchoolYear","signature","phone","password"};
        database = helper.getReadableDatabase();
        String selection = "username='" + username + "'";
        cursor = database.query("tb_user", columns, selection, null, null, null, null);
        if (cursor.moveToNext()){
            //如果可以查找到下一条数据的话
            user = new Tb_user();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setRealName(cursor.getString(cursor.getColumnIndexOrThrow("realName")));
            user.setHeadView(cursor.getString(cursor.getColumnIndexOrThrow("headView")));
            user.setGender(cursor.getString(cursor.getColumnIndexOrThrow("gender")));
            user.setBirthday(cursor.getString(cursor.getColumnIndexOrThrow("birthday")));
            user.setStartSchoolYear(cursor.getString(cursor.getColumnIndexOrThrow("startSchoolYear")));
            user.setSignature(cursor.getString(cursor.getColumnIndexOrThrow("signature")));
            user.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
        }

        close();
        return user;
    }
}
