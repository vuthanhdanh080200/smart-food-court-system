package com.example.smart_food_court_system;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.example.smart_food_court_system.model.Food;

import java.util.ArrayList;

public class FoodAdapter extends ArrayAdapter<Food> {


    public FoodAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
}
