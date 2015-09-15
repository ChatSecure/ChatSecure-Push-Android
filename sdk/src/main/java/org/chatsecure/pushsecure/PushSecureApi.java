package org.chatsecure.pushsecure;

import android.support.annotation.NonNull;

import org.chatsecure.pushsecure.response.Account;
import org.chatsecure.pushsecure.response.Device;
import org.chatsecure.pushsecure.response.DeviceList;
import org.chatsecure.pushsecure.response.Message;
import org.chatsecure.pushsecure.response.PushToken;
import org.chatsecure.pushsecure.response.TokenList;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * API Definition for ChatSecure Push Server Protocol 3.1, Level 1
 *
 * @see <a href="https://github.com/ChatSecure/ChatSecure-Push-Server/tree/master/docs/v3</a>
 * <p>
 * Created by davidbrodsky on 6/23/15.
 */
interface PushSecureApi {

    @POST("accounts/")
    @FormUrlEncoded
    Call<Account> authenticateAccount(@NonNull @Field("username") String username,
                                      @NonNull @Field("password") String password,
                                      @Field("email") String email);

    @POST("device/gcm/")
    @FormUrlEncoded
    Call<Device> createDevice(@NonNull @Field("registration_id") String registrationId,
                              @Field("name") String name,
                              @Field("device_id") String deviceId);

    @PUT("device/gcm/{id}/")
    Call<Device> updateDevice(@NonNull @Path("id") String id,
                              @Body Device device);

    @DELETE("device/gcm/{id}/")
    Call<Void> deleteDevice(@NonNull @Path("id") String id);

    @POST("tokens/")
    @FormUrlEncoded
    Call<PushToken> createToken(@NonNull @Field("gcm_device") String id,
                                @Field("name") String name);

    @GET("tokens/")
    Call<TokenList> getTokens();

    @DELETE("tokens/{token}/")
    Call<Void> deleteToken(@NonNull @Path("token") String token);

    @POST("messages/")
    @FormUrlEncoded
    Call<Message> sendMessage(@NonNull @Field("token") String token,
                              @Field("data") String data);

    @GET("device/gcm/")
    Call<DeviceList> getGcmDevices();

    @GET("device/apns/")
    Call<DeviceList> getApnsDevices();
}
