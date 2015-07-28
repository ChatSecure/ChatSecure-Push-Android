package org.chatsecure.pushsecure;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.chatsecure.pushsecure.response.Account;
import org.chatsecure.pushsecure.response.Device;
import org.chatsecure.pushsecure.response.DeviceList;
import org.chatsecure.pushsecure.response.Message;
import org.chatsecure.pushsecure.response.PushToken;
import org.chatsecure.pushsecure.response.TokenList;

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;

/**
 * API Definition for ChatSecure Push Server Protocol 3.1, Level 1
 *
 * @see <a href="https://github.com/ChatSecure/ChatSecure-Push-Server/tree/master/docs/v3</a>
 * <p>
 * Created by davidbrodsky on 6/23/15.
 */
interface PushSecureApi {

    @POST("/accounts/")
    @FormUrlEncoded
    Observable<Account> authenticateAccount(@NonNull @Field("username") String username,
                                            @NonNull @Field("password") String password,
                                            @Nullable @Field("email") String email);

    @POST("/device/gcm/")
    @FormUrlEncoded
    Observable<Device> createDevice(@Nullable @Field("name") String name,
                                    @NonNull @Field("registration_id") String registrationId,
                                    @Nullable @Field("device_id") String deviceId);

    @PUT("/device/gcm/{registrationId}/")
    Observable<Device> updateDevice(@NonNull @Path("registrationId") String registrationId,
                                    @Body Device device);

    @DELETE("/device/gcm/{registrationId}/")
    Observable<Response> deleteDevice(@NonNull @Path("registrationId") String registrationId);

    @POST("/tokens/")
    @FormUrlEncoded
    Observable<PushToken> createToken(@Nullable @Field("name") String name,
                                      @Field("gcm_device") String registrationId);

    @GET("/tokens/")
    Observable<TokenList> getTokens();

    @DELETE("/tokens/{token}/")
    Observable<Response> deleteToken(@NonNull @Path("token") String token);

    @POST("/messages/")
    @FormUrlEncoded
    Observable<Message> sendMessage(@NonNull @Field("token") String token,
                                    @Nullable @Field("data") String data);

    @GET("/device/gcm/")
    Observable<DeviceList> getGcmDevices();

    @GET("/device/apns/")
    Observable<DeviceList> getApnsDevices();
}
