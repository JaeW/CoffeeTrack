package com.nemwick.coffeetrack.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class CoffeeDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "coffee.db";
    public static final int DATABASE_VERSION = 1;

    public static final String SQL_CREATE_COFFEE_TABLE = "CREATE TABLE "
            + CoffeeContract.CoffeeEntry.TABLE_NAME + "("
            + CoffeeContract.CoffeeEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + CoffeeContract.CoffeeEntry.COLUMN_COFFEE_TIME + " INTEGER NOT NULL);";

    public static final String SQL_DELETE_COFFEE_TABLE = "DROP TABLE IF EXISTS "
            + CoffeeContract.CoffeeEntry.TABLE_NAME;


    public CoffeeDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COFFEE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_COFFEE_TABLE);
        onCreate(db);
    }
}
