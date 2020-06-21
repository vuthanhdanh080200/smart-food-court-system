package com.example.smart_food_court_system.model;

//Class Food có các trường
//+Food_Id: int
//+FoodStall_id: int
//+FoodName: string
//+Type: string
//+Price: int
//+Description: string
//+Amount: int

public class Food {
    private String foodID;
    private String foodName;
    private String foodPrice;
    private String foodImage;
    private String Remaining;

    public Food() {
    }

    public Food(String foodID, String foodName, String foodPrice, String foodImage, String remaining) {
        this.foodID = foodID;
        this.foodName = foodName;
        this.foodPrice = foodPrice;
        this.foodImage = foodImage;
        this.Remaining = remaining;

    }

    public String getFoodID() {
        return foodID;
    }

    public void setFoodID(String foodID) {
        this.foodID = foodID;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

    public void setFoodPrice(String foodPrice) {
        this.foodPrice = foodPrice;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public void setFoodImage(String foodImage) {
        this.foodImage = foodImage;
    }

    public String getRemaining() {
        return Remaining;
    }

    public void setRemaining(String remaining) {
        Remaining = remaining;
    }

}
