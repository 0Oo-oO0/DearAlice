package com.alva.testvoice.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.alva.testvoice.Activity.MainActivity;

@SuppressLint("NewApi")
public class NotificationMonitorService extends NotificationListenerService {

    // 在收到消息时触发
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        if (notificationPkg.equals("com.tencent.mm") || notificationPkg.equals("com.tencent.mobileqq")) {
            // 获取接收消息的抬头
            String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
            // 获取接收消息的内容
            String notificationText = extras.getString(Notification.EXTRA_TEXT);
            if (notificationPkg.equals("com.tencent.mobileqq")) {
                MainActivity.textToSpeech.speak(notificationTitle, TextToSpeech.QUEUE_ADD, null);
                MainActivity.textToSpeech.speak(notificationText, TextToSpeech.QUEUE_ADD, null);
            } else {
                if (notificationText.length() > 5 + notificationTitle.length()) {
                    if (('条' == notificationText.charAt(2) || '条' == notificationText.charAt(3))) {
                        MainActivity.textToSpeech.speak(notificationTitle, TextToSpeech.QUEUE_ADD, null);
                        MainActivity.textToSpeech.speak(notificationText.substring(5 + notificationTitle.length()), TextToSpeech.QUEUE_ADD, null);
                    }
                    else {
                        MainActivity.textToSpeech.speak(notificationTitle, TextToSpeech.QUEUE_ADD, null);
                        MainActivity.textToSpeech.speak(notificationText, TextToSpeech.QUEUE_ADD, null);
                    }
                }else {
                    MainActivity.textToSpeech.speak(notificationTitle, TextToSpeech.QUEUE_ADD, null);
                    MainActivity.textToSpeech.speak(notificationText, TextToSpeech.QUEUE_ADD, null);
                }
            }
        }
    }

    // 在删除消息时触发
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // TODO Auto-generated method stub
        Bundle extras = sbn.getNotification().extras;
        // 获取接收消息APP的包名
        String notificationPkg = sbn.getPackageName();
        // 获取接收消息的抬头
        String notificationTitle = extras.getString(Notification.EXTRA_TITLE);
        // 获取接收消息的内容
        String notificationText = extras.getString(Notification.EXTRA_TEXT);
        Log.i("XSL_Test", "Notification removed " + notificationTitle + " & " + notificationText);
    }
}