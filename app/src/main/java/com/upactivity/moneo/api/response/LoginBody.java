package com.upactivity.moneo.api.response;

import android.support.annotation.Nullable;

/**
 * Body part for the login API call
 */
class LoginBody {
    @Nullable
    String token;

    int cardHolderId;
}
