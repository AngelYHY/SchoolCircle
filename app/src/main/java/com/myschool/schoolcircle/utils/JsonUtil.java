package com.myschool.schoolcircle.utils;

import android.util.Log;

import com.myschool.schoolcircle.entity.Tb_user;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mr.R on 2016/7/18.
 */
public class JsonUtil {

    public static Tb_user json2Tb_user(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);

            Tb_user student = new Tb_user();
            Log.i("json",jsonObject.getInt("user_id")+"");
            student.setId(jsonObject.getInt("user_id"));
            student.setUsername(jsonObject.getString("user_name"));
            student.setGender(jsonObject.getString("sex"));
            student.setBirthday(jsonObject.getString("birthday"));
            student.setStartSchoolYear(jsonObject.getString("start_school_year"));
            student.setSignature(jsonObject.getString("signature"));
            student.setPhone(jsonObject.getString("phone"));
            student.setPassword(jsonObject.getString("password"));

            return student;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

}
