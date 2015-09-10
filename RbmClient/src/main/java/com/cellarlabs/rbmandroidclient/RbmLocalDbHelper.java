package com.cellarlabs.rbmandroidclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by vhanssen on 25/08/15.
 */
public class RbmLocalDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rbmlocal.db";

    private static RbmLocalDbHelper sInstance;

    public static final String TABLE_NAME = "rbm_store";
    public static final String COL_KEYS = "reqkeys";
    public static final String COL_VALUE = "kvvalue";
    public static final String COL_LASTUPDATED = "lastupdated";

    public static RbmLocalDbHelper get(Context context) {
        if (sInstance == null) {
            Context ctx = context.getApplicationContext() != null ? context.getApplicationContext() : context;
            sInstance = new RbmLocalDbHelper(ctx);
        }

        return sInstance;
    }

    private RbmLocalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        doUpgrade(db, 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        doUpgrade(db, oldVersion);
    }

    private void doUpgrade(SQLiteDatabase db, int oldVersion) {
        if (oldVersion < 1) onCreate1(db);
    }

    private void onCreate1(SQLiteDatabase db) {
        Log.d("RBM", "Creating table");
        db.execSQL("create table " +
                TABLE_NAME + "("
                + COL_KEYS + " text, "
                + COL_VALUE + " text, "
                + COL_LASTUPDATED + " integer)");

        Log.d("RBM", "Creating index");
        db.execSQL("create index ind_" + TABLE_NAME + "_keys on " + TABLE_NAME + "(" + COL_KEYS + ")");
        db.execSQL("create index ind_" + TABLE_NAME + "_lastupdated on " + TABLE_NAME + "(" + COL_LASTUPDATED + ")");
    }

    public String get(String command, String keys) {
        Log.d("RBM", "Reading key "+command+" "+keys);
        SQLiteDatabase db = this.getReadableDatabase();

        String key = command+":"+keys;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, COL_KEYS+"=?",
                    new String[] { key }, null, null, null );
            if (cursor == null)
                return null;

            if(cursor.moveToFirst()){
                return cursor.getString(cursor.getColumnIndex(COL_VALUE));
            }
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public String get(String command, String[] keys) {
        return get(command, TextUtils.join(":", keys));
    }

    public void set(String command, String[] keys, String value) {
        set(command, TextUtils.join(":", keys), value);
    }

    public void set(String command, String keys, String value) {
        Log.d("RBM", "Storing key " + command+" "+keys+" "+value);
        ContentValues values = new ContentValues();
        values.put(COL_VALUE, value);
        values.put(COL_LASTUPDATED, System.currentTimeMillis());

        String key = command+":"+keys;
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            if (db.update(TABLE_NAME, values, COL_KEYS + "=?", new String[]{key}) == 0) {
                Log.d("RBM", "Trying insert");
                values.put(COL_KEYS, key);
                db.insert(TABLE_NAME, null, values);
            }
        } catch(Exception e) {
            Log.d("RBM", "Got exception");
            Log.d("RBM", e.toString());
        } finally {
            Log.d("RBM", "Finished storing");
        }
    }
}