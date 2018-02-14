package com.sehaj.bani.navigation.home.presenter;

import android.app.Activity;
import android.view.View;

import com.sehaj.bani.navigation.home.model.HomeInteractor;
import com.sehaj.bani.navigation.home.model.HomeInteractorImpl;
import com.sehaj.bani.navigation.home.adapter.RaagiInfoAdapter;
import com.sehaj.bani.navigation.home.view.HomeView;

/**
 * Created by ivesingh on 2/2/18.
 */

public class HomePresenterImpl implements HomePresenter{

    private HomeView homeView;
    private HomeInteractor homeInteractor;

    public HomePresenterImpl(HomeView homeView, Activity context){
        this.homeView = homeView;
        homeInteractor = new HomeInteractorImpl(context);
    }

    @Override
    public void init(View view) {
        homeView.init(view);
    }

    @Override
    public void prepareRaagis() {
        RaagiInfoAdapter raagiInfoAdapter = homeInteractor.fetchRaagis();
        homeView.showRaagis(raagiInfoAdapter);
    }
}
