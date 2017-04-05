package com.upactivity.moneo.sync;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.upactivity.moneo.R;
import com.upactivity.moneo.api.Moneo;
import com.upactivity.moneo.api.response.MoneoLogin;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Ask for server credentials
 */
public class AuthenticatorActivity extends AccountAuthenticatorActivity {
    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";

    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";

    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";

    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public static final String AUTH_TOKEN_TYPE = "Moneo";

    private static final String TAG = AuthenticatorActivity.class.getSimpleName();

    @BindView(R.id.login_account_id)
    EditText mAccountId;

    @BindView(R.id.login_pin)
    EditText mPin;

    @BindView(R.id.login_submit)
    Button mSubmitButton;

    private AccountManager mAccountManager;

    private String mAuthTokenType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null) {
            mAuthTokenType = AUTH_TOKEN_TYPE;
        }

        if (accountName != null) {
            mAccountId.setText(accountName);
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    public void submit() {
        final String accountId = mAccountId.getText().toString();
        final String pin = mPin.getText().toString();

        final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>() {
            @Override
            protected Intent doInBackground(String... params) {
                Log.d(TAG, "> Started authenticating");

                Bundle data = new Bundle();
                try {
                    MoneoLogin login = Moneo.login(accountId, pin);
                    if (login == null || login.failed()) {
                        data.putString(KEY_ERROR_MESSAGE, "Unable to authenticate");
                    } else {
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, accountId);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);

                        // if Moneo knew how to make APIs we would use the token instead of the plain password...
                        data.putString(AccountManager.KEY_AUTHTOKEN, /*login.token()*/ pin);
                    }
                } catch (Exception e) {
                    data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    Log.e(TAG, "Unable to get self information", e);
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }

    private void finishLogin(Intent intent) {
        Log.d(TAG, "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(TAG, "> finishLogin > addAccountExplicitly");
            String authTokenType = mAuthTokenType;

            // Save TODO in db
//            Server server = new Server();
//            server.setApiKey(authToken);
//            server.setUrl(accountName);
//            serverBox.put(server);

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, authToken, null);
            mAccountManager.setAuthToken(account, authTokenType, authToken);
        } else {
            Log.d(TAG, "> finishLogin > setPassword");
            mAccountManager.setPassword(account, authToken);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
}
