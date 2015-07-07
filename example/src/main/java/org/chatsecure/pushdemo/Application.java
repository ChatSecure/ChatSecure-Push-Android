package org.chatsecure.pushdemo;

import timber.log.Timber;

/**
 * Created by davidbrodsky on 6/23/15.
 */
public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }
}
