package com.sehaj.bani.navigation.home.view;

import android.view.View;

import com.sehaj.bani.navigation.home.adapter.RaagiInfoAdapter;

/**
 * Created by ivesingh on 2/2/18.
 */

public interface HomeView {

    void init(View view);

    void showRaagis(RaagiInfoAdapter raagiInfoAdapter);

}
