package com.cellarlabs.rbmandroidclient;

import android.content.Context;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by vhanssen on 25/08/15.
 */
public class AckStore {
    protected Context mContext = null;
    protected AckStoreDbHelper mDbHelper = null;
    protected ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
    protected static AckStore instance;

    private AckStore(Context context) {
        setContext(context);

    }

    public static AckStore getAckStore() {
        if (instance==null)
            instance = new AckStore(null);
        return instance;
    }

    public void setContext(Context context) {
        setContext(context, false);
    }

    public void setContext(Context context, boolean clear) {
        if (context!=null) {
            mContext = context.getApplicationContext() == null ? context : context.getApplicationContext();
            mDbHelper = AckStoreDbHelper.get(mContext);
            if (clear)
                mDbHelper.removeAll();
            if (exec!=null) {
                exec.shutdown();
            }
            exec = Executors.newScheduledThreadPool(1);
            exec.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    try {
                        resend();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 5, 10, TimeUnit.SECONDS);
        }
    }

    public void add(Request req) {
        if (mDbHelper==null) return;

        Log.d("RBM", "Adding request to store");
        Log.d("RBM", req.data());
        mDbHelper.addRequest(req);
    }

    public void remove(Request req) {
        if (mDbHelper==null) return;

        Log.d("RBM", "Removing resend request");
        Log.d("RBM", req.data());
        if (req.hasReqid())
            mDbHelper.removeRequest(req.getReqid());
    }

    public boolean hasId(int id) {
        if (mDbHelper==null) return false;

        return mDbHelper.hasRequest(id);
    }

    public void resend() {
        if (mContext==null) {
            if (exec!=null) {
                exec.shutdown();
                exec = null;
            }
        }
        if (mDbHelper==null) return;
        RbmAndroidClient client = RbmFactory.getRbmClient();
        if (!client.isAuthenticated()) return;
        Log.d("RBM", "Checking resend");
        Request req = mDbHelper.getNext();
        while(req!=null) {
            if (req.getCount()>5) {
                Log.d("RBM", "Deleting resend request");
                Log.d("RBM", req.data());
                mDbHelper.removeRequest(req.getReqid());
            } else {
                Log.d("RBM", "Resending request");
                Log.d("RBM", req.data());
                mDbHelper.incRequest(req);
                client.send(req);
            }
            req = mDbHelper.getNext();
        }
    }

    public void onStop() {
        if (exec!=null)
            exec.shutdown();
    }

    public void clear() {
        if (mDbHelper==null) return;
        mDbHelper.removeAll();
    }
}
