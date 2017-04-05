package com.upactivity.moneo;

import android.app.Application;

import okhttp3.OkHttpClient;

/**
 * Used to setup debug/release build variations
 */
public class BuildUtils {
    /**
     * Add a logging interceptor
     */
    public static void setupHttpLogging(OkHttpClient.Builder clientBuilder) {
    }
}
