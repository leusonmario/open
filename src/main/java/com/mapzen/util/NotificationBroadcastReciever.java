package com.mapzen.util;


import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.mapzen.activity.BaseActivity;

public class NotificationBroadcastReciever extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        if (intent.getBooleanExtra(MapzenNotificationCreator.exitNavigation, false)) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Activity.NOTIFICATION_SERVICE);
            nm.cancel(0);
            Intent exitRouting = new Intent();
            exitRouting.setClass(context, BaseActivity.class);
            exitRouting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(exitRouting);
        }
    }
}

