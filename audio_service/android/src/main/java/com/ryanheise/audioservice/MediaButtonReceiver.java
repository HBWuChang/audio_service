package com.ryanheise.audioservice;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

public class MediaButtonReceiver extends androidx.media.session.MediaButtonReceiver {
    public static final String ACTION_NOTIFICATION_DELETE = "com.ryanheise.audioservice.intent.action.ACTION_NOTIFICATION_DELETE";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null
                && ACTION_NOTIFICATION_DELETE.equals(intent.getAction())
                && AudioService.instance != null) {
            AudioService.instance.handleDeleteNotification();
            return;
        }
        // Notification action buttons are broadcast to this receiver. When the
        // service is already running, dispatch the key event straight to the
        // media session callback instead of calling super.onReceive(), which
        // routes it through MediaController.dispatchMediaButtonEvent(). On
        // Android 14+ that call silently drops KEYCODE_MUTE (the synthetic
        // keycode used by the notification's play action) because it is no
        // longer listed in KeyEvent.isMediaSessionKey(), so the play button
        // would otherwise do nothing.
        if (intent != null
                && Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())
                && AudioService.instance != null) {
            @SuppressWarnings("deprecation")
            final KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (event != null && AudioService.instance.handleMediaButtonEvent(event)) {
                return;
            }
        }
        super.onReceive(context, intent);
    }
}
