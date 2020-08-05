package com.example.smart_food_court_system.model;

import java.util.ArrayList;
import java.util.HashMap;

public class Order {
    private String orderID;
    private String userName;
    private HashMap<String, FoodOrder> foodOrderList;
    private String total;
    private String TimeStamp;
    private String Status;
    private String MethodPayment;

    public Order() {
    }

    public Order(String orderID, String userName, HashMap<String, FoodOrder> foodOrderList, String total, String timeStamp, String status, String methodPayment) {
        this.orderID = orderID;
        this.userName = userName;
        this.foodOrderList = foodOrderList;
        this.total = total;
        TimeStamp = timeStamp;
        Status = status;
        MethodPayment = methodPayment;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public HashMap<String, FoodOrder> getFoodOrderList() {
        return foodOrderList;
    }

    public void setFoodOrderList(HashMap<String, FoodOrder> foodOrderList) {
        this.foodOrderList = foodOrderList;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getMethodPayment() {
        return MethodPayment;
    }

    public void setMethodPayment(String methodPayment) {
        MethodPayment = methodPayment;
    }
}
