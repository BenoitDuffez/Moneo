package com.upactivity.moneo.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Authenticator service
 * https://developer.android.com/training/sync-adapters/creating-authenticator.html#CreateAuthenticatorService
 */
public class AuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private AccountAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new AccountAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
