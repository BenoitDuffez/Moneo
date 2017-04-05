package com.upactivity.moneo.api.response;

import android.support.annotation.Nullable;

import java.util.Calendar;

/**
 * Body part for the balance API call
 */
class BalanceBody {
    int cardHolderId;

    @Nullable
    AccountBalance accountBalanceStruct;

    static class AccountBalance {
        String accountNumber;

        int productId;

        @Nullable
        Calendar expirationDate;

        int vf;

        int availableBalance;

        int ledgerBalance;

        @Nullable
        Calendar timestampBO;

        int lastMovement;

        int dayLimitPayment;
    }
}
