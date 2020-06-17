package com.example.smart_food_court_system.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "database_name";
    public static final String TABLE_NAME = "table_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable= "CREATE TABLE " + TABLE_NAME + "(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE, ProductID TEXT, ProductName TEXT, Quantity TEXT, Price TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public boolean addText(String ProductID, String ProductName, String Quantity, String Price){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("ProductID", ProductID);
        contentValues.put("ProductName", ProductName);
        contentValues.put("Quantity", Quantity);
        contentValues.put("Price", Price);
        db.insert(TABLE_NAME, null, contentValues);
        return true;
    }
}
