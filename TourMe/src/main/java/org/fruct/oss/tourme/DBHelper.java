package org.fruct.oss.tourme;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Database helper to access TravelLog database
 * Created by alexander on 24.11.13.
 */

public final class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "brandNewDb", null, 2); // TODO: generate release name
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("db", "--- onCreate database ---");
        db.execSQL("create table " + ConstantsAndTools.TABLE_TRAVELLOG + " ("
                + "id integer primary key autoincrement, "
                + "name text, "
                + "longitude text,"
                + "latitude text,"
                + "date text,"
                + "image text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

