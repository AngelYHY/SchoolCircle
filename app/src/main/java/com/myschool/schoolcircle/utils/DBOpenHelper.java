package com.myschool.schoolcircle.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Mr.R on 2016/7/22.
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;

    public DBOpenHelper(Context context) {
        super(context, "college.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 创建用户信息表
        db.execSQL("create table tb_user(" +
                "_id integer primary key autoincrement," +
                "username text not null," +
                "realName text," +
                "headView text," +
                "gender text," +
                "birthday text," +
                "startSchoolYear text," +
                "signature text," +
                "phone text," +
                "password text)");

        //好友表
        db.execSQL("create table tb_friends(" +
                "_id integer primary key autoincrement," +
                "userName text not null," +
                "name text not null)");

        //消息列表
        db.execSQL("create table tb_message_info(" +
                "_id integer primary key autoincrement," +
                "receiverUsername text not null," +
                "senderUsername text not null," +
                "senderHeadView text not null," +
                "senderRealName text not null," +
                "content text not null," +
                "time text not null," +
                "type text not null)");

        //通知列表
        db.execSQL("create table tb_notification(" +
                "_id integer primary key autoincrement," +
                "avatar text," +
                "title text not null," +
                "message text not null," +
                "time bigint," +
                "type text not null)");

        //聊天信息
        db.execSQL("create table tb_chat_messages(" +
                "_id integer primary key autoincrement," +
                "receiverUsername text not null," +
                "senderUsername text not null," +
                "senderHeadView text not null," +
                "senderRealName text not null," +
                "content text not null," +
                "time text not null," +
                "type text not null)");

        //群组表
        db.execSQL("create table tb_group(" +
                "_id integer primary key autoincrement," +
                "groupId integer," +
                "creatorUsername text not null," +
                "groupImage text," +
                "groupName text not null," +
                "groupDescribe text," +
                "groupSchoolName text," +
                "groupGrade text," +
                "groupSpecialty text," +
                "joinNum integer," +
                "label text not null," +
                "createTime text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
