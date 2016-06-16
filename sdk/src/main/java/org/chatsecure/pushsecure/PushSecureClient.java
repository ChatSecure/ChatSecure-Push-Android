package org.chatsecure.pushsecure;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import org.chatsecure.pushsecure.response.Account;
import org.chatsecure.pushsecure.response.Device;
import org.chatsecure.pushsecure.response.DeviceList;
import org.chatsecure.pushsecure.response.Message;
import org.chatsecure.pushsecure.response.PushToken;
import org.chatsecure.pushsecure.response.TokenList;

import java.io.IOException;
import java.util.Date;

import okio.Buffer;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import timber.log.Timber;

/**
 * An API client for the ChatSecure Push Server
 * Created by davidbrodsky on 6/23/15.
 */
public class PushSecureClient {

    private PushSecureApi api;
    private String token;

    // <editor-fold desc="Public API">

    public PushSecureClient(@NonNull String apiHost) {
        this(apiHost, null);
    }

    public PushSecureClient(@NonNull String apiHost, @Nullable Account account) {

        if (account != null) token = account.token;

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request;

                if (token == null) {
                    request = chain.request();
                } else {
                    // If a ChatSecure-Push auth token has been set, attach that to each request
                    request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", "Token " + token)
                            .build();
                }

                logRequest(request);

                // Perform request
                Response response = chain.proceed(request);

                // Log response. Consuming the Response's body requires us to re-make it for further client consumption
                response = logResponse(response);

                return response;
            }
        });

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new org.chatsecure.pushsecure.response.typeadapter.DjangoDateTypeAdapter())
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiHost)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        api = retrofit.create(PushSecureApi.class);
    }

    public void setAccount(@Nullable Account account) {
        this.token = account != null ? account.token : null;
    }

    /**
     * Authenticate an account with the given credentials, creating one if none exists.
     *
     * @return an {@link Account} representing the newly created or existing account matching
     * the passed credentials. This should be passed to {@link #setAccount(Account)} before
     * performing any other operations with this client.
     */
    public void authenticateAccount(@NonNull String username,
                                    @NonNull String password,
                                    @Nullable String email,
                                    @NonNull RequestCallback<Account> callback) {

        api.authenticateAccount(username, password, email)
                .enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void createDevice(@NonNull String gcmRegistrationId,
                             @Nullable String name,
                             @Nullable String gcmDeviceId,
                             @NonNull RequestCallback<Device> callback) {

        api.createDevice(gcmRegistrationId, name, gcmDeviceId)
                .enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void createToken(@NonNull Device device,
                            @Nullable String name,
                            @NonNull RequestCallback<PushToken> callback) {

        api.createToken(device.id, name).enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void deleteToken(@NonNull String token, @NonNull RequestCallback<Void> callback) {

        api.deleteToken(token).enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void getTokens(@NonNull RequestCallback<TokenList> callback) {

        api.getTokens().enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void sendMessage(@NonNull String recipientToken,
                            @Nullable String data,
                            @NonNull String providerUrl,
                            @NonNull RequestCallback<Message> callback) {

        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request;

                if (token == null) {
                    request = chain.request();
                } else {
                    // If a ChatSecure-Push auth token has been set, attach that to each request
                    request = chain.request()
                            .newBuilder()
                            .addHeader("Authorization", "Token " + token)
                            .build();
                }

                logRequest(request);

                // Perform request
                Response response = chain.proceed(request);

                // Log response. Consuming the Response's body requires us to re-make it for further client consumption
                response = logResponse(response);

                return response;
            }
        });

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new org.chatsecure.pushsecure.response.typeadapter.DjangoDateTypeAdapter())
                .create();

        if (providerUrl.endsWith("/messages/"))
            providerUrl = providerUrl.replace("/messages/","/");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(providerUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        PushSecureApi api = retrofit.create(PushSecureApi.class);
        api.sendMessage(recipientToken, data).enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void getGcmDevices(@NonNull RequestCallback<DeviceList> callback) {

        api.getGcmDevices().enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void getApnsDevices(@NonNull RequestCallback<DeviceList> callback) {

        api.getApnsDevices().enqueue(new RetrofitCallbackBridge<>(callback));
    }

//    public Observable<List<Device>> getAllDevices() {
//        return Observable.concat(
//                getGcmDevices()
//                        .flatMap(gcmDeviceList -> Observable.from(gcmDeviceList.results))
//                        .map(gcmDevice -> new Device(gcmDevice, Device.Type.GCM)),
//                getApnsDevices()
//                        .flatMap(apnsDeviceList -> Observable.from(apnsDeviceList.results))
//                        .map(apnsDevice -> new Device(apnsDevice, Device.Type.APNS)))
//                .toList();
//    }

    /**
     * Update properties of the current device. Note that changes to {@link Device#id} will
     * not be respected
     */
    public void updateDevice(@NonNull Device device, @NonNull RequestCallback<Device> callback) {

        api.updateDevice(device.id, device).enqueue(new RetrofitCallbackBridge<>(callback));
    }

    public void deleteDevice(@NonNull String id, @NonNull RequestCallback<Void> callback) {

        api.deleteDevice(id).enqueue(new RetrofitCallbackBridge<>(callback));
    }

    // </editor-fold desc="Public API">

    private void logRequest(Request request) {
        StringBuilder builder = new StringBuilder();
        builder.append(request.method())
                .append(" ")
                .append(request.httpUrl())
                .append("\n")
                .append(request.headers().toString());

        if (request.body() != null) {
            try {
                final Request copy = request.newBuilder().build();
                final Buffer buffer = new Buffer();
                copy.body().writeTo(buffer);
                builder.append(buffer.readUtf8());
            } catch (final IOException e) {
                Timber.e(e, "Failed to log request body");
            }
        }

        //Timber.d("Request -> " + builder.toString());
    }

    private
    @Nullable
    Response logResponse(Response response) {
        StringBuilder builder = new StringBuilder();
        builder.append(response.code())
                .append(" ")
                .append(response.request().httpUrl())
                .append("\n")
                .append(response.headers().toString())
                .append("\n");

        if (response.body() != null) {
            try {
                final Response copy = response.newBuilder().build();
                String responseBodyString = copy.body().string();
                builder.append(responseBodyString);
                copy.body().close();
                Response newResponse = response.newBuilder().body(ResponseBody.create(copy.body().contentType(), responseBodyString.getBytes())).build();
               // Timber.d("Response <- " + builder.toString());
                return newResponse;
            } catch (IOException e) {
                Timber.e(e, "Failed to read response body");
            }
        }
        return response;
    }

    /**
     * A bridging callback that eliminates some of the boilerplate from
     * {@link Callback}
     */
    private class RetrofitCallbackBridge<T> implements Callback<T> {

        private RequestCallback<T> callback;

        RetrofitCallbackBridge(RequestCallback<T> callback) {
            this.callback = callback;
        }

        @Override
        public void onResponse(retrofit.Response<T> response) {
            if (200 <= response.code() && response.code() < 300) {
                callback.onSuccess(response.body());
            } else {
                // TODO : Find out what these look like
                try {
                    if (response.errorBody().contentLength() > 0) {
                        callback.onFailure(new RequestException(response.raw(), response.errorBody().string()));
                    } else {
                        callback.onFailure(new RequestException(response.raw(), "A server error ocurred"));
                    }
                } catch (IOException e) {
                    Timber.e(e, "Failed to report error response reason");
                    callback.onFailure(new RequestException(response.raw(), "A server error ocurred"));
                }
            }
        }

        @Override
        public void onFailure(Throwable t) {
            callback.onFailure(t);
        }
    }

    public interface RequestCallback<T> {

        void onSuccess(@NonNull T response);

        void onFailure(@NonNull Throwable t);
    }

    public class RequestException extends Throwable {

        private Response response;

        public RequestException(Response response, String detailMessage) {
            super(detailMessage);
            this.response = response;
        }

        public Response getResponse() {
            return response;
        }
    }
}
