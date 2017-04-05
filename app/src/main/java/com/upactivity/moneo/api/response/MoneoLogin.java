package com.upactivity.moneo.api.response;

import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Full response for balance API call
 */
public class MoneoLogin {
    @Nullable
    Header respHeader;

    @Nullable
    private LoginBody respBody;

    @Nullable
    public String token() {
        return respBody == null ? null : respBody.token;
    }

    public boolean failed() {
        return TextUtils.isEmpty(token()) || respHeader == null || respHeader.resultCode != 0;
    }
}
