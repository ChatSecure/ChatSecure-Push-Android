package org.chatsecure.pushsecure.pushsecure;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.chatsecure.pushsecure.pushsecure.response.Account;
import org.chatsecure.pushsecure.pushsecure.response.Device;
import org.chatsecure.pushsecure.pushsecure.response.PushToken;
import org.chatsecure.pushsecure.pushsecure.response.Message;

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
interface PushSecureApi {

    @POST("/api/accounts/")
    @FormUrlEncoded
    Observable<Account> authenticateAccount(@NonNull @Field("username") String username,
                                            @NonNull @Field("password") String password,
                                            @Nullable @Field("email") String email);

    @POST("/api/device/gcm/")
    @FormUrlEncoded
    Observable<Device> createDevice(@Nullable @Field("name") String name,
                                    @NonNull @Field("registration_id") String registrationId,
                                    @Nullable @Field("device_id") String deviceId);

    @POST("/api/tokens/")
    @FormUrlEncoded
    Observable<PushToken> createToken(@Nullable @Field("name") String name,
                                      @Field("gcm_device") String registrationId);

    @POST("/api/messages/")
    @FormUrlEncoded
    Observable<Message> sendMessage(@NonNull @Field("token") String token,
                                    @Nullable @Field("data") String data);

    @GET("/api/device/gcm/")
    Observable<Response> getDevices();
}
