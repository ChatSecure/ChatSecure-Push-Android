package org.chatsecure.pushsecure.pushsecure;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.chatsecure.pushsecure.pushsecure.response.Account;
import org.chatsecure.pushsecure.pushsecure.response.Device;
import org.chatsecure.pushsecure.pushsecure.response.Message;
import org.chatsecure.pushsecure.pushsecure.response.Token;
import org.chatsecure.pushsecure.pushsecure.response.typeadapter.DjangoDateTypeAdapter;

import java.util.Date;

import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import timber.log.Timber;

/**
 * An API client for the ChatSecure Push Server
 * Created by davidbrodsky on 6/23/15.
 */
public class PushSecureClient {

    private PushSecureApi api;
    private String token;

    public PushSecureClient(@NonNull String apiHost) {
        this(apiHost, null);
    }

    public PushSecureClient(@NonNull String apiHost, @Nullable Account account) {

        if (account != null) token = account.token;

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DjangoDateTypeAdapter())
                .create();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(apiHost)
                .setConverter(new GsonConverter(gson))
                .setRequestInterceptor(request -> {
                    if (token != null) request.addHeader("Authorization", "Token " + token);
                })
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        api = restAdapter.create(PushSecureApi.class);
    }

    public void setAccount(@NonNull Account account) {
        this.token = account.token;
    }

    public Observable<Account> createAccount(@Nullable String email,
                                             @NonNull String username,
                                             @NonNull String password) {

        return api.createAccount(email, username, password);
    }

    public Observable<Device> createDevice(@Nullable String name,
                                           @NonNull String gcmRegistrationId,
                                           @Nullable String gcmDeviceId) {

        return api.createDevice(name, gcmRegistrationId, gcmDeviceId);
    }

    public Observable<Token> createToken(@NonNull String gcmRegistrationId, @Nullable String name) {
        return api.createToken(name, gcmRegistrationId);
    }

    public Observable<Message> sendMessage(@NonNull String recipientToken,
                                           @Nullable String data) {

        return api.sendMessage(recipientToken, data);
    }
}
