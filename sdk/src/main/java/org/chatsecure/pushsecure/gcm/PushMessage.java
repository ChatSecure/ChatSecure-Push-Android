package org.chatsecure.pushsecure.gcm;

import android.support.annotation.NonNull;

/**
 * A ChatSecure Push push message
 * Created by dbro on 7/29/15.
 */
public class PushMessage {

    public final @NonNull String token;
    public final @NonNull String payload;

    public PushMessage(@NonNull String token, @NonNull String payload) {
        this.token = token;
        this.payload = payload;
    }

}
