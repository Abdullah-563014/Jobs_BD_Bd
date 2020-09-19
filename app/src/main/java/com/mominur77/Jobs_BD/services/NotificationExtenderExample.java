package com.mominur77.Jobs_BD.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.media.AudioAttributesCompat;

import com.mominur77.Jobs_BD.R;
import com.mominur77.Jobs_BD.Utils;
import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

public class NotificationExtenderExample extends NotificationExtenderService {

    //Non Govment=====OS_c1a70a68-4501-4804-a727-7e161abb4962
    //Govment=====OS_8f0b4779-a4a3-4044-9d56-c6f65cd31771
    // Silent=====OS_0e4c09fb-81df-4cf4-834c-b7b25b4c3199
    // Breaking News=====OS_40f7e8a5-8a6f-4277-9093-c396e29777ce


    NotificationChannel notificationChannel;
    String body = "message body";


    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult notification) {
        final OverrideSettings overrideSettings = new OverrideSettings();
        String sound = notification.payload.sound;
        String url = notification.payload.launchURL;
        body = notification.payload.body;
        final String type = notification.payload.additionalData.optString("type", null);

        overrideSettings.extender = new NotificationCompat.Extender() {

            @Override
            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
                if (!Utils.retrieveBooleanFromStorage(getApplicationContext(), "NotificationSoundJobStatus")) {
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= 26) {
                        if (notificationManager != null) {
                            builder.setChannelId("OS_0e4c09fb-81df-4cf4-834c-b7b25b4c3199");
                        }
                    } else {
                        builder.setSound(null);
                    }
                }
                builder.setGroup("custom_group");
                builder.setShowWhen(true);
                return builder;
            }
        };


        if (type != null && type.equalsIgnoreCase("Govment")) {
            if (Utils.retrieveBooleanFromStorage(getApplicationContext(), "GovernmentJobStatus")) {
                OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
                return true;
            } else {
                return true;
            }
        }

        if (type != null && type.equalsIgnoreCase("Non Govment")) {
            if (Utils.retrieveBooleanFromStorage(getApplicationContext(), "NonGovernmentJobStatus")) {
                OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
                return true;
            } else {
                return true;
            }
        }

//        if (type != null && type.equalsIgnoreCase("Breaking News")) {
//            if (Utils.retrieveBooleanFromStorage(getApplicationContext(), "BreakingNewsStatus")) {
//                OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
//                return true;
//            } else {
//                return true;
//            }
//        }
        return false;

    }
}

