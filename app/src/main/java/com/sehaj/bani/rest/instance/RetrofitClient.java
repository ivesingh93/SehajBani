package com.sehaj.bani.rest.instance;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ivesingh on 1/3/18.
 */

public class RetrofitClient {
    private static Retrofit retrofit = null;
    public static final String BASE_URL = "http://ec2-18-218-75-154.us-east-2.compute.amazonaws.com:4200/api/";
    public static Retrofit getClient(){

        if(retrofit == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }
}
