package com.upactivity.moneo.api;

import com.upactivity.moneo.api.response.MoneoBalance;
import com.upactivity.moneo.api.response.MoneoLogin;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Moneo API endpoints
 */
interface MoneoApi {
    @GET("/app/ws/wsClasses.php?q=loginCardH&callback=")
    Call<MoneoLogin> login(@Query("login") String login, @Query("pwd") String password, @Query("_dc") long millis);

    @GET("/app/ws/wsClasses.php?q=getBalanceCardH&callback=")
    Call<MoneoBalance> getBalance(@Query("login") String login, @Query("pwd") String password, @Query("_dc") long millis);
}
