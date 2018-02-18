package com.sehaj.bani.player.service;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;

import com.sehaj.bani.player.service.NotificationManager;

/**
 * Created by DELL on 1/29/2018.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {
    NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(this);
        notificationManager=new NotificationManager(getApplicationContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createMainNotificationChannel();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
