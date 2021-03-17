package com.app.cartoons.anime.newappcartoons.app;

import android.app.Application;

import androidx.multidex.MultiDexApplication;

import com.onesignal.OneSignal;

public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
}
