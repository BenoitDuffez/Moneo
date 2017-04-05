package com.upactivity.moneo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Define a Service that returns an IBinder for the syncadapter adapter class, allowing the syncadapter adapter framework to call
 * onPerformSync().
 */
public class SyncService extends Service {
    // Object to use as a thread-safe lock
    private static final Object sSyncAdapterLock = new Object();

    // Storage for an instance of the syncadapter adapter
    private static SyncAdapter sSyncAdapter = null;

    /**
     * Instantiate the syncadapter adapter object.
     */
    @Override
    public void onCreate() {
        // Create the syncadapter adapter as a singleton. Set the syncadapter adapter as syncable Disallow parallel syncs
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke
     * the syncadapter adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
        // Get the object that allows external processes to call onPerformSync(). The object is created
        // in the base class code when the SyncAdapter constructors call super()
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
