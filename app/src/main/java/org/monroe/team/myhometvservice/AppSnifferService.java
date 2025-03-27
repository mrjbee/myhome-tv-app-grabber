package org.monroe.team.myhometvservice;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.io.IOException;

public class AppSnifferService extends AccessibilityService {
    private MyHttpService httpService;
    private BroadcastReceiver powerStateReceiver;
    private long lastUpdatedAt = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i("ActiveApp", "Current event: " + event.getEventType());
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            String packageName = event.getPackageName().toString();
            //if ///(packageName.equals("com.google.android.tv.launcher") || packageName.equals("com.google.android.apps.tv.launcherx") ||
            if (packageName.startsWith("com.google.android.inputmethod")) {
                return; // Skip launcher events
            }
            if (System.currentTimeMillis() - lastUpdatedAt > 1000) {
                Log.i("ActiveApp", "Current app: " + packageName);
                httpService.updateCurrentApp(packageName);
                lastUpdatedAt = System.currentTimeMillis();
            } else {
                Log.i("ActiveApp", "Skip current app: " + packageName);
            }
        }
    }

    @Override
    public void onInterrupt() {
        Log.i("ActiveApp", "Watcher interrupted");
        if (httpService != null) {
            httpService.stop();
        }
        if (powerStateReceiver != null) {
            unregisterReceiver(powerStateReceiver);
        }
    }

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        // pass the typeof events you want your service to listen to
        // other will not be handledby this service
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED |
                AccessibilityEvent.TYPE_VIEW_FOCUSED | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOWS_CHANGED;


        // Set the type of feedback your service will provide.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        // the notification timeout is the time interval after which the service would
        // listen from the system. Anything happening between that interval won't be
        // captured by the service
        info.notificationTimeout = 100;

        // finally set the serviceInfo
        this.setServiceInfo(info);

        // Start the HTTP server when the service connects
        httpService = new MyHttpService();
        try {
            httpService.start();
        } catch (IOException e) {
            Log.w("Could not start service", e);
        }

        powerStateReceiver = new PowerStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(powerStateReceiver, filter);
    }
}
