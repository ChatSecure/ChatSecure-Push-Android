package org.chatsecure.pushsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class Device {

    public final @Nullable String name;
    public final @NonNull String registrationId;
    public final @Nullable String deviceId;
    public final boolean active;
    public final @NonNull Date dateCreated;


    public Device(@Nullable String name,
                  @NonNull String registrationId,
                  @Nullable String deviceId,
                  boolean active,
                  @NonNull Date dateCreated) {

        this.name = name;
        this.registrationId = registrationId;
        this.deviceId = deviceId;
        this.active = active;
        this.dateCreated = dateCreated;
    }
}
