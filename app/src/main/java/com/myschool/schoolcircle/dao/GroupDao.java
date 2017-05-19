package com.myschool.schoolcircle.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.myschool.schoolcircle.entity.Tb_group;
import com.myschool.schoolcircle.entity.Tb_user;
import com.myschool.schoolcircle.utils.DBOpenHelper;

/**
 * Created by Mr.R on 2016/7/23.
 */
public class GroupDao {
    private Context context; // 上下文对象，提供给 MySqliteHelper的构造方法是用
    private DBOpenHelper helper;// 创建数据库对象
    private SQLiteDatabase database; // 操作数据库的对象
    private Cursor cursor;// 查询的结果集对象

    /**
     * 构造方法，上下文由外部传入
     *
     * @param context
     */
    public GroupDao(Context context) {
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
     * 添加群组
     * @param tb
     * @return
     */
    public int insert(Tb_group tb) {
        int count = 0;
        open();// 开启数据库
        database = helper.getWritableDatabase(); // 获取一个写入对象

        ContentValues values = new ContentValues();
        values.put("groupId", tb.getGroupId());// 前面是列名，后面是参数
        values.put("creatorUsername", tb.getCreatorUsername());
        values.put("groupImage", tb.getGroupImage());
        values.put("groupName", tb.getGroupName());
        values.put("groupDescribe", tb.getGroupDescribe());
        values.put("groupSchoolName", tb.getGroupSchoolName());
        values.put("groupGrade", tb.getGroupGrade());
        values.put("groupSpecialty", tb.getGroupSpecialty());
        values.put("joinNum", tb.getJoinNum());
        values.put("label", tb.getLabel());
        values.put("createTime", tb.getCreateTime());

        count = (int) database.insert("tb_group", null, values);

        close();// 关闭数据库
        return count;
    }

    /**
     * //根据群组id查找
     * @param groupId
     * @return
     */
    public Tb_group query(long groupId) {
        Tb_group tb = null;
        open();
        String columns[] = {"_id","groupId","creatorUsername","groupImage",
                "groupName", "groupDescribe","groupSchoolName","groupGrade",
                "groupSpecialty","joinNum","label","createTime"};

        database = helper.getReadableDatabase();
        String selection = "groupId='" + groupId + "'";
        cursor = database.query("tb_group", columns, selection, null, null, null, null);
        if (cursor.moveToNext()){
            //如果可以查找到下一条数据的话
            tb = new Tb_group();
            tb.setId(cursor.getInt(cursor.getColumnIndexOrThrow("_id")));
            tb.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("groupId")));
            tb.setCreatorUsername(cursor.getString(cursor.getColumnIndexOrThrow("creatorUsername")));
            tb.setGroupImage(cursor.getString(cursor.getColumnIndexOrThrow("groupImage")));
            tb.setGroupName(cursor.getString(cursor.getColumnIndexOrThrow("groupName")));
            tb.setGroupDescribe(cursor.getString(cursor.getColumnIndexOrThrow("groupDescribe")));
            tb.setGroupSchoolName(cursor.getString(cursor.getColumnIndexOrThrow("groupSchoolName")));
            tb.setGroupGrade(cursor.getString(cursor.getColumnIndexOrThrow("groupGrade")));
            tb.setGroupSpecialty(cursor.getString(cursor.getColumnIndexOrThrow("groupSpecialty")));
            tb.setJoinNum(cursor.getInt(cursor.getColumnIndexOrThrow("joinNum")));
            tb.setLabel(cursor.getString(cursor.getColumnIndexOrThrow("label")));
            tb.setCreateTime(cursor.getString(cursor.getColumnIndexOrThrow("createTime")));
        }

        close();
        return tb;
    }
}
