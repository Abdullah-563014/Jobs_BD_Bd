package com.mominur77.Jobs_BD;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.crashlytics.android.Crashlytics;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import io.fabric.sdk.android.Fabric;

import static com.mopub.common.logging.MoPubLog.LogLevel.DEBUG;
import static com.mopub.common.logging.MoPubLog.LogLevel.NONE;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        String platform=Utils.retrieveStringFromStorage(this,Constants.platformNameKey);
        if (platform==null || platform.isEmpty()) {
            platform="No Ads";
        }
        Constants.platformName=platform;




        Fabric.with(this, new Crashlytics());

        FirebaseAnalytics.getInstance(this);

        AudienceNetworkAds.initialize(this);

        AdSettings.setDebugBuild(Constants.facebookDebugBuild);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });


        OneSignal.startInit(this)
                .setNotificationReceivedHandler(new ExampleNotificationReceivedHandler())
                .setNotificationOpenedHandler(new ExampleNotificationOpenedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


    }


    private class ExampleNotificationReceivedHandler implements OneSignal.NotificationReceivedHandler {

        @Override

        public void notificationReceived(OSNotification notification) {

        }

    }

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            String launchUrl = result.notification.payload.launchURL; // update docs launchUrl
            Intent intent = new Intent(getApplicationContext(), ContinueActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Url", launchUrl);
            startActivity(intent);
        }

    }
}
