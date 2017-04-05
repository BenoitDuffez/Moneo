package com.upactivity.moneo.api.response;

import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Common header for all API calls
 */
class Header {
    int resultCode;

    @Nullable
    String resultDesc;

    @Nullable
    Calendar dateTimeServer;

    int serverId;

    long transactionId;
}
