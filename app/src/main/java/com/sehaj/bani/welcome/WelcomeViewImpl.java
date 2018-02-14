package com.sehaj.bani.welcome;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.sehaj.bani.R;
import com.sehaj.bani.welcome.login.LoginActivity;
import com.sehaj.bani.welcome.signup.SignupActivity;

/**
 * Created by ivesingh on 2/1/18.
 */

public class WelcomeViewImpl implements WelcomeView, View.OnClickListener {

    private Activity context;
    private WelcomePresenter welcomePresenter;
    private Button signup_free_B, continue_with_facebook_B, connect_using_google_B, login_B;

    public WelcomeViewImpl(Activity context){
        this.context = context;
        welcomePresenter = new WelcomePresenterImpl(this);
    }

    @Override
    public void init() {
        signup_free_B = context.findViewById(R.id.signup_free_B);
        continue_with_facebook_B = context.findViewById(R.id.continue_with_facebook_B);
        connect_using_google_B = context.findViewById(R.id.connect_using_google_B);
        login_B = context.findViewById(R.id.login_B);

        signup_free_B.setOnClickListener(this);
        continue_with_facebook_B.setOnClickListener(this);
        connect_using_google_B.setOnClickListener(this);
        login_B.setOnClickListener(this);
    }

    @Override
    public void navigateToRegistration() {

        Intent intent = new Intent(context, SignupActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void navigateToLoginPage() {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.signup_free_B:
                navigateToRegistration();
                break;

            case R.id.continue_with_facebook_B:
                welcomePresenter.continueWithFacebook();
                break;

            case R.id.connect_using_google_B:
                welcomePresenter.connectUsingGoogle();
                break;

            case R.id.login_B:
                navigateToLoginPage();
                break;
        }
    }
}
