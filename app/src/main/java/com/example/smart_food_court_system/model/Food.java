package com.example.smart_food_court_system.model;

public class Food {
    private String foodStallName;
    private String foodName;
    private String foodType;
    private String foodPrice;
    private String foodDescription;
    private String foodRemaining;
    private String foodImage;

    public Food() {
    }

    public Food(String foodStallName, String foodName, String foodType, String foodPrice, String foodDescription, String foodRemaining, String foodImage) {
        this.foodStallName = foodStallName;
        this.foodName = foodName;
        this.foodType = foodType;
        this.foodPrice = foodPrice;
        this.foodDescription = foodDescription;
        this.foodRemaining = foodRemaining;
        this.foodImage = foodImage;
    }

    public String getFoodStallName() {
        return foodStallName;
    }

    public void setFoodStallName(String foodStallName) {
        this.foodStallName = foodStallName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodType() {
        return foodType;
    }

    public void setFoodType(String foodType) {
        this.foodType = foodType;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodDescription() {
        return foodDescription;
    }

    public void setFoodDescription(String foodDescription) {
        this.foodDescription = foodDescription;
    }

    public String getFoodRemaining() {
        return foodRemaining;
    }

    public void setFoodRemaining(String foodRemaining) {
        this.foodRemaining = foodRemaining;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }
}
