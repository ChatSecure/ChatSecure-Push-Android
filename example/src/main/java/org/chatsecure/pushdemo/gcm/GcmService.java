/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package org.chatsecure.pushdemo.gcm;

import android.app.NotificationManager;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.chatsecure.pushdemo.R;

import timber.log.Timber;


/**
 * Service used for receiving GCM messages. When a message is received this service will log it.
 */
public class GcmService extends GcmListenerService {

    public GcmService() {}

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Gson gson = new Gson();
        String message = data.getString("message");
        JsonObject payload = gson.fromJson(message, JsonObject.class);
        payload = payload.get("message").getAsJsonObject();
        String token = payload.get("token").getAsString();
        String pushPayload = payload.get("data").getAsString();
        Timber.d("Got push " + payload.get("data").getAsString());
        postNotification(pushPayload, token);
    }

    @Override
    public void onDeletedMessages() {
        postNotification("Deleted messages on server");
    }

    @Override
    public void onMessageSent(String msgId) {
        postNotification("Upstream message sent. Id=" + msgId);
    }

    @Override
    public void onSendError(String msgId, String error) {
        postNotification("Upstream message send error. Id=" + msgId + ", error" + error);
    }

    private void postNotification(String msg) {
        postNotification(msg, null);
    }

    private void postNotification(String msg, String from) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setContentTitle(msg);
        if (from != null) builder.setContentText("From " + from);
        builder.setVibrate(new long[]{250, 250});
        builder.setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(2357, builder.build());
    }
}