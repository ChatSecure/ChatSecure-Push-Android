package org.chatsecure.pushsecure.pushsecure.response;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by davidbrodsky on 6/24/15.
 */
public class Account {

    public final @NonNull String username;
    public final @Nullable String email;
    public final @NonNull String token;

    public Account(@NonNull String username, @Nullable String email, @NonNull String token) {
        this.username = username;
        this.email = email;
        this.token = token;
    }
}
