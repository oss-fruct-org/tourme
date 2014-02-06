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
        super(context, ConstantsAndTools.DATABASE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("db", "--- onCreate database ---");

        // Travellog items database
        db.execSQL("create table " + ConstantsAndTools.TABLE_TRAVELLOG + " ("
                + "id integer primary key autoincrement, "
                + "name text, "
                + "longitude text,"
                + "latitude text,"
                + "location text, "
                + "date text,"
                + "image text" + ");");

        // Database for points storing
        db.execSQL("create table " + ConstantsAndTools.TABLE_WIKIARTICLES + " ("
                + "id integer primary key autoincrement, "
                + "service text, "
                + "latitude text, "
                + "longitude text, "
                + "name text, "
                + "description text, "
                + "type text, "
                + "url text, "
                + "distance text, "
                + "timestamp text"
                +");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

