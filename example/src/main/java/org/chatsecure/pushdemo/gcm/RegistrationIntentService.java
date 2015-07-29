package org.chatsecure.pushdemo.gcm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.chatsecure.pushdemo.R;

import rx.Observable;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};

    private static PublishSubject<String> gcmTokenSubject = PublishSubject.create();
    private static Observable<String> gcmTokenObservable;

    /**
     * Convenience method to retrieve refreshed GCM token as an {@link Observable}.
     * This will only perform a network request to GCM at most once per process lifecycle
     */
    public static Observable<String> refreshGcmToken(@NonNull Context packageContext) {
        // We expect to receive only one token per process lifecycle so cache the first result
        // for all clients
        if (gcmTokenObservable == null) {
            gcmTokenObservable = gcmTokenSubject.cache(1);

            Intent intent = new Intent(packageContext, RegistrationIntentService.class);
            packageContext.startService(intent);
        }
        return gcmTokenObservable;
    }

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            // In the (unlikely) event that multiple refresh operations occur simultaneously,
            // ensure that they are processed sequentially.
            synchronized (TAG) {
                // [START register_for_gcm]
                // Initially this call goes out to the network to retrieve the token, subsequent calls
                // are local.
                // [START get_token]
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                // [END get_token]
                Log.i(TAG, "GCM Registration Token: " + token);

                // TODO: Implement this method to send any registration to your app's servers.
                if (gcmTokenSubject != null) gcmTokenSubject.onNext(token);

                // Subscribe to topic channels
                //subscribeTopics(token);

                // You should store a boolean that indicates whether the generated token has been
                // sent to your server. If the boolean is false, send the token to your server,
                // otherwise your server should have already received the token.
                //sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true).apply();
                // [END register_for_gcm]
            }
        } catch (Exception e) {
            String errorMessage = "Failed to obtain GCM token";
            Timber.e(e, errorMessage);
            // If an exception happens while fetching the new token or updating our registration data
            // on a third-party server, this ensures that we'll attempt the update at a later time.
            gcmTokenSubject.onError(new IllegalStateException(errorMessage, e));
        }
    }
}