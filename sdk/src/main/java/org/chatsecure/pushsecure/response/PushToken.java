package org.chatsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class PushToken {

    public final @Nullable String name;
    public final @NonNull String token;
    public final @Nullable String apnsDevice;
    public final @Nullable String gcmDevice;

    public PushToken(@NonNull String token) {
        this(token, null, null, null);
    }

    public PushToken(@NonNull String token,
                     @Nullable String name,
                     @Nullable String apnsDevice,
                     @Nullable String gcmDevice) {

        this.name = name;
        this.token = token;
        this.apnsDevice = apnsDevice;
        this.gcmDevice = gcmDevice;
    }

    public @Nullable String getDeviceIdentifier() {
        return apnsDevice != null ? apnsDevice : gcmDevice;
    }
}
