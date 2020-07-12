package com.example.smart_food_court_system.model;

public class FoodOrder {
    private String FoodName;
    private String Quantity;
    private String Price;
    private String FoodStallName;

    public FoodOrder() {
    }

    public FoodOrder(String foodName, String quantity, String price, String foodStallName) {
        FoodName = foodName;
        Quantity = quantity;
        Price = price;
        FoodStallName = foodStallName;
    }

    public String getFoodName() {
        return FoodName;
    }

    public void setFoodName(String foodName) {
        FoodName = foodName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getFoodStallName() {
        return FoodStallName;
    }

    public void setFoodStallName(String foodStallName) {
        FoodStallName = foodStallName;
    }
}

