package org.chatsecure.pushsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import timber.log.Timber;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class Device {

    public enum Type { UNKNOWN, APNS, GCM }

    public final @Nullable String name;
    public final @NonNull String registrationId;
    public final @Nullable String deviceId;
    public final boolean active;
    public final @NonNull Date dateCreated;

    // Non API fields (Not present on-the-wire, but populated by api client)
    public final @Nullable Type type;

    public Device(@NonNull Device device,
                  @NonNull Type type) {

        this(device.name, device.registrationId, device.deviceId, device.active, device.dateCreated, type);
    }

    public Device(@Nullable String name,
                  @NonNull String registrationId,
                  @Nullable String deviceId,
                  boolean active,
                  @NonNull Date dateCreated) {

        this(name, registrationId, deviceId, active, dateCreated, Type.UNKNOWN);
    }

    public Device(@Nullable String name,
                  @NonNull String registrationId,
                  @Nullable String deviceId,
                  boolean active,
                  @NonNull Date dateCreated,
                  @NonNull Type type) {

        this.name = name;
        this.registrationId = registrationId;
        this.deviceId = deviceId;
        this.active = active;
        this.dateCreated = dateCreated;
        this.type = type;
    }
}
