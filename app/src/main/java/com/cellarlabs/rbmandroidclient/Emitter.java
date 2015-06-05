package com.cellarlabs.rbmandroidclient;

/**
 * Created by vhanssen on 04/06/15.
 *
 * This class builds on engine.io-client Emitter
 * https://github.com/nkzawa/engine.io-client.java/blob/master/src/main/java/com/github/nkzawa/emitter/Emitter.java
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Emitter class for RBM
 *
 *
 */
public class Emitter {

    private ConcurrentMap<String, ConcurrentLinkedQueue<Listener>> callbacks
            = new ConcurrentHashMap<String, ConcurrentLinkedQueue<Listener>>();

    /**
     * Listens on the event.
     *
     * @param event event name.
     * @param fn
     * @return a reference to this object.
     */
    public Emitter on(String event, Listener fn) {
        ConcurrentLinkedQueue<Listener> callbacks = this.callbacks.get(event);
        if (callbacks == null) {
            callbacks = new ConcurrentLinkedQueue<Listener>();
            ConcurrentLinkedQueue<Listener> _callbacks = this.callbacks.putIfAbsent(event, callbacks);
            if (_callbacks != null) {
                callbacks = _callbacks;
            }
        }
        callbacks.add(fn);
        return this;
    }

    /**
     * Adds a one time listener for the event.
     *
     * @param event an event name.
     * @param fn
     * @return a reference to this object.
     */
    public Emitter once(final String event, final Listener fn) {
        this.on(event, new OnceListener(event, fn));
        return this;
    }

    /**
     * Removes all registered listeners.
     *
     * @return a reference to this object.
     */
    public Emitter off() {
        this.callbacks.clear();
        return this;
    }

    /**
     * Removes all listeners of the specified event.
     *
     * @param event an event name.
     * @return a reference to this object.
     */
    public Emitter off(String event) {
        this.callbacks.remove(event);
        return this;
    }

    /**
     * Removes a listener from an event
     *
     * @param event an event name.
     * @param fn
     * @return a reference to this object.
     */
    public Emitter off(String event, Listener fn) {
        ConcurrentLinkedQueue<Listener> callbacks = this.callbacks.get(event);
        if (callbacks != null) {
            Iterator<Listener> it = callbacks.iterator();
            while (it.hasNext()) {
                Listener internal = it.next();
                if (Emitter.sameAs(fn, internal)) {
                    it.remove();
                    break;
                }
            }
        }
        return this;
    }

    /**
     * Removes the RbmListener from all events
     *
     * @param fn
     * @return a reference to this object.
     */
    public Emitter off(Listener fn) {
        for (String event : this.callbacks.keySet()) {
            off(event, fn);
        }
        return this;
    }

    private static boolean sameAs(Listener fn, Listener internal) {
        if (fn.equals(internal)) {
            return true;
        } else if (internal instanceof OnceListener) {
            return fn.equals(((OnceListener) internal).fn);
        } else {
            return false;
        }
    }

    /**
     * Executes each of listeners on an event with the RbmRequest object.
     *
     * @param event         an event name.
     * @param {RBM_Request} req
     * @return a reference to this object.
     */
    public Emitter emit(String event, Request req) {
        ConcurrentLinkedQueue<Listener> callbacks = this.callbacks.get(event);
        if (callbacks != null) {
            for (Listener fn : callbacks) {
                fn.onResponse(req);
            }
        }
        return this;
    }

    /**
     * Returns a list of all listeners for the specified event.
     *
     * @param event an event name.
     * @return a reference to this object.
     */
    public List<Listener> listeners(String event) {
        ConcurrentLinkedQueue<Listener> callbacks = this.callbacks.get(event);
        return callbacks != null ?
                new ArrayList<Listener>(callbacks) : new ArrayList<Listener>(0);
    }

    /**
     * Check if this emitter has listeners for the specified event.
     *
     * @param event an event name.
     * @return a reference to this object.
     */
    public boolean hasListeners(String event) {
        ConcurrentLinkedQueue<Listener> callbacks = this.callbacks.get(event);
        return callbacks != null && !callbacks.isEmpty();
    }

    /**
     * Private class for listening to an event only once
     */
    private class OnceListener extends Listener {

        public final String event;
        public final Listener fn;

        public OnceListener(String event, Listener fn) {
            this.event = event;
            this.fn = fn;
        }

        @Override
        public void onResponse(Request req) {
            Emitter.this.off(this.event, this);
            this.fn.onResponse(req);
        }
    }
}
