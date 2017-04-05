package com.upactivity.moneo.api.response;

import android.support.annotation.Nullable;

/**
 * Full response for balance API call
 */
public class MoneoBalance {
    @Nullable
    Header respHeader;

    @Nullable
    private BalanceBody respBody;

    @Nullable
    public Double getRemainingBalance() {
        return respBody == null || respBody.accountBalanceStruct == null
                ? null
                : respBody.accountBalanceStruct.dayLimitPayment / 100.;
    }

    @Nullable
    public Double getTotalBalance() {
        return respBody == null || respBody.accountBalanceStruct == null
                ? null
                : respBody.accountBalanceStruct.availableBalance / 100.;
    }
}
