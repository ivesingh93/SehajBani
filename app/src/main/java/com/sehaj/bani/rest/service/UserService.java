package com.sehaj.bani.rest.service;

import com.sehaj.bani.rest.model.user.UserCredentials;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by ivesingh on 1/3/18.
 */

public interface UserService {

    @POST("userRoutes/signup")
    Call<UserCredentials> create_user(@Body UserCredentials userCredentials);

    @GET("userRoutes/accounts/{account_id}")
    Call<UserCredentials> has_account(@Path("account_id") String account_id);

    @GET("userRoutes/usernames/{username}")
    Call<UserCredentials> has_username(@Path("username") String username);

    @POST("userRoutes/authenticate")
    Call<UserCredentials> login(@Body UserCredentials userCredentials);

}
