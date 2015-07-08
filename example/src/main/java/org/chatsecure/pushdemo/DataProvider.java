package org.chatsecure.pushdemo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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
    private static final String GCM_TOKEN = "gcmToken";                            // String

    private static final String SHARED_PREFS_NAME = DataProvider.class.getSimpleName();

    private SharedPreferences sharedPrefs;
    private SharedPreferences.Editor editor;

    public DataProvider(@NonNull Context context) {
        sharedPrefs = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();
    }

    public @Nullable String getGcmToken() {
        return sharedPrefs.getString(GCM_TOKEN, null);
    }

    public void setGcmToken(@NonNull String gcmToken) {
        editor.putString(GCM_TOKEN, gcmToken).apply();
    }

    public @Nullable String getPushSecureUsername() {
        return sharedPrefs.getString(PUSHSECURE_UNAME, null);
    }

    public void setPushSecureUsername(@NonNull String username) {
        editor.putString(PUSHSECURE_UNAME, username).apply();
    }

    public @Nullable String getPushSecureAuthToken() {
        return sharedPrefs.getString(GCM_TOKEN, null);
    }

    public void savePushSecureAuthToken(@NonNull String newToken) {
        editor.putString(GCM_TOKEN, newToken).apply();
    }

    public boolean didSendGcmTokenToPushSecure() {
        return sharedPrefs.getBoolean(SENT_GCM_TOKEN_TO_SERVER, false);
    }

    public void setDidSendGcmTokenToPushSecure(boolean didSend) {
        editor.putBoolean(SENT_GCM_TOKEN_TO_SERVER, didSend).apply();
    }
}
