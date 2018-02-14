package com.sehaj.bani.navigation.home.raagi_detail.model;

import android.app.Activity;

import com.sehaj.bani.navigation.home.raagi_detail.adapter.ShabadAdapter;
import com.sehaj.bani.rest.instance.RetrofitClient;
import com.sehaj.bani.rest.model.raagi.Shabad;
import com.sehaj.bani.rest.service.RaagiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by ivesingh on 2/3/18.
 */

public class RaagiInteractorImpl implements RaagiInteractor {

    private ArrayList<Shabad> shabadList;
    private RaagiService raagiService;
    private ShabadAdapter shabadAdapter;

    public RaagiInteractorImpl(Activity context){
        shabadList = new ArrayList<>();
        shabadAdapter = new ShabadAdapter(context, shabadList);
        raagiService = RetrofitClient.getClient().create(RaagiService.class);
    }


    @Override
    public ShabadAdapter fetchShabads(String raagi_name) {
        Call<List<Shabad>> raagiShabadsCall = raagiService.raagi_shabads(raagi_name);

        raagiShabadsCall.enqueue(new Callback<List<Shabad>>() {
            @Override
            public void onResponse(Call<List<Shabad>> call, Response<List<Shabad>> response) {
                shabadList.clear();
                for(Shabad raagiShabad: response.body()){
                    shabadList.add(raagiShabad);
                }
                shabadAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Shabad>> call, Throwable t) {
                // TODO - Failed Raagi Shabads Call
            }
        });

        return shabadAdapter;
    }
}
