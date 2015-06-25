package org.chatsecure.pushdemo.pushsecure;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.chatsecure.pushdemo.pushsecure.response.CreateAccountResponse;
import org.chatsecure.pushdemo.pushsecure.response.CreateDeviceResponse;
import org.chatsecure.pushdemo.pushsecure.response.CreateTokenResponse;
import org.chatsecure.pushdemo.pushsecure.response.SendMessageResponse;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * API Definition for ChatSecure Push Server Protocol 3.1, Level 1
 *
 * @see <a href="https://github.com/ChatSecure/ChatSecure-Push-Server/tree/master/docs/v3</a>
 * <p>
 * Created by davidbrodsky on 6/23/15.
 */
interface PushSecureService {

    @POST("/api/accounts/")
    @FormUrlEncoded
    Observable<CreateAccountResponse> createAccount(@Nullable @Field("email") String email,
                                                    @NonNull @Field("username") String username,
                                                    @NonNull @Field("password") String password);

    @POST("/api/device/gcm/")
    @FormUrlEncoded
    Observable<CreateDeviceResponse> createDevice(@Nullable @Field("name") String name,
                                                  @NonNull @Field("registration_id") String registrationId,
                                                  @Nullable @Field("device_id") String deviceId);

    @POST("/api/tokens/")
    @FormUrlEncoded
    Observable<CreateTokenResponse> createToken(@Nullable @Field("name") String name,
                                                @Field("gcm_device") String registrationId);

    @POST("/api/messages/")
    @FormUrlEncoded
    Observable<SendMessageResponse> sendMessage(@NonNull @Field("token") String token,
                                                @Nullable @Field("data") String data);

    @GET("/api/device/gcm/")
    Observable<Response> getDevices();
}
