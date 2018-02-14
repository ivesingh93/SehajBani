package com.sehaj.bani.navigation.home.model;

import android.app.Activity;

import com.sehaj.bani.navigation.home.adapter.RaagiInfoAdapter;
import com.sehaj.bani.rest.instance.RetrofitClient;
import com.sehaj.bani.rest.model.raagi.RaagiInfo;
import com.sehaj.bani.rest.service.RaagiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ivesingh on 2/2/18.
 */

public class HomeInteractorImpl implements HomeInteractor  {

    private List<RaagiInfo> raagiInfoList;
    private RaagiService raagiService;
    private RaagiInfoAdapter raagiInfoAdapter;

    public HomeInteractorImpl(Activity context){
        raagiInfoList = new ArrayList<>();
        raagiInfoAdapter = new RaagiInfoAdapter(context, raagiInfoList);
        raagiService = RetrofitClient.getClient().create(RaagiService.class);
    }

    @Override
    public RaagiInfoAdapter fetchRaagis() {
        Call<List<RaagiInfo>> raagiInfoCall = raagiService.raagi_info();

        raagiInfoCall.enqueue(new Callback<List<RaagiInfo>>() {
            @Override
            public void onResponse(Call<List<RaagiInfo>> call, Response<List<RaagiInfo>> response) {
                raagiInfoList.clear();
                for(RaagiInfo raagiInfo: response.body()){
                    raagiInfoList.add(raagiInfo);
                }
                raagiInfoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<RaagiInfo>> call, Throwable t) {
                // TODO - Failed Raagi Info Call
            }
        });

        return raagiInfoAdapter;
    }


}
