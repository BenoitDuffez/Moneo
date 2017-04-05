package com.upactivity.moneo;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.upactivity.moneo.api.Moneo;
import com.upactivity.moneo.api.response.MoneoBalance;
import com.upactivity.moneo.sync.AuthenticatorActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    public static final String ACCOUNT_TYPE = "com.upactivity.moneo";

    public static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.main_todays_balance)
    TextView mTodaysBalance;

    @BindView(R.id.main_total_balance)
    TextView mTotalBalance;

    /**
     * Callback for the get token account manager call
     * Called on a background thread
     */
    private final AccountManagerCallback<Bundle> callback = new AccountManagerCallback<Bundle>() {
        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                final Bundle result = future.getResult(30, TimeUnit.SECONDS);
                if (result == null
                        || !result.containsKey(AccountManager.KEY_ACCOUNT_NAME)
                        || !result.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                    Log.e(TAG, "Unable to retrieve auth token");
                } else {
                    final String account = result.getString(AccountManager.KEY_ACCOUNT_NAME);
                    final String token = result.getString(AccountManager.KEY_AUTHTOKEN);
                    final MoneoBalance balance = Moneo.getBalance(account, token);

                    if (balance == null) {
                        Log.e(TAG, "Unable to retrieve the balance: invalidate auth token");
                        AccountManager.get(MainActivity.this).invalidateAuthToken(ACCOUNT_TYPE, token);
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateData(balance);
                            }
                        });
                    }
                }
            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                Log.e(TAG, "Unable to get auth token");
                e.printStackTrace();
            }
        }
    };

    /**
     * Create a background thread because the balance data will be downloaded (no network on main thread)
     */
    HandlerThread thread = new HandlerThread("moneo_bg");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        thread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Account[] accounts = AccountManager.get(this).getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length == 0) {
            final Intent intent = new Intent(this, AuthenticatorActivity.class);
            intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, ACCOUNT_TYPE);
            intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            startActivity(intent);
        } else {
            updateData(accounts[0]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        thread.quitSafely();
        thread = null;
    }

    /**
     * Will retrieve the auth token for that account, and then download the latest balance information.
     * {@link #updateData(MoneoBalance)} will be called when the data is downloaded
     *
     * @param account Target account
     */
    private void updateData(Account account) {
        final AccountManager am = AccountManager.get(this);
        am.getAuthToken(account, AuthenticatorActivity.AUTH_TOKEN_TYPE, null, this, callback, new Handler(thread.getLooper()));
    }

    /**
     * Called after the balance has been downloaded
     *
     * @param balance New balance information
     */
    private void updateData(@NonNull MoneoBalance balance) {
        final Double today = balance.getRemainingBalance();
        if (today == null) {
            mTodaysBalance.setText(R.string.main_balance_na);
        } else {
            mTodaysBalance.setText(getString(R.string.main_money_format, today));
        }

        final Double total = balance.getTotalBalance();
        if (total == null) {
            mTotalBalance.setText(R.string.main_balance_na);
        } else {
            mTotalBalance.setText(getString(R.string.main_money_format, total));
        }
    }
}
