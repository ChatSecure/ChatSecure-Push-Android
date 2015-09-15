package org.chatsecure.pushsecure.gcm;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import timber.log.Timber;

/**
 * Created by dbro on 7/29/15.
 */
public class PushParser {

    private Gson gson = new Gson();

    /**
     * Handle a push message received from GcmListenerService#parseBundle that
     * might originate from ChatSecure Push.
     * @param from the from identifier as reported by GCM
     * @param data the payload as reported by GCM
     * @return a {@link PushMessage} or null if this message is not a ChatSecure Push message
     */
    public @Nullable PushMessage parseBundle(String from, Bundle data) {
        try {
            String message = data.getString("message");
            JsonObject payload = gson.fromJson(message, JsonObject.class);
            payload = payload.get("message").getAsJsonObject();
            String token = payload.get("token").getAsString();
            String pushPayload = payload.get("data").getAsString();
            Timber.d("Got push " + payload.get("data").getAsString());
            return new PushMessage(token, pushPayload);
        } catch (UnsupportedOperationException e) {
            return null;
        }
    }
}
