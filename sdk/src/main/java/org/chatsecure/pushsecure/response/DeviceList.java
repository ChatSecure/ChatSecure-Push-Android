package org.chatsecure.pushsecure.response;

import android.support.annotation.NonNull;

/**
 * Created by davidbrodsky on 6/23/15.
 */
public class DeviceList extends List {

    public final @NonNull
    java.util.List<Device> results;

    protected DeviceList(int count, @NonNull String next, @NonNull String previous, @NonNull java.util.List<Device> devices) {
        super(count, next, previous);
        this.results = devices;
    }
}
