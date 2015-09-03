package com.cellarlabs.rbmandroidclient;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by vhanssen on 25/08/15.
 */
public class AckStoreDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "rbmackstore.db";

    private static AckStoreDbHelper sInstance;

    public static final String TABLE_NAME = "req_store";
    public static final String COL_REQID = "reqid";
    public static final String COL_REQ = "req";
    public static final String COL_RESEND = "resend";
    public static final String COL_COUNT = "count";

    private int resendIntervallMS = 1000*60*2;

    public static AckStoreDbHelper get(Context context) {
        if (sInstance == null) {
            Context ctx = context.getApplicationContext() != null ? context.getApplicationContext() : context;
            sInstance = new AckStoreDbHelper(ctx);
        }

        return sInstance;
    }

    private AckStoreDbHelper(Context context) {
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
        db.execSQL("create table " +
                TABLE_NAME + "("
                + COL_REQID + " integer primary key, "
                + COL_REQ + " text,"
                + COL_COUNT + " integer default 0,"
                + COL_RESEND + " real)");

        db.execSQL("create index ind_" + TABLE_NAME + "_reqid on " + TABLE_NAME + "(" + COL_REQID + ")");
        db.execSQL("create index ind_" + TABLE_NAME + "_resend on " + TABLE_NAME + "(" + COL_RESEND + ")");
    }

    public void addRequest(Request req) {
        double resend = resendIntervallMS;
        if (req.getResendAfter()>0)
            resend = req.getResendAfter();
        ContentValues values = new ContentValues();
        values.put(COL_REQID, req.getReqid());
        values.put(COL_REQ, req.data());
        values.put(COL_RESEND, System.currentTimeMillis() + resend);

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.insert(TABLE_NAME, null, values);
        } finally {
        }
    }

    public Request getRequest(int reqid) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        Request req = null;
        try {
            cursor = db.query(TABLE_NAME, null, COL_REQID+"=?",
                    new String[] { String.valueOf(reqid) }, null, null, null );
            if (cursor == null)
                return null;

            cursor.moveToFirst();
            req = populateRequest(cursor);
        } finally {
            if (cursor != null)
                cursor.close();
            return req;
        }
    }

    public boolean hasRequest(int reqid) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = null;
        Request req = null;
        try {
            cursor = db.query(TABLE_NAME, new String[] { COL_REQID }, COL_REQID+"=?",
                    new String[] { String.valueOf(reqid) }, null, null, null );
            if (cursor == null)
                return false;

            return cursor.getCount()>0 ? true : false;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public void removeRequest(int reqid) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, COL_REQID + " = ?",
                    new String[]{String.valueOf(reqid)});
        } finally {
        }
    }

    public void removeAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_NAME, null,
                    null);
        } finally {
        }
    }

    public Request getNext() {
        SQLiteDatabase db = this.getReadableDatabase();

        Request req = null;
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, null, COL_RESEND+" < ?",
                new String[] { String.valueOf(System.currentTimeMillis())}, null, null, null );
            if (cursor==null || cursor.getCount()==0) {
                return null;
            }
            cursor.moveToFirst();
            req = populateRequest(cursor);
        } finally {
            if (cursor!=null)
                cursor.close();
            return req;
        }
    }

    public void incRequest(Request req) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!req.hasReqid()) return;
        try {
            db.execSQL("UPDATE " + TABLE_NAME + " SET " + COL_COUNT + "=" + COL_COUNT + "+1" + ", "+COL_RESEND+"="+(System.currentTimeMillis()+resendIntervallMS)+" WHERE " + COL_REQID + "=?",
                    new String[]{String.valueOf(req.getReqid())});
        } finally {

        }
    }

    public Request populateRequest(Cursor cursor) {
        Request req = new Request(cursor.getString(cursor.getColumnIndex(COL_REQ)));
        req.setReqid(cursor.getInt(cursor.getColumnIndex(COL_REQID)));
        req.setCount(cursor.getInt(cursor.getColumnIndex(COL_COUNT)));
        req.setResendAfter(cursor.getDouble(cursor.getColumnIndex(COL_RESEND)) - System.currentTimeMillis());
        double test = cursor.getDouble(cursor.getColumnIndex(COL_RESEND));
        return req;
    }
}