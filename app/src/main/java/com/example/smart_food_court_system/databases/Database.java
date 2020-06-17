package com.example.smart_food_court_system.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.smart_food_court_system.DatabaseHelper.DatabaseHelper;
import com.example.smart_food_court_system.model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME="OrderDb.db";
    private static final int DB_VER=1;

    DatabaseHelper databaseHelper;
    public Database(Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts(){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"ProductID","ProductName","Quantity", "Price"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db,sqlSelect,null,null,null,null, null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst()){
            do{
                Order order = new Order(c.getString(c.getColumnIndex("ProductID")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price"))
                );
                result.add(order);
            }while(c.moveToNext());
        }
        return result;
    }

    /*
    public void addToCart(Order order){
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO OrderDetail(ProductID,ProductName,Quantity,Price) VALUES('%s','%s','%s','%s');",
                order.getProductID(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice());
        db.execSQL(query);
    }
    */

    /*
    public void addToCart(Order order){

        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ProductID", order.getProductID());
        contentValues.put("ProductName", order.getProductName());
        contentValues.put("Quantity", order.getQuantity());
        contentValues.put("Price", order.getPrice());
        db.insert("OrderDetail", null, contentValues);
    }
    */

    public void addToCart(Order order){

        
    }

}
