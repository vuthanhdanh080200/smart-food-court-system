package com.example.smart_food_court_system.model;

import java.util.ArrayList;
import java.util.HashMap;

//Unused class
public class UserCart {
    private String userName;
    private HashMap<String, FoodOrder> foodOrderList;
    private String total;

    public UserCart(){}

    public UserCart(String userName, HashMap<String, FoodOrder> foodOrderList, String total) {
        this.userName = userName;
        this.foodOrderList = foodOrderList;
        this.total = total;
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
}