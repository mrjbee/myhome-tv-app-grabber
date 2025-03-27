package org.monroe.team.myhometvservice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

public class PowerStateReceiver extends BroadcastReceiver {

    public static AtomicBoolean screenOn = new AtomicBoolean(true);

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.i("PowerStateReceiver", "Device is in suspend mode (Screen OFF)");
            screenOn.set(false);
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.i("PowerStateReceiver", "Device is awake (Screen ON)");
            screenOn.set(true);
        }
    }

}
