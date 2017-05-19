package com.myschool.schoolcircle.entity;

/**
 * 订单表
 * Created by chenxing on 2016/8/17.
 */
public class Tb_order {

    int _id;//订单ID
    String userName;//用户ID
    String realName;
    String orderName;//商品名称
    String orderPhoto;//商品照片
    String orderStatus;//订单状态״̬
    String orderTotal;//订单价格
    String orderAddress;//订单地址ַ

    public Tb_order(){

    }

    public Tb_order(int _id, String userName, String realName,
                    String orderName, String orderPhoto, String orderStatus,
                    String orderTotal, String orderAddress) {
        super();
        this._id = _id;
        this.userName = userName;
        this.realName = realName;
        this.orderName = orderName;
        this.orderPhoto = orderPhoto;
        this.orderStatus = orderStatus;
        this.orderTotal = orderTotal;
        this.orderAddress = orderAddress;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
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

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    public String getOrderPhoto() {
        return orderPhoto;
    }

    public void setOrderPhoto(String orderPhoto) {
        this.orderPhoto = orderPhoto;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }
}
