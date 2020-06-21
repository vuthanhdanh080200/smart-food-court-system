package com.example.smart_food_court_system.model;

public class Category {
    private String foodName;
    private String foodImage;

    public Category() {
    }

    public Category(String foodName, String foodImage) {
        this.foodName = foodName;
        this.foodImage = foodImage;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
}
