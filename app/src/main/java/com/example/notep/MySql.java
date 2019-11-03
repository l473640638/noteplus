package com.example.notep;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySql extends SQLiteOpenHelper {
    public final static String TABLE_NAME_RECORD = "record";

    public final static String ID = "_id";
    public final static String TITLE = "title_name";
    public final static String  ARTICLE= "text_body";
    public MySql(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+TABLE_NAME_RECORD+" ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                TITLE+" VARCHAR(30)," +
                ARTICLE+" TEXT)" );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
