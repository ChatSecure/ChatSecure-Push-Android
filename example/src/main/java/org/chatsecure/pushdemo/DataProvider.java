package org.chatsecure.pushdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.chatsecure.pushsecure.response.Device;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import timber.log.Timber;

/**
 * Persists application data using {@link SharedPreferences}
 *
 * Created by davidbrodsky on 7/7/15.
 */
public class DataProvider {

    private static final String SENT_GCM_TOKEN_TO_SERVER = "sentGcmTokenToServer"; // boolean
    private static final String REGISTRATION_COMPLETE = "registrationComplete";    // boolean
    private static final String PUSHSECURE_UNAME = "psUsername";                   // String
    private static final String PUSHSECURE_TOKEN = "psToken";                      // String

    // This device storage
    private static final String GCM_TOKEN = "gcmToken";                            // String
    private static final String DEVICE_SERVER_ID = "deviceSId";                    // String
    private static final String DEVICE_ID = "deviceId";                            // String
    private static final String DEVICE_NAME = "deviceName";                        // String
    private static final String DEVICE_ACTIVE = "deviceActive";                    // boolean
    private static final String DEVICE_CREATION_DATE = "deviceCreationDate";       // String

    private static final String SHARED_PREFS_NAME = DataProvider.class.getSimpleName();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    public DataProvider(@NonNull Context context) {
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    @SuppressWarnings("ConstantConditions")
    public @Nullable Device getDevice() {
        if (sharedPrefs.getString(GCM_TOKEN, null) == null ||
            sharedPrefs.getString(DEVICE_SERVER_ID, null) == null) {

            Timber.w("The returned device is not fully registered. Returning null");
            return null;
        }

        try {
            return new Device(sharedPrefs.getString(DEVICE_NAME, null),
                              sharedPrefs.getString(GCM_TOKEN, null),
                              sharedPrefs.getString(DEVICE_ID, null),
                              sharedPrefs.getString(DEVICE_SERVER_ID, null),
                              sharedPrefs.getBoolean(DEVICE_ACTIVE, true),
                              sdf.parse(sharedPrefs.getString(DEVICE_CREATION_DATE, null)));
        } catch (ParseException e) {
            Timber.e(e, "Unable to restore device from persisted data");
            return null;
        }
    }

    public void setDevice(@NonNull Device device) {
        editor.putString(DEVICE_NAME, device.name)
              .putString(GCM_TOKEN, device.registrationId)
              .putString(DEVICE_ID, device.deviceId)
              .putString(DEVICE_SERVER_ID, device.id)
              .putBoolean(DEVICE_ACTIVE, device.active)
              .putString(DEVICE_CREATION_DATE, sdf.format(device.dateCreated)).apply();
    }

    public @Nullable String getPushSecureUsername() {
        return sharedPrefs.getString(PUSHSECURE_UNAME, null);
    }

    public void setPushSecureUsername(@NonNull String username) {
        editor.putString(PUSHSECURE_UNAME, username).apply();
    }

    public @Nullable String getPushSecureAuthToken() {
        return sharedPrefs.getString(PUSHSECURE_TOKEN, null);
    }

    public void setPushSecureAuthToken(@NonNull String newToken) {
        editor.putString(PUSHSECURE_TOKEN, newToken).apply();
    }

    public boolean didSendGcmTokenToPushSecure() {
        return sharedPrefs.getBoolean(SENT_GCM_TOKEN_TO_SERVER, false);
    }

    public void setDidSendGcmTokenToPushSecure(boolean didSend) {
        editor.putBoolean(SENT_GCM_TOKEN_TO_SERVER, didSend).apply();
    }

    public void clear() {
        editor.clear().apply();
    }
}
