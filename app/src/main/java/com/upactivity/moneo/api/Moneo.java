package com.upactivity.moneo.api;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.upactivity.moneo.BuildUtils;
import com.upactivity.moneo.api.response.MoneoBalance;
import com.upactivity.moneo.api.response.MoneoLogin;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Wrapper around retrofit
 */
public class Moneo {
    public static final String MONEO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    private static final String BASE_URL = "http://www.moneo-resto.fr";

    private static MoneoApi mApiCache = null;

    /**
     * Create a retrofit object with the necessary interceptors
     *
     * @return retrofit object to process API calls
     */
    @NonNull
    private static MoneoApi getMoneoApi() {
        if (mApiCache == null) {
            final OkHttpClient.Builder clientBuilder;

            clientBuilder = new OkHttpClient.Builder();

            clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
            clientBuilder.readTimeout(30, TimeUnit.SECONDS);
            clientBuilder.writeTimeout(0, TimeUnit.SECONDS); // https://github.com/square/retrofit/issues/931

            BuildUtils.setupHttpLogging(clientBuilder);

            // Create it
            Gson gson = new GsonBuilder()
                    .setDateFormat(MONEO_DATE_FORMAT)
                    .registerTypeAdapter(Calendar.class, new CalendarTypeAdapter())
                    .create();

            final Retrofit.Builder builder = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(new GsonPConverterFactory(gson))

                    .client(clientBuilder.build());

            mApiCache = builder.build().create(MoneoApi.class);
        }
        return mApiCache;
    }

    /**
     * Generic method to execute an API call, while handling errors and logging properly
     *
     * @param call API call
     * @param <T>  Type of result
     * @return The result of the API call, if successful; null otherwise
     */
    @Nullable
    private static <T> T executeApiCall(@NonNull Call<T> call) {
        try {
            final Response<T> execute = call.execute();
            ResponseBody error = execute.errorBody();
            if (error != null) {
                Log.e("apicall", "Couldn't execute API call " + call + " (" + call.request() + "), error: " + error.string());
            }
            Log.d("apicall", "error?: " + (error == null ? "none" : error.string()));
            return execute.body();
        } catch (IOException e) {
            Log.e("apicall", "Couldn't execute API call " + call + " (" + call.request() + ")", e);
        }
        return null;
    }

    @Nullable
    public static MoneoLogin login(String username, String password) {
        return executeApiCall(getMoneoApi().login(username, password, new GregorianCalendar().getTimeInMillis()));
    }

    @Nullable
    public static MoneoBalance getBalance(String username, String password) {
        return executeApiCall(getMoneoApi().getBalance(username, password, new GregorianCalendar().getTimeInMillis()));
    }
}
