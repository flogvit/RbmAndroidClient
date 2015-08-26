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
    final ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

    public AckStore(Context context) {
        setContext(context);
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

    public void setContext(Context context) {
        if (context!=null) {
            mContext = context.getApplicationContext() == null ? context : context.getApplicationContext();
            mDbHelper = AckStoreDbHelper.get(mContext);
        }
    }

    public void add(Request req) {
        if (mDbHelper==null) return;

        mDbHelper.addRequest(req);
    }

    public void remove(Request req) {
        if (mDbHelper==null) return;

        if (req.hasReqid())
            mDbHelper.removeRequest(req.getReqid());
    }

    public boolean hasId(int id) {
        if (mDbHelper==null) return false;

        return mDbHelper.hasRequest(id);
    }

    public void resend() {
        if (mDbHelper==null) return;
        RbmAndroidClient client = RbmFactory.getRbmClient();
        if (!client.isAuthenticated()) return;
        Log.d("RBM", "Checking resend");
        Request req = mDbHelper.getNext();
        while(req!=null) {
            Log.d("RBM", "Resending request");
            Log.d("RBM", req.data());
            mDbHelper.incRequest(req);
            client.send(req);
            req = mDbHelper.getNext();
        }
    }


}
