package com.example.smart_food_court_system.model;

import java.util.ArrayList;

public class Order {
    private String orderID;
    private String userName;
    private ArrayList<FoodOrder> foodOrderList;
    private String total;
    private String TimeStamp;
    private String Status;

    public Order() {
    }

    public Order(String orderID, String userName, ArrayList<FoodOrder> foodOrderList, String total, String timeStamp, String status) {
        this.orderID = orderID;
        this.userName = userName;
        this.foodOrderList = foodOrderList;
        this.total = total;
        TimeStamp = timeStamp;
        Status = status;
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

    public ArrayList<FoodOrder> getFoodOrderList() {
        return foodOrderList;
    }

    public void setFoodOrderList(ArrayList<FoodOrder> foodOrderList) {
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
}
