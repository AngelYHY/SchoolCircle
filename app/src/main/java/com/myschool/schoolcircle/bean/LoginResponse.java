package com.myschool.schoolcircle.bean;

import com.myschool.schoolcircle.entity.Tb_user;

/**
 * 描述：
 * 作者：一颗浪星
 * 日期：2017/5/18 0018
 * github：
 */

public class LoginResponse extends Base{

    /**
     * use : {"user_id":69,"username":"zhengrj","password":"123456","realName":"仁杰","phone":"15359600015","gender":"男","birthday":"1994-09-05","startSchoolYear":"2013","signature":"我的风格～","headView":"http://oap9ibq03.bkt.clouddn.com/FkFxeKO3EkUuBjxHxDA3-wzj-Fgy"}
     * errorMsg :
     * success : true
     */

    private Tb_user use;

    public Tb_user getUse() {
        return use;
    }

    public void setUse(Tb_user use) {
        this.use = use;
    }

//    public static class UseBean {
//        /**
//         * user_id : 69
//         * username : zhengrj
//         * password : 123456
//         * realName : 仁杰
//         * phone : 15359600015
//         * gender : 男
//         * birthday : 1994-09-05
//         * startSchoolYear : 2013
//         * signature : 我的风格～
//         * headView : http://oap9ibq03.bkt.clouddn.com/FkFxeKO3EkUuBjxHxDA3-wzj-Fgy
//         */
//
//        private int user_id;
//        private String username;
//        private String password;
//        private String realName;
//        private String phone;
//        private String gender;
//        private String birthday;
//        private String startSchoolYear;
//        private String signature;
//        private String headView;
//
//        public int getUser_id() {
//            return user_id;
//        }
//
//        public void setUser_id(int user_id) {
//            this.user_id = user_id;
//        }
//
//        public String getUsername() {
//            return username;
//        }
//
//        public void setUsername(String username) {
//            this.username = username;
//        }
//
//        public String getPassword() {
//            return password;
//        }
//
//        public void setPassword(String password) {
//            this.password = password;
//        }
//
//        public String getRealName() {
//            return realName;
//        }
//
//        public void setRealName(String realName) {
//            this.realName = realName;
//        }
//
//        public String getPhone() {
//            return phone;
//        }
//
//        public void setPhone(String phone) {
//            this.phone = phone;
//        }
//
//        public String getGender() {
//            return gender;
//        }
//
//        public void setGender(String gender) {
//            this.gender = gender;
//        }
//
//        public String getBirthday() {
//            return birthday;
//        }
//
//        public void setBirthday(String birthday) {
//            this.birthday = birthday;
//        }
//
//        public String getStartSchoolYear() {
//            return startSchoolYear;
//        }
//
//        public void setStartSchoolYear(String startSchoolYear) {
//            this.startSchoolYear = startSchoolYear;
//        }
//
//        public String getSignature() {
//            return signature;
//        }
//
//        public void setSignature(String signature) {
//            this.signature = signature;
//        }
//
//        public String getHeadView() {
//            return headView;
//        }
//
//        public void setHeadView(String headView) {
//            this.headView = headView;
//        }
//    }
}
