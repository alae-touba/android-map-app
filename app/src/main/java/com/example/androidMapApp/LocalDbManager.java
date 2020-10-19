package com.example.androidMapApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocalDbManager extends SQLiteOpenHelper {

    private static final String DB_NAME = "mylocaldatabase";
    private static final int DB_VERSION = 1;

    public LocalDbManager(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating the table ( tab(_id, name) )
        db.execSQL("CREATE TABLE locations (_id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, description TEXT," +
                " latitude FLOAT, longitude FLOAT, date TEXT, number_votes INTEGER, sum_votes INTEGER )" );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }



}