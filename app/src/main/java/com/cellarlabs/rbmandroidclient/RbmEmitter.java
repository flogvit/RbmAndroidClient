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
public class RbmEmitter {

    private ConcurrentMap<String, ConcurrentLinkedQueue<RbmListener>> callbacks
            = new ConcurrentHashMap<String, ConcurrentLinkedQueue<RbmListener>>();

    /**
     * Listens on the event.
     *
     * @param event event name.
     * @param fn
     * @return a reference to this object.
     */
    public RbmEmitter on(String event, RbmListener fn) {
        ConcurrentLinkedQueue<RbmListener> callbacks = this.callbacks.get(event);
        if (callbacks == null) {
            callbacks = new ConcurrentLinkedQueue<RbmListener>();
            ConcurrentLinkedQueue<RbmListener> _callbacks = this.callbacks.putIfAbsent(event, callbacks);
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
    public RbmEmitter once(final String event, final RbmListener fn) {
        this.on(event, new OnceRbmListener(event, fn));
        return this;
    }

    /**
     * Removes all registered listeners.
     *
     * @return a reference to this object.
     */
    public RbmEmitter off() {
        this.callbacks.clear();
        return this;
    }

    /**
     * Removes all listeners of the specified event.
     *
     * @param event an event name.
     * @return a reference to this object.
     */
    public RbmEmitter off(String event) {
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
    public RbmEmitter off(String event, RbmListener fn) {
        ConcurrentLinkedQueue<RbmListener> callbacks = this.callbacks.get(event);
        if (callbacks != null) {
            Iterator<RbmListener> it = callbacks.iterator();
            while (it.hasNext()) {
                RbmListener internal = it.next();
                if (RbmEmitter.sameAs(fn, internal)) {
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
    public RbmEmitter off(RbmListener fn) {
        for (String event : this.callbacks.keySet()) {
            off(event, fn);
        }
        return this;
    }

    private static boolean sameAs(RbmListener fn, RbmListener internal) {
        if (fn.equals(internal)) {
            return true;
        } else if (internal instanceof OnceRbmListener) {
            return fn.equals(((OnceRbmListener) internal).fn);
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
    public RbmEmitter emit(String event, RbmRequest req) {
        ConcurrentLinkedQueue<RbmListener> callbacks = this.callbacks.get(event);
        if (callbacks != null) {
            for (RbmListener fn : callbacks) {
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
    public List<RbmListener> listeners(String event) {
        ConcurrentLinkedQueue<RbmListener> callbacks = this.callbacks.get(event);
        return callbacks != null ?
                new ArrayList<RbmListener>(callbacks) : new ArrayList<RbmListener>(0);
    }

    /**
     * Check if this emitter has listeners for the specified event.
     *
     * @param event an event name.
     * @return a reference to this object.
     */
    public boolean hasListeners(String event) {
        ConcurrentLinkedQueue<RbmListener> callbacks = this.callbacks.get(event);
        return callbacks != null && !callbacks.isEmpty();
    }

    /**
     * Private class for listening to an event only once
     */
    private class OnceRbmListener extends RbmListener {

        public final String event;
        public final RbmListener fn;

        public OnceRbmListener(String event, RbmListener fn) {
            this.event = event;
            this.fn = fn;
        }

        @Override
        public void onResponse(RbmRequest req) {
            RbmEmitter.this.off(this.event, this);
            this.fn.onResponse(req);
        }
    }
}
