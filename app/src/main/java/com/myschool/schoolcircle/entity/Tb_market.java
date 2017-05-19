package com.myschool.schoolcircle.entity;

/**
 * 商品表
 * Created by chenxing on 2016/8/16.
 */
public class Tb_market {

    int _id;// ID
    String userName;
    String realName;
    String marketName;// 商品名称
    //    String marketImage;// 商品照片
    int marketImage;
    String marketTotal;
    String marketPrice;// 商品价格
    String marketDescribe;// 商品描述
    String marketTime;
    String userPhone;

    public Tb_market() {

    }

    public Tb_market(int _id, int marketImage, String marketDescribe, String marketTotal, String marketPrice, String userPhone) {
        this._id = _id;
        this.marketImage = marketImage;
        this.marketDescribe = marketDescribe;
        this.marketTotal = marketTotal;
        this.marketPrice = marketPrice;
        this.userPhone = userPhone;
    }

    public Tb_market(String marketName, int marketImage, String marketPrice, String marketDescribe, String marketTime) {
        this.marketName = marketName;
        this.marketImage = marketImage;
        this.marketPrice = marketPrice;
        this.marketDescribe = marketDescribe;
        this.marketTime = marketTime;
    }

    public Tb_market(int _id, String userName, String realName,
                     String marketName, int marketImage, String marketPrice,
                     String marketDescribe, String marketTime) {
        super();
        this._id = _id;
        this.userName = userName;
        this.realName = realName;
        this.marketName = marketName;
        this.marketImage = marketImage;
        this.marketPrice = marketPrice;
        this.marketDescribe = marketDescribe;

        this.marketTime = marketTime;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getMarketTotal() {
        return marketTotal;
    }

    public void setMarketTotal(String marketTotal) {
        this.marketTotal = marketTotal;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public int getMarketImage() {
        return marketImage;
    }

    public void setMarketImage(int marketImage) {
        this.marketImage = marketImage;
    }

    public String getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(String marketPrice) {
        this.marketPrice = marketPrice;
    }

    public String getMarketDescribe() {
        return marketDescribe;
    }

    public void setMarketDescribe(String marketDescribe) {
        this.marketDescribe = marketDescribe;
    }

    public String getMarketTime() {
        return marketTime;
    }

    public void setMarketTime(String marketTime) {
        this.marketTime = marketTime;
    }
}