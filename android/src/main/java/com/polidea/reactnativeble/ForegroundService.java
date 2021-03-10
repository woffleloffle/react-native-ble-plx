package com.polidea.reactnativeble;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.polidea.multiplatformbleadapter.BleAdapter;
import com.polidea.multiplatformbleadapter.Characteristic;
import com.polidea.multiplatformbleadapter.OnErrorCallback;
import com.polidea.multiplatformbleadapter.OnEventCallback;
import com.polidea.multiplatformbleadapter.errors.BleError;
import com.polidea.reactnativeble.converter.CharacteristicToJsObjectConverter;

import static com.polidea.reactnativeble.BleClientManager.bleAdapter;
import static com.polidea.reactnativeble.Constants.NOTIFICATION_CONFIG;

public class ForegroundService extends Service {

  private final CharacteristicToJsObjectConverter characteristicConverter = new CharacteristicToJsObjectConverter();

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

          // Notification
          Bundle notificationConfig = intent.getExtras().getBundle(NOTIFICATION_CONFIG);

          if (notificationConfig != null && notificationConfig.containsKey("id")) {
            Notification notification = NotificationHelper.getInstance(getApplicationContext())
              .buildNotification(getApplicationContext(), notificationConfig);

            int id = (int) notificationConfig.getDouble("id");

            Log.e("calling...", "startForeground");
            startForeground(id, notification);



            // Bluetooth
            String deviceId = intent.getExtras().getString("deviceId");
            String serviceUUID = intent.getExtras().getString("serviceUUID");
            String characteristicUUID = intent.getExtras().getString("characteristicUUID");
            String transactionId = intent.getExtras().getString("transactionId");


            Log.e("attaching...", "monitorCharacteristicForDevice");
            bleAdapter.monitorCharacteristicForDevice(
              deviceId, serviceUUID, characteristicUUID, transactionId,
              new OnEventCallback<Characteristic>() {
                @Override
                public void onEvent(Characteristic data) {
                  WritableArray jsResult = Arguments.createArray();
                  jsResult.pushNull();
                  jsResult.pushMap(characteristicConverter.toJSObject(data));

                  Log.i("char_data", String.valueOf(jsResult));

//                  BleClientManager.sendBackgroundEvent(Event.ReadEvent, jsResult);
                }
              },
              new OnErrorCallback() {
                @Override
                public void onError(BleError error) {
//                  safePromise.reject(null, errorConverter.toJs(error));
                  Log.i("char_err", String.valueOf(error));

                }
              });
          }
        }

      } else if (action.equals(Constants.ACTION_FOREGROUND_SERVICE_STOP)) {
        stopSelf();
      }
    }
    return START_NOT_STICKY;

  }
}
