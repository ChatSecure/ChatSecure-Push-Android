/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.chatsecure.pushdemo;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.chatsecure.pushdemo.gcm.GcmService;
import org.chatsecure.pushdemo.gcm.RegistrationIntentService;
import org.chatsecure.pushdemo.ui.fragment.DevicesFragment;
import org.chatsecure.pushdemo.ui.fragment.MessagingFragment;
import org.chatsecure.pushdemo.ui.fragment.RegistrationFragment;
import org.chatsecure.pushdemo.ui.fragment.TokensFragment;
import org.chatsecure.pushsecure.PushSecureClient;
import org.chatsecure.pushsecure.response.Account;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements RegistrationFragment.AccountRegistrationListener, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.drawer)
    DrawerLayout drawer;

    @Bind(R.id.navigation)
    NavigationView navigation;

    @Bind(R.id.container)
    FrameLayout container;

    @Bind(R.id.nameTextView)
    TextView name;

    @Bind(R.id.signOutButton)
    Button signOut;

    private org.chatsecure.pushsecure.PushSecureClient client;
    private DataProvider dataProvider;

    private PublishSubject<Account> newAccountObservable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        navigation.setNavigationItemSelectedListener(this);

        signOut.setOnClickListener(this);

        if (checkPlayServices()) {

            dataProvider = new DataProvider(this);

            client = new PushSecureClient("https://chatsecure-push.herokuapp.com/api/v1/");
            //client = new PushSecureClient("http://10.11.41.186:8000/api/v1/");

            register();
        }
        processIntent(getIntent());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        processIntent(intent);
    }

    private void processIntent(Intent intent) {
        if (intent.getAction().equals(GcmService.REVOKE_TOKEN_ACTION)) {
            handleRevokeTokenIntent(intent);
        }
    }

    private void setContentFragment(Fragment fragment, int titleResId) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();

        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setTitle(titleResId);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void handleRevokeTokenIntent(Intent intent) {
        client.deleteToken(intent.getStringExtra(GcmService.TOKEN_EXTRA), new PushSecureClient.RequestCallback<Void>() {
            @Override
            public void onSuccess(Void response) {
                Timber.d("Delete token!");
                handleRevokeTokenIntentProcessed(intent);
            }

            @Override
            public void onFailure(Throwable t) {
                Timber.e(t, "Failed to delete token");
                handleRevokeTokenIntentProcessed(intent);
            }
        });
    }

    private void handleRevokeTokenIntentProcessed(Intent intent) {
        dismissNotification(intent.getIntExtra(GcmService.NOTIFICATION_ID_EXTRA, 1));
        Snackbar.make(container, getString(R.string.revoked_token), Snackbar.LENGTH_SHORT).show();
    }

    private void dismissNotification(int id) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }

    private void register() {
        Registration.register(RegistrationIntentService.refreshGcmToken(this),
                client,
                dataProvider, () -> {
                    // Registration needed
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                    setContentFragment(RegistrationFragment.newInstance(client), R.string.registration);
                    hideActionBar();

                    newAccountObservable = PublishSubject.create();
                    return newAccountObservable;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pushSecureClient -> {
                    Timber.d("Registered");
                    name.setText(dataProvider.getPushSecureUsername());
                    drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    // Show a "Create / Share Whitelist token" Fragment
                    setContentFragment(MessagingFragment.newInstance(pushSecureClient, dataProvider), R.string.messaging);
                }, throwable -> {
                    Timber.e(throwable, "Failed to register device!");
                });
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Timber.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onAccountCreated(Account account) {
        // PushSecureRegistration doesn't need the ChatSecure Push username
        // so our app is responsible for persisting it in order to personalize our UI
        dataProvider.setPushSecureUsername(account.username);

        // Notify PushSecureRegistration
        if (newAccountObservable != null) {
            newAccountObservable.onNext(account);

            // Change this when we support the user changing their account in-app
            newAccountObservable.onCompleted();
        }

    }

    /** Navigation drawer item selection */
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.my_tokens:
                setContentFragment(TokensFragment.newInstance(client, dataProvider), R.string.my_tokens);
                break;

            case R.id.my_devices:
                setContentFragment(DevicesFragment.newInstance(client, dataProvider), R.string.my_devices);
                break;

            case R.id.messaging:
                setContentFragment(MessagingFragment.newInstance(client, dataProvider), R.string.messaging);
                break;
        }

        menuItem.setChecked(true);
        drawer.closeDrawers();
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v.equals(signOut)) {
            dataProvider.clear();
            client.setAccount(null);
            register();
            drawer.closeDrawers();
        }
    }

    private void hideActionBar() {
        ActionBar actionBar;
        if ((actionBar = getSupportActionBar()) !=  null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }
    }
}