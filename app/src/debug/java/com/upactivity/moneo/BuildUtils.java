package com.upactivity.moneo;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Used to setup debug/release build variations
 */
public class BuildUtils {
    /**
     * Add a logging interceptor
     */
    public static void setupHttpLogging(OkHttpClient.Builder clientBuilder) {
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            clientBuilder.addInterceptor(logging);
        }
    }
}
