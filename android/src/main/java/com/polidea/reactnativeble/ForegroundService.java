package com.polidea.reactnativeble;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import static com.polidea.reactnativeble.Constants.NOTIFICATION_CONFIG;

public class ForegroundService extends Service {

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    String action = intent.getAction();
    if (action != null) {
      if (action.equals(Constants.ACTION_FOREGROUND_SERVICE_START)) {
        if (intent.getExtras() != null && intent.getExtras().containsKey(NOTIFICATION_CONFIG)) {

          Bundle notificationConfig = intent.getExtras().getBundle(NOTIFICATION_CONFIG);

          Log.w("CONFIG ->", String.valueOf((notificationConfig.toString())));

          if (notificationConfig != null && notificationConfig.containsKey("id")) {
            Notification notification = NotificationHelper.getInstance(getApplicationContext())
              .buildNotification(getApplicationContext(), notificationConfig);

            int id = (int)notificationConfig.getDouble("id");
            Log.w("id ->", String.valueOf(id));

            Log.e("notification", String.valueOf(notification));

            startForeground(id, notification);
          }
        }

      } else if (action.equals(Constants.ACTION_FOREGROUND_SERVICE_STOP)) {
        stopSelf();
      }
    }
    return START_NOT_STICKY;

  }
}
