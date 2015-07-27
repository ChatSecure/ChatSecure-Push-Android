package org.chatsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class Message {

    public final @NonNull String token;
    public final @Nullable String data;

    public Message(@Nullable String data, @NonNull String token) {
        this.data = data;
        this.token = token;
    }
}
