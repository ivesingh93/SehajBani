package com.sehaj.bani.rest.service;

import com.sehaj.bani.rest.model.raagi.RaagiInfo;
import com.sehaj.bani.rest.model.raagi.Shabad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by ivesingh on 1/8/18.
 */

public interface RaagiService {

    @GET("raagiRoutes/raagi_info")
    Call<List<RaagiInfo>> raagi_info();

    @GET("raagiRoutes/raagis/{raagi_name}/shabads")
    Call<List<Shabad>> raagi_shabads(@Path("raagi_name") String raagi_name);
}
