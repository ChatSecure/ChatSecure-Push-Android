package org.chatsecure.pushsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class PushToken {

    public final @Nullable String url;
    public final @Nullable String name;
    public final @NonNull String token;
    public final @Nullable String apnsDevice;
    public final @Nullable String gcmDevice;

    public PushToken(@Nullable String url,
                     @Nullable String name,
                     @NonNull String token,
                     @Nullable String apnsDevice,
                     @Nullable String gcmDevice) {

        this.url = url;
        this.name = name;
        this.token = token;
        this.apnsDevice = apnsDevice;
        this.gcmDevice = gcmDevice;
    }
}
