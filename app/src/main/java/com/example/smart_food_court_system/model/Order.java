package com.example.smart_food_court_system.model;

import java.util.ArrayList;

public class Order {
    private FoodOrder foodOrderList;
    private String orderID;
    private String status;
    private String timeStamp;
    private String total;
    private String userName;

    public FoodOrder getFoodOrderList() { return foodOrderList; }
    public void setFoodOrderList(FoodOrder value) { this.foodOrderList = value; }
    public String getOrderID() { return orderID; }
    public void setOrderID(String value) { this.orderID = value; }
    public String getStatus() { return status; }
    public void setStatus(String value) { this.status = value; }
    public String getTimeStamp() { return timeStamp; }
    public void setTimeStamp(String value) { this.timeStamp = value; }
    public String getTotal() { return total; }
    public void setTotal(String value) { this.total = value; }
    public String getUserName() { return userName; }
    public void setUserName(String value) { this.userName = value; }
}
