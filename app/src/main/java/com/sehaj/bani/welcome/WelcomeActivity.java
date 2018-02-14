package com.sehaj.bani.welcome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sehaj.bani.R;

public class WelcomeActivity extends AppCompatActivity {

    private WelcomeViewImpl welcomeViewImpl;
    private WelcomePresenterImpl welcomePresenterImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        welcomeViewImpl = new WelcomeViewImpl(this);
        welcomePresenterImpl = new WelcomePresenterImpl(welcomeViewImpl);
        welcomePresenterImpl.init();
    }
}
